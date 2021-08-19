package com.shau.mocap.ui.controller;

import com.shau.mocap.domain.Joint;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class JointDataController {

    private Joint joint;

    @FXML private Label lblJointId;
    @FXML private TextField txtJointX;
    @FXML private TextField txtJointY;
    @FXML private TextField txtJointZ;

    public Joint getUpdatedJoint() throws IllegalArgumentException {
        try {
            return new Joint(joint.getId(),
                    Double.valueOf(txtJointX.getText()),
                    Double.valueOf(txtJointY.getText()),
                    Double.valueOf(txtJointZ.getText()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Joint Data: " + txtJointX.getText()
                    + ", " + txtJointY.getText()
                    + ", " + txtJointZ.getText());
        }
    }

    public void initJointData(Joint joint) {
        this.joint = joint;
        lblJointId.setText("Joint ID: " + joint.getId());
        txtJointX.setText(String.valueOf(joint.getX()));
        txtJointY.setText(String.valueOf(joint.getY()));
        txtJointZ.setText(String.valueOf(joint.getZ()));
    }
}
