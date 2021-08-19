package com.shau.mocap.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BoundsTest {

    private static final Double JOINT_1_X = -2.0;
    private static final Double JOINT_1_Y = -2.0;
    private static final Double JOINT_1_Z = -2.0;
    private static final Double JOINT_2_X = -1.0;
    private static final Double JOINT_2_Y = -1.0;
    private static final Double JOINT_2_Z = -1.0;
    private static final Double JOINT_3_X = 1.0;
    private static final Double JOINT_3_Y = 1.0;
    private static final Double JOINT_3_Z = 1.0;
    private static final Double JOINT_4_X = 2.0;
    private static final Double JOINT_4_Y = 2.0;
    private static final Double JOINT_4_Z = 2.0;

    private static Joint joint1;
    private static Joint joint2;
    private static Joint joint3;
    private static Joint joint4;

    private static Frame frame1;
    private static Frame frame2;

    private static MoCapScene moCapScene;

    @BeforeClass
    public static void initTests() {
        joint1 = new Joint(1, JOINT_1_X, JOINT_1_Y, JOINT_1_Z);
        joint2 = new Joint(2, JOINT_2_X, JOINT_2_Y, JOINT_2_Z);
        joint3 = new Joint(3, JOINT_3_X, JOINT_3_Y, JOINT_3_Z);
        joint4 = new Joint(4, JOINT_4_X, JOINT_4_Y, JOINT_4_Z);

        frame1 = new Frame(1, Arrays.asList(joint1, joint2));
        frame2 = new Frame(2, Arrays.asList(joint3, joint4));

        moCapScene = new MoCapScene("testFileName", Arrays.asList(frame1, frame2));
    }

    @Test
    public void testFrameBounds() {
        Bounds frame1Bounds = frame1.getBounds();
        assertThat(frame1Bounds.getMinX(), is(JOINT_1_X));
        assertThat(frame1Bounds.getMaxX(), is(JOINT_2_X));
        assertThat(frame1Bounds.getMinY(), is(JOINT_1_Y));
        assertThat(frame1Bounds.getMaxY(), is(JOINT_2_Y));
        assertThat(frame1Bounds.getMinZ(), is(JOINT_1_Z));
        assertThat(frame1Bounds.getMaxZ(), is(JOINT_2_Z));

        Bounds frame2Bounds = frame2.getBounds();
        assertThat(frame2Bounds.getMinX(), is(JOINT_3_X));
        assertThat(frame2Bounds.getMaxX(), is(JOINT_4_X));
        assertThat(frame2Bounds.getMinY(), is(JOINT_3_Y));
        assertThat(frame2Bounds.getMaxY(), is(JOINT_4_Y));
        assertThat(frame2Bounds.getMinZ(), is(JOINT_3_Z));
        assertThat(frame2Bounds.getMaxZ(), is(JOINT_4_Z));
    }

    @Test
    public void testSceneBounds() {
        Bounds sceneBounds = moCapScene.getBounds();
        assertThat(sceneBounds.getMinX(), is(JOINT_1_X));
        assertThat(sceneBounds.getMaxX(), is(JOINT_4_X));
        assertThat(sceneBounds.getMinY(), is(JOINT_1_Y));
        assertThat(sceneBounds.getMaxY(), is(JOINT_4_Y));
        assertThat(sceneBounds.getMinZ(), is(JOINT_1_Z));
        assertThat(sceneBounds.getMaxZ(), is(JOINT_4_Z));
    }
}