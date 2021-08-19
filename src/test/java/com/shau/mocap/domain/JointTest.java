package com.shau.mocap.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JointTest {

    private static final int JOINT_ID = 1;
    private static final double JOINT_X =  2.0;
    private static final double JOINT_Y =  2.0;
    private static final double JOINT_Z =  2.0;

    private static Joint testJoint;

    @BeforeClass
    public static void initTests() {
        testJoint = new Joint(JOINT_ID,
                JOINT_X,
                JOINT_Y,
                JOINT_Z);
    }

    @Test
    public void testJointDefaults() {
        assertThat(testJoint.getId(), is(JOINT_ID));
        assertThat(testJoint.getX(), is(JOINT_X));
        assertThat(testJoint.getY(), is(JOINT_Y));
        assertThat(testJoint.getZ(), is(JOINT_Z));
        assertThat(testJoint.getColour(), is("White"));
        assertThat(testJoint.isDisplay(), is(true));
    }
}