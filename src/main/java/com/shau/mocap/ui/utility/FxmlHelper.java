package com.shau.mocap.ui.utility;

import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

public class FxmlHelper {

    public static void showError(String title, String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(title);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    public static Color getColourByName(String colourName) {
        switch (colourName) {
            case "White":
                return Color.WHITE;
            case "Red":
                return Color.RED;
            case "Green":
                return Color.GREEN;
            case "Blue":
                return Color.BLUE;
            default:
                throw new IllegalArgumentException("Unsupported Colour Lookup: " + colourName);
        }
    }

    /*
    public static void setTextFieldPositiveIntegerOnly(TextField textField) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d+")) {
                    textField.setText(oldValue);
                }
            }
        });
    }
    */

    /*
    public static void setTextFieldDoubleOnly(TextField textField) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.matches("^-?\\d+(\\.\\d+)?$")) {
                    textField.setText(oldValue);
                }
            }
        });
    }
     */
}
