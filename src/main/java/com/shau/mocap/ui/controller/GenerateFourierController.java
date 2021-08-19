package com.shau.mocap.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class GenerateFourierController {

    private int startFrame;
    private int endFrame;
    private int fourierFrames;
    private boolean loop;

    @FXML private TextField txtStartFrame;
    @FXML private TextField txtEndFrame;
    @FXML private TextField txtFourierFrames;
    @FXML private CheckBox cbLoop;

    public void initFourierFrames(int startFrame, int endFrame) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.fourierFrames = endFrame - startFrame;
        this.loop = false;
        display();
    }

    public int getStartFrame() {
        return Integer.valueOf(txtStartFrame.getText());
    }

    public int getEndFrame() {
        return Integer.valueOf(txtEndFrame.getText());
    }

    public int getFourierFrames() {
        return Integer.valueOf(txtFourierFrames.getText());
    }

    public boolean isLoop() {
        return cbLoop.isSelected();
    }

    public void display() {
        txtStartFrame.setText(String.valueOf(startFrame));
        txtEndFrame.setText(String.valueOf(endFrame));
        txtFourierFrames.setText(String.valueOf(fourierFrames));
        cbLoop.setSelected(loop);
    }
}
