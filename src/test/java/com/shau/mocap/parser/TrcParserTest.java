package com.shau.mocap.parser;

import com.shau.mocap.domain.Joint;
import com.shau.mocap.exception.ParserException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

/* functional tests with known input*/

public class TrcParserTest {

    private List<String> lines;

    @Before
    public void initTests() throws IOException {
        Path testFilePath = Paths.get("src","test", "resources", "Dog_Run.trc");
        lines = Files.readAllLines(testFilePath);
    }

    @Test
    public void testValidHeaderAndReturnData_Success() throws Exception {
        assertThat(lines.size(), is(566));
        //header is stripped in validation
        List<String> data = TrcParser.validateHeaderAndReturnMocapData(lines);
        assertThat(data.size(), is(560));
    }

    @Test(expected = ParserException.class)
    public void testTooFewLinesInHeader() throws Exception {
        try {
            //header size ok
            TrcParser.validateHeaderAndReturnMocapData(lines.subList(0, 6));
        } catch (Exception pe) {
            fail("Not expecting error in validating header data");
        }

        //reduced header not ok
        TrcParser.validateHeaderAndReturnMocapData(lines.subList(0, 5));
    }

    @Test(expected = ParserException.class)
    public void testInvalidHeaderFirstLine() throws Exception {
        try {
            //header data ok
            TrcParser.validateHeaderAndReturnMocapData(lines.subList(0, 6));
        } catch (Exception pe) {
            fail("Not expecting error in validating header data");
        }

        //bad first line
        lines.set(0, "NOT PathFileType");
        TrcParser.validateHeaderAndReturnMocapData(lines.subList(0, 6));
    }

    @Test(expected = ParserException.class)
    public void testInvalidHeaderFifthLine() throws Exception {
        try {
            //header data ok
            TrcParser.validateHeaderAndReturnMocapData(lines.subList(0, 6));
        } catch (Exception pe) {
            fail("Not expecting error in validating header data");
        }

        //bad 5th line
        lines.set(5, "NOT empty");
        TrcParser.validateHeaderAndReturnMocapData(lines.subList(0, 6));
    }

    @Test
    public void parseFrameDataSuccess() throws Exception {
        List<String> data = TrcParser.validateHeaderAndReturnMocapData(lines);
        assertThat(data.size(), is(560));

        String frame = data.get(0);
        try {
            List<Double> frameData = TrcParser.parseFrameData(frame);
            assertThat(frameData.size(), is(558));
        } catch (Exception e) {
            fail("Parse frame should not fail");
        }
    }

    @Test(expected = ParserException.class)
    public void parseInvalidFrameData() throws Exception {
        List<String> data = TrcParser.validateHeaderAndReturnMocapData(lines);
        assertThat(data.size(), is(560));

        //throw exception for invalid frame data
        String frame = "";
        List<Double> frameData = TrcParser.parseFrameData(frame);
    }

    @Test
    public void parseFrameIdSuccess() throws Exception {
        List<String> data = TrcParser.validateHeaderAndReturnMocapData(lines);
        assertThat(data.size(), is(560));

        String frame = data.get(0);
        List<Double> frameData = new ArrayList<>();
        try {
            frameData = TrcParser.parseFrameData(frame);
        } catch (Exception e) {
            fail("Parse frame should not fail");
        }
        assertThat(frameData.size(), is(558));

        int frameId = 0;
        try {
            frameId = TrcParser.parseFrameId(frameData);
        } catch (Exception e) {
            fail("Parse Frame ID should not fail");
        }
        assertThat(frameId, is(1));
    }

    @Test(expected = ParserException.class)
    public void parseFrameIdInvalidData() throws Exception {
        TrcParser.parseFrameId(Collections.EMPTY_LIST);
    }

    @Test
    public void parseJointDataSuccess() throws Exception {
        List<String> data = TrcParser.validateHeaderAndReturnMocapData(lines);
        assertThat(data.size(), is(560));

        String frame = data.get(0);
        List<Double> frameData = new ArrayList<>();
        try {
            frameData = TrcParser.parseFrameData(frame);
        } catch (Exception e) {
            fail("Parse frame should not fail");
        }
        assertThat(frameData.size(), is(558));

        List<Joint> jointData = new ArrayList<>();
        try {
            jointData = TrcParser.parseJoints(frameData);
        } catch (Exception e) {
            fail("Parse joint data should not fail");
        }
        assertThat(jointData.size(), is(185));
    }

    @Test
    public void parseInvalidJointDataSuccess() throws Exception {
        List<String> data = TrcParser.validateHeaderAndReturnMocapData(lines);
        assertThat(data.size(), is(560));

        String frame = data.get(0);
        List<Double> frameData = new ArrayList<>();
        try {
            frameData = TrcParser.parseFrameData(frame);
        } catch (Exception e) {
            fail("Parse frame should not fail");
        }
        assertThat(frameData.size(), is(558));

        //try with truncated list
        List<Joint> jointData = new ArrayList<>();
        try {
            jointData = TrcParser.parseJoints(frameData.subList(0,2));
        } catch (Exception e) {
            fail("Parse joint data should not fail");
        }
        assertThat(jointData.size(), is(0));

        //try with empty list
        try {
            jointData = TrcParser.parseJoints(Collections.EMPTY_LIST);
        } catch (Exception e) {
            fail("Parse joint data should not fail");
        }
        assertThat(jointData.size(), is(0));
    }
}