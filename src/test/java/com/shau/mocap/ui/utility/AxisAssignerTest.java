package com.shau.mocap.ui.utility;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.closeTo;

public class AxisAssignerTest {

    private static final Double X1 = 0.1;
    private static final Double Y1 = 0.2;
    private static final Double Z1 = 0.3;
    private static final Double X2 = 0.4;
    private static final Double Y2 = 0.5;
    private static final Double Z2 = 0.6;
    private static final Double X3 = 0.7;
    private static final Double Y3 = 0.8;
    private static final Double Z3 = 0.9;
    private static final Double X4 = 1.0;
    private static final Double Y4 = 1.1;
    private static final Double Z4 = 1.2;

    private List<Frame> frames;

    @Before
    public void initTest() {
        frames = new ArrayList<>();
        List<Joint> joints = new ArrayList<>();
        joints.add(new Joint(1, X1, Y1, Z1));
        joints.add(new Joint(2, X2, Y2, Z2));
        frames.add(new Frame(1, joints));
        joints = new ArrayList<>();
        joints.add(new Joint(3, X3, Y3, Z3));
        joints.add(new Joint(4, X4, Y4, Z4));
        frames.add(new Frame(2, joints));
    }

    @Test
    public void testAssignJointAccess() {
        String[] axisAssignment = {"X", "Z", "Y"};

        Double testX = AxisAssigner.assignJointAxis(frames.get(0).getJoints().get(0), axisAssignment[0]);
        Double testY = AxisAssigner.assignJointAxis(frames.get(0).getJoints().get(0), axisAssignment[1]);
        Double testZ = AxisAssigner.assignJointAxis(frames.get(0).getJoints().get(0), axisAssignment[2]);
        assertThat(testX, is(closeTo(X1, 0.01)));
        assertThat(testY, is(closeTo(Z1, 0.01)));
        assertThat(testZ, is(closeTo(Y1, 0.01)));

        axisAssignment = new String[] {"Z", "X", "Y"};
        testX = AxisAssigner.assignJointAxis(frames.get(0).getJoints().get(1), axisAssignment[0]);
        testY = AxisAssigner.assignJointAxis(frames.get(0).getJoints().get(1), axisAssignment[1]);
        testZ = AxisAssigner.assignJointAxis(frames.get(0).getJoints().get(1), axisAssignment[2]);
        assertThat(testX, is(closeTo(Z2, 0.01)));
        assertThat(testY, is(closeTo(X2, 0.01)));
        assertThat(testZ, is(closeTo(Y2, 0.01)));
    }

    @Test
    public void testAssignAllJointsAxis() {
        String[] axisAssignment = {"Y", "Z", "X"};
        AxisAssigner.assignAllJointsAxis(frames, axisAssignment);

        List<Joint> joints = frames.get(0).getJoints();
        assertThat(joints.get(0).getX(), is(closeTo(Y1, 0.01)));
        assertThat(joints.get(0).getY(), is(closeTo(Z1, 0.01)));
        assertThat(joints.get(0).getZ(), is(closeTo(X1, 0.01)));
        assertThat(joints.get(1).getX(), is(closeTo(Y2, 0.01)));
        assertThat(joints.get(1).getY(), is(closeTo(Z2, 0.01)));
        assertThat(joints.get(1).getZ(), is(closeTo(X2, 0.01)));

        joints = frames.get(1).getJoints();
        assertThat(joints.get(0).getX(), is(closeTo(Y3, 0.01)));
        assertThat(joints.get(0).getY(), is(closeTo(Z3, 0.01)));
        assertThat(joints.get(0).getZ(), is(closeTo(X3, 0.01)));
        assertThat(joints.get(1).getX(), is(closeTo(Y4, 0.01)));
        assertThat(joints.get(1).getY(), is(closeTo(Z4, 0.01)));
        assertThat(joints.get(1).getZ(), is(closeTo(X4, 0.01)));
    }
}