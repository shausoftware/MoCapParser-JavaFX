package com.shau.mocap.ui;

import com.shau.mocap.ui.controller.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MoCapUIApp extends Application {

    public MoCapUIApp() {
    }

    public void openScene(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        String filepath = getParameters().getRaw().get(1);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/javafx/Main.fxml"
                )
        );
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.loadMoCap(filepath);

        primaryStage.setTitle("MoCap Parser");
        Scene mainScene = new Scene(root, 1200, 900);
        mainScene.getStylesheets().add("css/mocap.css");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
