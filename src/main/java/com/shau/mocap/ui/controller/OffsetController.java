package com.shau.mocap.ui.controller;

import com.shau.mocap.domain.SpatialOffset;
import com.shau.mocap.ui.utility.FxmlHelper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class OffsetController {

    private MainController mainController;

    private SpatialOffset spatialOffset;

    @FXML private Label lblOffsetJointId;
    @FXML private TextField txtOffsetX;
    @FXML private TextField txtOffsetY;
    @FXML private TextField txtOffsetZ;
    @FXML private Button btnClearOffset;
    @FXML private Button btnUpdateOffset;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void updateSpatialOffset(SpatialOffset spatialOffset) {
        this.spatialOffset = spatialOffset;
        display();
    }

    public void btnClearOffsetAction(ActionEvent ae) {
        spatialOffset = new SpatialOffset(); //empty offset
        mainController.updateSpatialOffset(spatialOffset);
    }

    public void btnUpdateOffsetAction(ActionEvent ae) {
        try {
            spatialOffset = new SpatialOffset(Double.valueOf(txtOffsetX.getText()),
                    Double.valueOf(txtOffsetY.getText()),
                    Double.valueOf(txtOffsetZ.getText()));
            mainController.updateSpatialOffset(spatialOffset);
        } catch (Exception e) {
            FxmlHelper.showError("Update Offset Error",
                    "Invalid spatial data x:" + txtOffsetX.getText() + " y:" + txtOffsetY.getText()
                            + " z:" + txtOffsetZ.getText());
        }
    }

    public SpatialOffset getSpatialOffset() {
        return spatialOffset;
    }

    private void display() {
        txtOffsetX.setText(null);
        txtOffsetY.setText(null);
        txtOffsetZ.setText(null);
        txtOffsetX.setEditable(true);
        txtOffsetY.setEditable(true);
        txtOffsetZ.setEditable(true);
        btnUpdateOffset.setDisable(true);
        if (spatialOffset.getOffsetMode() == SpatialOffset.OFFSET_JOINT) {
            lblOffsetJointId.setText("Offset Joint ID: " + spatialOffset.getOffsetJointId());
            txtOffsetX.setEditable(false);
            txtOffsetY.setEditable(false);
            txtOffsetZ.setEditable(false);
        } else if (spatialOffset.getOffsetMode() == SpatialOffset.OFFSET_XYZ) {
            lblOffsetJointId.setText("Offset Joint Not Set");
            txtOffsetX.setText(String.valueOf(spatialOffset.getOffsetPointX()));
            txtOffsetY.setText(String.valueOf(spatialOffset.getOffsetPointY()));
            txtOffsetZ.setText(String.valueOf(spatialOffset.getOffsetPointZ()));
            btnUpdateOffset.setDisable(false);
        } else if (spatialOffset.getOffsetMode() == SpatialOffset.OFFSET_NONE) {
            lblOffsetJointId.setText("Offset Joint Not Set");
            btnUpdateOffset.setDisable(false);
        }
    }
}
