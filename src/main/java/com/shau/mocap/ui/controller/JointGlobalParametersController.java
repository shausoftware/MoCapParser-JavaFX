package com.shau.mocap.ui.controller;

import com.shau.mocap.domain.Joint;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class JointGlobalParametersController {

    private Joint joint;

    @FXML private Label lblJointId;
    @FXML private ComboBox<String> cmbColour;
    @FXML private CheckBox cbDisplay;

    public void initJointData(Joint joint) {
        this.joint = joint;
        lblJointId.setText("Joint ID: " + joint.getId());
        cmbColour.setValue(joint.getColour());
        cbDisplay.setSelected(joint.isDisplay());
    }

    public String getUpdatedColour() {
        return cmbColour.getValue();
    }

    public Boolean getJointDisplay() {
        return cbDisplay.isSelected();
    }
}
