package com.shau.mocap.parser.c3d;

import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.c3d.domain.C3dGroup;
import com.shau.mocap.parser.c3d.domain.C3dHeader;
import com.shau.mocap.parser.c3d.domain.C3dParameter;
import com.shau.mocap.parser.c3d.domain.C3dValue;
import com.shau.mocap.util.BinaryHelper;
import com.shau.mocap.parser.util.DataIndex;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/* functional tests with known input*/

public class C3dDataParserTest {

    private FileInputStream fis;

    @Before
    public void initTests() throws FileNotFoundException {
        Path testFilePath = Paths.get("src","test", "resources", "60_12.c3d");
        fis = new FileInputStream(testFilePath.toFile());
    }

    @Test(expected = ParserException.class)
    public void testParseAndValidateHeaderInvalid() throws Exception {
        byte[] headerBlock = C3dDataLoader.loadHeaderBlock(fis);
        headerBlock[1] = 99;
        C3dDataParser.parseAndValidateHeader(headerBlock);
    }

    @Test
    public void testParseSuccess() throws Exception {

        byte[] headerBlock = C3dDataLoader.loadHeaderBlock(fis);
        int parameterStartLocation = C3dDataParser.parseAndValidateHeader(headerBlock);
        assertThat(parameterStartLocation, is(2));

        byte[] parameterMetaDataBlock = C3dDataLoader.loadParameterMetadata(fis, parameterStartLocation);
        int numberOfParameters = C3dDataParser.parseNoOfParameters(parameterMetaDataBlock);
        assertThat(numberOfParameters, is(17));

        byte[] parametersBlock = C3dDataLoader.loadParameters(fis, parameterStartLocation, numberOfParameters);
        int processorType = C3dDataParser.parseProcessorType(parametersBlock);
        assertThat(processorType, is(84));
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
        assertThat(groups.size(), is(8));

        C3dHeader header = C3dDataParser.parseHeader(headerBlock, byteOrder);
        assertThat(header.getAnalogueMeasurementsPerFrame(), is(0));
        assertThat(header.getAnalogueSamplesPerFrame(), is(1));
        assertThat(header.getScale(), is(-0.05f));
        assertThat(header.getDataStartBlock(), is(19));
        assertThat(header.getFrameRate(), is(60.0f));
        assertThat(header.getMaxInterpolationGap(), is(0));
        assertThat(header.getNumberOfPoints(), is(82));
        assertThat(header.getRawFirstFrame(), is(172));
        assertThat(header.getRawLastFrame(), is(1861));

        C3dValueMetadata valueMetadata = new C3dValueMetadata(groups);
        assertThat(valueMetadata.getAnalogChannels(), is(0));
        assertThat(valueMetadata.getAnalogSamples(), is(0));
        assertThat(valueMetadata.getScale(), is(-0.05f));
        assertThat(valueMetadata.getFrames(), is(1690));
        assertThat(valueMetadata.getSize(), is(4));
        assertThat(valueMetadata.getBlocks(), is(4331));
        assertThat(valueMetadata.getDataStart(), is(19));
        assertThat(valueMetadata.getPointRate(), is(60.0f));
        assertThat(valueMetadata.getPoints(), is(82));
        assertThat(valueMetadata.getPointsSize(), is(1312));

        byte[] dataBlock = C3dDataLoader.loadData(fis, valueMetadata);
        List<C3dValue> dataValues = C3dDataParser.parseDataValues(dataBlock, valueMetadata, byteOrder);
        assertThat(dataValues.size(), is(1690));
    }
}