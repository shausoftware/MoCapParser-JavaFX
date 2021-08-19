package com.shau.mocap.ui.utility;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;

import java.util.List;

public class AxisAssigner {

    public static void assignAllJointsAxis(List<Frame> frames, String[] axisAssignment) {
        frames.stream().flatMap(f -> f.getJoints().stream()).forEach(j -> {
            Double updateX = assignJointAxis(j, axisAssignment[0]);
            Double updateY = assignJointAxis(j, axisAssignment[1]);
            Double updateZ = assignJointAxis(j, axisAssignment[2]);
            j.updatePosition(updateX, updateY, updateZ);
        });
    }

    public static Double assignJointAxis(Joint joint, String assignment) {
        if ("X".equals(assignment)) {
            return joint.getX();
        } else if ("Y".equals(assignment)) {
            return joint.getY();
        }
        return joint.getZ();
    }
}
