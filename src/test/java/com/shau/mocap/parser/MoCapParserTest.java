package com.shau.mocap.parser;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.exception.ParserException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

public class MoCapParserTest {

    @Test
    public void testParseTrcSuccess() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "Dog_Run.trc");
        MoCapScene moCapScene = null;
        try {
            moCapScene = MoCapParser.parseTrcFile(testFilePath.toAbsolutePath().toString());
        } catch (Exception e) {
            fail("Should not fail parsing TRC");
        }
        assertThat(moCapScene.getFrames(), is(notNullValue()));
        assertThat(moCapScene.getFrames().size(), is(560));
    }

    @Test(expected = IOException.class)
    public void testParseTrcInvalidFilePath() throws Exception {
        MoCapParser.parseTrcFile("");
    }

    @Test
    public void testParseC3dSuccess() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "60_12.c3d");
        MoCapScene moCapScene = null;
        try {
            moCapScene = MoCapParser.parseC3dFile(testFilePath.toAbsolutePath().toString());
        } catch (Exception e) {
            fail("Should not fail parsing C3D");
        }
        assertThat(moCapScene.getFrames(), is(notNullValue()));
        assertThat(moCapScene.getFrames().size(), is(1690));

        Frame frame = moCapScene.getFrames().get(0);
        assertThat(frame.getJoints().size(), is(82));
    }

    @Test
    public void testParseNoTruncationSuccess() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "60_12.c3d");
        MoCapScene moCapScene = null;
        try {
            moCapScene = MoCapParser.parse(testFilePath.toAbsolutePath().toString());
        } catch (Exception e) {
            fail("Should not fail parsing C3D");
        }
        assertThat(moCapScene.getFrames(), is(notNullValue()));
        assertThat(moCapScene.getFrames().size(), is(1690));

        Frame frame = moCapScene.getFrames().get(0);
        assertThat(frame.getJoints().size(), is(82));
    }

    @Test
    public void testParseTruncationSuccess() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "60_12.c3d");
        MoCapScene moCapScene = null;
        try {
            moCapScene = MoCapParser.parse(testFilePath.toAbsolutePath().toString(), 2, 80);
        } catch (Exception e) {
            fail("Should not fail parsing C3D");
        }
        assertThat(moCapScene.getFrames(), is(notNullValue()));
        assertThat(moCapScene.getFrames().size(), is(1690));

        Frame frame = moCapScene.getFrames().get(0);
        assertThat(frame.getJoints().size(), is(78));
    }

    @Test(expected = ParserException.class)
    public void testParseWithInvalidFileType() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "60_12.invalid");
        MoCapParser.parse(testFilePath.toAbsolutePath().toString());
    }
}