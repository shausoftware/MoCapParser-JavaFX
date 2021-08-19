package com.shau.mocap.ui.controller;

import com.shau.mocap.domain.PlayState;
import com.shau.mocap.ui.utility.FxmlHelper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PlayStateController {

    private PlayState currentPlayState;

    private MainController mainController;

    @FXML private Label lblTotalFrames;
    @FXML private Button btnPlay;
    @FXML private TextField txtStartFrame;
    @FXML private TextField txtEndFrame;
    @FXML private TextField txtCurrentFrame;
    @FXML private TextField txtFrameDuration;
    @FXML private ComboBox<String> cmbView;
    @FXML private ComboBox<String> cmbScale;
    @FXML private Button btnUpdatePlayState;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void updatePlayState(PlayState playState) {
        this.currentPlayState = playState;
        updateDisplay();
    }

    public void btnPlayClick(ActionEvent event) {
        mainController.startPlayer(!currentPlayState.isPlay());
    }

    public void btnUpdatePlayStateClick(ActionEvent event) {
        try {
            currentPlayState.updatePlayState(Integer.valueOf(txtFrameDuration.getText()),
                    Integer.valueOf(txtStartFrame.getText()),
                    Integer.valueOf(txtEndFrame.getText()),
                    Integer.valueOf(txtCurrentFrame.getText()),
                    cmbView.getValue(),
                    Float.valueOf(cmbScale.getValue()));
            mainController.updatePlayState();
        } catch (Exception e) {
            FxmlHelper.showError("Update Play State Error",
                    "Invalid play state data - frameDuration:" + txtFrameDuration.getText()
                            + ", startFrame:" + txtStartFrame.getText()
                            + ", endFrame:" + txtEndFrame.getText()
                            + ", currentFrame:" + txtCurrentFrame.getText());
            updateDisplay();
        }
    }

    private void updateDisplay() {
        lblTotalFrames.setText("Total Frames: " + currentPlayState.getMaxFrames());
        btnPlay.setText(currentPlayState.isPlay() ? "Pause" : "Play");
        txtStartFrame.setText(String.valueOf(currentPlayState.getStartFrame()));
        txtStartFrame.setEditable(!currentPlayState.isPlay());
        txtEndFrame.setText(String.valueOf(currentPlayState.getEndFrame()));
        txtEndFrame.setEditable(!currentPlayState.isPlay());
        txtCurrentFrame.setText(String.valueOf(currentPlayState.getCurrentFrame()));
        txtCurrentFrame.setEditable(!currentPlayState.isPlay());
        txtFrameDuration.setText(String.valueOf(currentPlayState.getFrameDuration()));
        txtFrameDuration.setEditable(!currentPlayState.isPlay());
        btnUpdatePlayState.setDisable(currentPlayState.isPlay());
        cmbView.setValue(currentPlayState.getView());
        cmbView.setDisable(currentPlayState.isPlay());
        cmbScale.setValue(String.valueOf(currentPlayState.getScale()));
        cmbScale.setDisable(currentPlayState.isPlay());
    }
}