package com.shau.mocap.ui.controller;

import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.ui.MoCapPlayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewerController implements Initializable {

    @FXML
    private VBox canvasVBox;
    @FXML private MoCapPlayer moCapViewer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moCapViewer.widthProperty().bind(canvasVBox.widthProperty());
        moCapViewer.heightProperty().bind(canvasVBox.heightProperty());
    }

    public void updateMoCapScene(MoCapScene moCapScene, String[] axisAssignment) {
        moCapViewer.updateMoCapScene(moCapScene, axisAssignment);
    }
}
