package com.shau.mocap.parser.c3d;

import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.c3d.domain.*;
import com.shau.mocap.util.BinaryHelper;
import com.shau.mocap.parser.util.DataIndex;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class C3dDataParser {

    public static int parseAndValidateHeader(byte[] headerBlock)
            throws ParserException {

        int parameterStartLocation = headerBlock[0];
        if (headerBlock[1] != 80) {
            throw new ParserException("Not a valid c3d header format");
        }

        return parameterStartLocation;
    }

    public static int parseNoOfParameters(byte[] parametersMetadataBlock) {
        //parameterBlock[0] reserved 1
        //parameterBlock[1] reserved 2
        return parametersMetadataBlock[2];
    }

    public static int parseProcessorType(byte[] parametersBlock) {
        return parametersBlock[3];
    }

    public static DataIndex parseGroupParameterData(byte[] parametersBlock, int idx, ByteOrder byteOrder) throws ParserException {

        try {
            boolean locked = false;

            int nameLength = parametersBlock[idx++];
            if (nameLength < 0) {
                locked = true;
                nameLength = Math.abs(nameLength);
            }

            int groupId = parametersBlock[idx++];
            if (groupId == 0) {
                throw new ParserException("End parsing token detected", ParserException.STOP_PARSING_EXCEPTION);
            }

            String name = "";
            for (int i = 0; i < nameLength; i++) {
                name += (char) parametersBlock[idx++];
            }

            if (groupId < 0) {

                //group
                groupId = Math.abs(groupId);

                int next = idx;
                byte[] word = new byte[]{parametersBlock[idx++], parametersBlock[idx++]};
                int increment = BinaryHelper.readInt16(word, byteOrder);
                if (increment == 0) {
                    throw new ParserException("No group data token detected", ParserException.STOP_PARSING_EXCEPTION);
                }
                next += increment;

                int descriptionLength = parametersBlock[idx++];
                String description = "";
                for (int i = 0; i < descriptionLength; i++) {
                    description += (char) parametersBlock[idx++];
                }

                C3dGroup group = new C3dGroup(groupId, name, description);
                return new DataIndex<>(group, next);

            } else {

                //parameter

                int next = idx;
                byte[] word = new byte[] {parametersBlock[idx++], parametersBlock[idx++]};
                int increment = BinaryHelper.readInt16(word, byteOrder);
                if (increment == 0) {
                    throw new ParserException("No parameter data token detected", ParserException.STOP_PARSING_EXCEPTION);
                }
                next += increment;

                //-1 character
                // 1 byte data
                // 2 16 bit int
                // 4 float
                int elementType = parametersBlock[idx++];
                int elementLength = C3dParameterHelper.parameterSize(elementType);
                int numberOfDimensions = parametersBlock[idx++];
                int[] dimensionSizes = new int[numberOfDimensions];

                List<Object> values = new ArrayList<>();
                if (numberOfDimensions > 0) {
                    //arrayed  data
                    int iterations =  1;
                    for (int i = 0; i < numberOfDimensions; i++) {
                        dimensionSizes[i] = parametersBlock[idx++];
                        iterations *= dimensionSizes[i];
                    }

                    for (int i = 0; i <iterations; i++) {
                        byte[] value = new byte[elementLength];
                        for (int j = 0; j < elementLength; j++) {
                            value[j] = parametersBlock[idx++];
                        }
                        values.add(C3dParameterHelper.create(elementType, value, byteOrder));
                    }

                } else {
                    //scalar
                    byte[] value = new byte[elementLength];
                    for (int j = 0; j < elementLength; j++) {
                        value[j] = parametersBlock[idx++];
                    }
                    values.add(C3dParameterHelper.create(elementType, value, byteOrder));
                }

                int descriptionLength = parametersBlock[idx++];
                String description = "";
                for (int i = 0; i < descriptionLength; i++) {
                    description += (char) parametersBlock[idx++];
                }

                C3dParameter parameter = new C3dParameter(name,
                                description,
                                groupId,
                                locked,
                                numberOfDimensions,
                                dimensionSizes,
                                values);
                return new DataIndex(parameter, next);
            }
        } catch (Exception e) {
            if (e instanceof ParserException)
                throw e;
            throw new ParserException("Error parsing Group Parameter data: " + e.getMessage());
        }
    }

    public static C3dHeader parseHeader(byte[] headerBlock, ByteOrder byteOrder) {

        int numberOfPoints = BinaryHelper.readInt16(Arrays.copyOfRange(headerBlock, 2,4), byteOrder);
        int  analogueMeasurementsPerFrame = BinaryHelper.readInt16(Arrays.copyOfRange(headerBlock, 4,6), byteOrder);
        int rawFirstFrame = BinaryHelper.readInt16(Arrays.copyOfRange(headerBlock, 6,8), byteOrder);
        int rawLastFrame = BinaryHelper.readInt16(Arrays.copyOfRange(headerBlock, 8,10), byteOrder);
        int maxInterpolationGap = BinaryHelper.readInt16(Arrays.copyOfRange(headerBlock, 10,12), byteOrder);
        float scale = BinaryHelper.readFloat(Arrays.copyOfRange(headerBlock, 12,16), byteOrder);
        int dataStartBlock = BinaryHelper.readInt16(Arrays.copyOfRange(headerBlock, 16,18), byteOrder);
        int analogueSamplesPerFrame = BinaryHelper.readInt16(Arrays.copyOfRange(headerBlock, 18,20), byteOrder);
        float frameRate = BinaryHelper.readFloat(Arrays.copyOfRange(headerBlock, 20,24), byteOrder);

        return new C3dHeader(numberOfPoints,
                analogueMeasurementsPerFrame,
                rawFirstFrame,
                rawLastFrame,
                maxInterpolationGap,
                scale,
                dataStartBlock,
                analogueSamplesPerFrame,
                frameRate);
    }

    public static List<C3dValue> parseDataValues(byte[] dataBlock, C3dValueMetadata metadata, ByteOrder byteOrder)
            throws ParserException {

        List<C3dValue> dataValues = new ArrayList<>();

        int idx = 0;
        for (int i = 0; i < metadata.getFrames(); i++) {
            List<C3dSpatialPoint> spatialPoints = new ArrayList<>();
            for (int j = 0; j < metadata.getPoints(); j++)  {
                byte[] point = new byte[metadata.getSize() * 4];
                for (int k = 0; k < metadata.getSize() * 4; k++)  {
                    point[k] = dataBlock[idx++];
                }
                spatialPoints.add(new C3dSpatialPoint(point, byteOrder, Math.abs(metadata.getScale())));
            }
            List<Float> analogValues = new ArrayList<>();
            for (int j = 0; j < metadata.getAnalogChannels() * metadata.getAnalogSamples(); j++) {
                byte[] analogValue = new byte[metadata.getSize()];
                for (int k = 0; k < metadata.getSize(); k++) {
                    analogValue[k] = dataBlock[idx++];
                }
                if (metadata.getScale() > 0)  {
                    analogValues.add(metadata.getScale() * (float) BinaryHelper.readInt16(analogValue, byteOrder));
                } else {
                    analogValues.add(Math.abs(metadata.getScale()) * BinaryHelper.readFloat(analogValue, byteOrder));
                }
            }
            dataValues.add(new C3dValue(spatialPoints, analogValues));
        }

        return dataValues;
    }
}
