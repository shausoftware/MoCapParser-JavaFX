package com.shau.mocap.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;

public class AxisAssignmentController {

    private String[] axisAssignment;

    @FXML private ComboBox<String> cmbAssignmentX;
    @FXML private ComboBox<String> cmbAssignmentY;
    @FXML private ComboBox<String> cmbAssignmentZ;

    public void initAxisAssignment(String[] axisAssignment) {
        this.axisAssignment = axisAssignment;
        display();
    }

    public String[] getAxisAssignment() throws IllegalArgumentException {
        List<String> updatedAxisAssignment = List.of(cmbAssignmentX.getValue(),
                cmbAssignmentY.getValue(),
                cmbAssignmentZ.getValue());
        if (updatedAxisAssignment.contains("X")
                && updatedAxisAssignment.contains("Y")
                && updatedAxisAssignment.contains("Z")) {
            return updatedAxisAssignment.toArray(new String[3]);
        }
        throw new IllegalArgumentException("Invalid Axis assignment " + updatedAxisAssignment.get(0)
                + "," + updatedAxisAssignment.get(1) + "," + updatedAxisAssignment.get(2));
    }

    private void display() {
        cmbAssignmentX.setValue(axisAssignment[0]);
        cmbAssignmentY.setValue(axisAssignment[1]);
        cmbAssignmentZ.setValue(axisAssignment[2]);
    }
}
