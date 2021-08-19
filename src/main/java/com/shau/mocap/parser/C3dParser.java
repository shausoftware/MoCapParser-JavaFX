package com.shau.mocap.parser;

import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.c3d.C3dDataLoader;
import com.shau.mocap.parser.c3d.C3dDataParser;
import com.shau.mocap.parser.c3d.C3dValueMetadata;
import com.shau.mocap.parser.c3d.domain.C3dGroup;
import com.shau.mocap.parser.c3d.domain.C3dHeader;
import com.shau.mocap.parser.c3d.domain.C3dParameter;
import com.shau.mocap.parser.c3d.domain.C3dValue;
import com.shau.mocap.parser.util.BinaryHelper;
import com.shau.mocap.parser.util.DataIndex;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class C3dParser {

    public static List<C3dValue> parseData(FileInputStream fis) throws IOException, ParserException {

        byte[] headerBlock = C3dDataLoader.loadHeaderBlock(fis);
        int parameterStartLocation = C3dDataParser.parseAndValidateHeader(headerBlock);

        byte[] parameterMetaDataBlock = C3dDataLoader.loadParameterMetadata(fis, parameterStartLocation);
        int numberOfParameters = C3dDataParser.parseNoOfParameters(parameterMetaDataBlock);

        byte[] parametersBlock = C3dDataLoader.loadParameters(fis, parameterStartLocation, numberOfParameters);
        int processorType = C3dDataParser.parseProcessorType(parametersBlock);
        ByteOrder byteOrder = BinaryHelper.getEndian(processorType);

        Map<Integer, C3dGroup> groups = new HashMap<>();
        int idx = 4;
        while (idx > 0) {
            try {
                DataIndex currentIdx = C3dDataParser.parseGroupParameterData(parametersBlock, idx, byteOrder);
                idx = currentIdx.getIdx();
                if (currentIdx.getGeneratedObject() instanceof C3dGroup) {
                    C3dGroup group = (C3dGroup) currentIdx.getGeneratedObject();
                    groups.put(group.getId(), group);
                } else if (currentIdx.getGeneratedObject() instanceof C3dParameter) {
                    C3dParameter parameter = (C3dParameter) currentIdx.getGeneratedObject();
                    groups.get(parameter.getGroupId()).addParameter(parameter);
                }
            } catch (ParserException pe) {
                if (pe.getExceptionType() == ParserException.STOP_PARSING_EXCEPTION) {
                    idx = -1;
                } else {
                    throw pe;
                }
            }
        }

        C3dHeader header = C3dDataParser.parseHeader(headerBlock, byteOrder);
        C3dValueMetadata valueMetadata = new C3dValueMetadata(groups);
        byte[] dataBlock = C3dDataLoader.loadData(fis, valueMetadata);
        List<C3dValue> dataValues = C3dDataParser.parseDataValues(dataBlock, valueMetadata, byteOrder);

        return dataValues;
    }
}
