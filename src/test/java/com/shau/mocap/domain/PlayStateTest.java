package com.shau.mocap.domain;

import com.shau.mocap.exception.PlayStateException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class PlayStateTest {

    private static final int MAX_FRAMES = 10;

    private PlayState testState;

    @Before
    public void initTest() {
        testState = new PlayState(MAX_FRAMES);
    }

    @Test
    public void testInitialState() {
        assertThat(testState.getCurrentFrame(), is(0));
        assertThat(testState.getStartFrame(), is(0));
        assertThat(testState.getEndFrame(), is(MAX_FRAMES - 1));
        assertThat(testState.getMaxFrames(), is(MAX_FRAMES));
        assertThat(testState.getFrameDuration(), is(100));
        assertThat(testState.getView(), is(testState.views[0]));
        assertThat(testState.getScale(), is(testState.scales[3]));
        assertThat(testState.isPlay(), is(false));
    }

    @Test
    public void testFrameIncrements() throws Exception{
        int currentFrame = testState.getCurrentFrame();

        for (int i = 0; i < MAX_FRAMES - 1; i++) {
            testState.incrementCurrentFrame();
            assertThat(testState.getCurrentFrame(), is(++currentFrame));
        }

        testState.incrementCurrentFrame();
        assertThat(testState.getCurrentFrame(), is(0));

        //change start and end frames
        testState.updatePlayState(testState.getFrameDuration(),
                2,
                8,
                2, //start position
                testState.getView(),
                testState.getScale());

        assertThat(testState.getStartFrame(), is(2));
        assertThat(testState.getEndFrame(), is(8));
        assertThat(testState.getStartFrame(), is(2));

        currentFrame = testState.getCurrentFrame();
        for (int i = 2; i < 8; i++) {
            testState.incrementCurrentFrame();
            assertThat(testState.getCurrentFrame(), is(++currentFrame));
        }

        testState.incrementCurrentFrame();
        assertThat(testState.getCurrentFrame(), is(2));
    }

    @Test
    public void testValidStateUpdate() throws Exception {

        testState.updatePlayState(testState.MIN_FRAME_DURATION,
                3,
                7,
                3, //start frame
                testState.views[1],
                testState.scales[0]);

        assertThat(testState.getFrameDuration(), is(testState.MIN_FRAME_DURATION));
        assertThat(testState.getStartFrame(), is(3));
        assertThat(testState.getEndFrame(), is(7));
        assertThat(testState.getCurrentFrame(), is(3));
        assertThat(testState.getView(), is(testState.views[1]));
        assertThat(testState.getScale(), is(testState.scales[0]));

        assertThat(testState.getCurrentFrame(), is(3));
    }

    @Test(expected = PlayStateException.class)
    public void testInvalidLowFrameDurationUpdate() throws Exception {
        int lowFrameRateDuration = testState.MIN_FRAME_DURATION - 1;
        try {
            testState.updatePlayState(lowFrameRateDuration,
                    testState.getStartFrame(),
                    testState.getEndFrame(),
                    testState.getCurrentFrame(),
                    testState.getView(),
                    testState.getScale());
            fail("Low frame rate duration should throw exception");
        } catch (PlayStateException pse) {
            assertThat(pse.getMessage(), is("frame duration (" + lowFrameRateDuration + ") outside of limits: 10 - 1000"));
            throw pse;
        }
    }

    @Test(expected = PlayStateException.class)
    public void testInvalidHighFrameDurationUpdate() throws Exception {
        int highFrameRateDuration = testState.MAX_FRAME_DURATION + 1;
        try {
            testState.updatePlayState(highFrameRateDuration,
                    testState.getStartFrame(),
                    testState.getEndFrame(),
                    testState.getCurrentFrame(),
                    testState.getView(),
                    testState.getScale());
            fail("High frame rate duration should throw exception");
        } catch (PlayStateException pse) {
            assertThat(pse.getMessage(), is("frame duration (" + highFrameRateDuration + ") outside of limits: 10 - 1000"));
            throw pse;
        }
    }

    @Test(expected = PlayStateException.class)
    public void testLowStartFrameUpdate() throws Exception {
        int lowStartFrame = -1;
        try {
            testState.updatePlayState(testState.getFrameDuration(),
                    lowStartFrame,
                    testState.getEndFrame(),
                    testState.getCurrentFrame(),
                    testState.getView(),
                    testState.getScale());
            fail("Low start frame should throw exception");
        } catch (PlayStateException pse) {
            assertThat(pse.getMessage(), is("start frame (" + lowStartFrame + ") outside of limits: 0 - " + (MAX_FRAMES - 2)));
            throw pse;
        }
    }

    @Test(expected = PlayStateException.class)
    public void testHighStartFrameUpdate() throws Exception {
        int highStartFrame = MAX_FRAMES - 1;
        try {
            testState.updatePlayState(testState.getFrameDuration(),
                    highStartFrame,
                    testState.getEndFrame(),
                    testState.getCurrentFrame(),
                    testState.getView(),
                    testState.getScale());
            fail("High start frame should throw exception");
        } catch (PlayStateException pse) {
            assertThat(pse.getMessage(), is("start frame (" + highStartFrame + ") outside of limits: 0 - " + (MAX_FRAMES - 2)));
            throw pse;
        }
    }

    @Test(expected = PlayStateException.class)
    public void testHighEndFrameUpdate() throws Exception {
        int highEndFrame = MAX_FRAMES;
        try {
            testState.updatePlayState(testState.getFrameDuration(),
                    testState.getStartFrame(),
                    highEndFrame,
                    testState.getCurrentFrame(),
                    testState.getView(),
                    testState.getScale());
            fail("High end frame should throw exception");
        } catch (PlayStateException pse) {
            assertThat(pse.getMessage(), is("end frame (" + highEndFrame + ") outside of limits: 1 - " + (MAX_FRAMES - 1)));
            throw pse;
        }
    }

    @Test(expected = PlayStateException.class)
    public void testLowCurrentFrameUpdate() throws Exception {
        int lowCurrentFrame = -1;
        try {
            testState.updatePlayState(testState.getFrameDuration(),
                    testState.getStartFrame(),
                    testState.getEndFrame(),
                    lowCurrentFrame,
                    testState.getView(),
                    testState.getScale());
            fail("Low current frame should throw exception");
        } catch (PlayStateException pse) {
            assertThat(pse.getMessage(), is("current frame (" + lowCurrentFrame + ") outside of limits: 0 - " + (MAX_FRAMES - 1)));
            throw pse;
        }
    }

    @Test(expected = PlayStateException.class)
    public void testHighCurrentFrameUpdate() throws Exception {
        int highCurrentFrame = -1;
        try {
            testState.updatePlayState(testState.getFrameDuration(),
                    testState.getStartFrame(),
                    testState.getEndFrame(),
                    highCurrentFrame,
                    testState.getView(),
                    testState.getScale());
            fail("High current frame should throw exception");
        } catch (PlayStateException pse) {
            assertThat(pse.getMessage(), is("current frame (" + highCurrentFrame + ") outside of limits: 0 - " + (MAX_FRAMES - 1)));
            throw pse;
        }
    }

    @Test(expected = PlayStateException.class)
    public void testInvalidViewUpdate() throws Exception {
        try {
            testState.updatePlayState(testState.getFrameDuration(),
                    testState.getStartFrame(),
                    testState.getEndFrame(),
                    testState.getCurrentFrame(),
                    "XX",
                    testState.getScale());
            fail("Invalid view should throw exception");
        } catch (PlayStateException pse) {
            assertThat(pse.getMessage(), is("invalid view (XX). Expecting XY, ZY"));
            throw pse;
        }
    }

    @Test(expected = PlayStateException.class)
    public void testInvalidScaleUpdate() throws Exception {
        try {
            testState.updatePlayState(testState.getFrameDuration(),
                    testState.getStartFrame(),
                    testState.getEndFrame(),
                    testState.getCurrentFrame(),
                    testState.getView(),
                    11.0f);
            fail("Invalid scale should throw exception");
        } catch (PlayStateException pse) {
            assertThat(pse.getMessage(), is("invalid scale (11.0). Expecting 10.0, 5.0, 2.0, 1.0, 0.5, 0.25, 0.1"));
            throw pse;
        }
    }
}