package com.shau.mocap.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MoCapSceneTest {

    private static final String TEST_FILE_NAME = "testFileName";

    private MoCapScene testMoCapScene;

    @Before
    public void initTests() {
        testMoCapScene = new MoCapScene(TEST_FILE_NAME, new ArrayList<Frame>());
    }

    @Test
    public void testInitialisedState() {
        assertThat(testMoCapScene.getFilePath(), is(TEST_FILE_NAME + ".mcd"));
        assertThat(testMoCapScene.getPlayState(), is(nullValue()));
        SpatialOffset spatialOffset = testMoCapScene.getSpatialOffset();
        assertThat(spatialOffset, is(notNullValue()));
        assertThat(spatialOffset.getOffsetMode(), is(SpatialOffset.OFFSET_NONE));
    }
}