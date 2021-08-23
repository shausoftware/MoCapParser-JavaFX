package com.shau.mocap.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class GenerateFourierController {

    private int startFrame;
    private int endFrame;
    private int fourierFrames;
    private boolean loop;
    private boolean useLowResolution;
    private int cutoff;

    @FXML private TextField txtStartFrame;
    @FXML private TextField txtEndFrame;
    @FXML private TextField txtFourierFrames;
    @FXML private CheckBox cbLoop;
    @FXML private CheckBox cbUseLowResolution;
    @FXML private TextField txtCutoff;

    public void initFourierFrames(int startFrame, int endFrame) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.fourierFrames = endFrame - startFrame;
        this.loop = false;
        this.useLowResolution = false;
        this.cutoff = fourierFrames;
        display();
    }

    public void cbUseLowResolutionAction(ActionEvent ae) {
        useLowResolution = cbUseLowResolution.isSelected();
        txtCutoff.setEditable(useLowResolution);
    }

    public int getFourierFrames() {
        validate();
        return fourierFrames;
    }

    public boolean isLoop() {
        return cbLoop.isSelected();
    }

    public boolean isUseLowResolution() {
        return cbUseLowResolution.isSelected();
    }

    public int getCutoff() {
        validate();
        return cutoff;
    }

    private void display() {
        txtStartFrame.setText(String.valueOf(startFrame));
        txtStartFrame.setEditable(false);
        txtEndFrame.setText(String.valueOf(endFrame));
        txtEndFrame.setEditable(false);
        txtFourierFrames.setText(String.valueOf(fourierFrames));
        cbLoop.setSelected(loop);
        cbUseLowResolution.setSelected(useLowResolution);
        txtCutoff.setText(String.valueOf(cutoff));
        txtCutoff.setEditable(useLowResolution);
    }

    private void validate() {
        int updatedFourierFrames = Integer.valueOf(txtFourierFrames.getText());
        if (updatedFourierFrames < 0 || updatedFourierFrames > endFrame - startFrame)
            throw new IllegalArgumentException("Invalid Fourier Frames: " + updatedFourierFrames
                    + " expected range 0-" + (endFrame - startFrame));
        this.fourierFrames = updatedFourierFrames;
        if (cbUseLowResolution.isSelected()) {
            int updatedCutoff = Integer.valueOf(txtCutoff.getText());
            if (updatedCutoff < 0 || updatedCutoff > this.fourierFrames)
                throw new IllegalArgumentException("Invalid Cutoff: " + updatedCutoff
                        + " expected range 0-" + fourierFrames);
            int lowResDataSize = fourierFrames - updatedCutoff;
            if (lowResDataSize % 2 != 0 )
                throw new IllegalArgumentException("Low resolution data size " + lowResDataSize
                        + " should be a modulo of 2. Try cutoff value " + (updatedCutoff - 1)
                        + " or " + (updatedCutoff + 1) + "?");
            this.cutoff = updatedCutoff;
        }
    }
}
