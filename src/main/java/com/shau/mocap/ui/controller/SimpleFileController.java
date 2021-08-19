package com.shau.mocap.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SimpleFileController {

    @FXML private TextField txtFilename;

    public void initFilename(String filename) {
        txtFilename.setText(filename);
    }

    public String getFilename() {
        return txtFilename.getText();
    }
}
