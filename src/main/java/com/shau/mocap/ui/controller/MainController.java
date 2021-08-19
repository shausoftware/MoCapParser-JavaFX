package com.shau.mocap.ui.controller;

import com.shau.mocap.MoCapFileHandler;
import com.shau.mocap.domain.*;
import com.shau.mocap.exception.MoCapFileHandlerException;
import com.shau.mocap.fourier.FourierGenerator;
import com.shau.mocap.ui.utility.AxisAssigner;
import com.shau.mocap.ui.utility.FxmlHelper;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final int FILE_OPEN = 0;
    private static final int FILE_SAVE_AS = 1;

    private MoCapScene moCapScene;
    private String[] axisAssignment = {"X", "Y", "Z"};

    private Timeline timeline;

    @FXML private PlayStateController playStateController;
    @FXML private JointsTableController jointsTableController;
    @FXML private ViewerController viewerController;
    @FXML private OffsetController offsetController;

    @FXML private MenuItem menuOpenFile;
    @FXML private MenuItem menuSaveFile;
    @FXML private MenuItem menuSaveFileAs;
    @FXML private MenuItem menuChangeAxis;
    @FXML private MenuItem menuGenerateFourier;

    @FXML private Label lblFilename;
    @FXML private Button btnUpdateJointsForFrame;

    public void initialize(URL location, ResourceBundle resources) {
        playStateController.injectMainController(this);
        jointsTableController.injectMainController(this);
        offsetController.injectMainController(this);
    }

    public void loadMoCap(String filepath) throws MoCapFileHandlerException {
        moCapScene = MoCapFileHandler.loadSceneData(filepath);
        lblFilename.setText("Current File: " + moCapScene.getFilename());
        if (moCapScene.getPlayState() == null) {
            moCapScene.setPlayState(new PlayState(moCapScene.getFrames().size()));
        }
        updateJointsTable();
        updateControls();
    }

    //controller callbacks

    public void openJointDataView(Joint joint) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/javafx/JointData.fxml"
                    )
            );
            DialogPane dialogPane = loader.load();
            JointDataController jointDataController = loader.getController();
            jointDataController.initJointData(joint);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Joint Spatial Data");
            Optional<ButtonType> clicked = dialog.showAndWait();
            if (clicked.get() == ButtonType.APPLY) {
                Joint updatedJoint = jointDataController.getUpdatedJoint();
                moCapScene.getFrames().stream()
                        .filter(f -> f.getId() == jointsTableController.getCurrentFrame() + 1) //frame ids from  1 not 0
                        .flatMap(f -> f.getJoints().stream())
                        .filter(j -> j.getId() == updatedJoint.getId())
                        .forEach(j -> j.updatePosition(updatedJoint.getX(), updatedJoint.getY(), updatedJoint.getZ()));
                updateJointsTable();
                updateControls();
            }
        } catch (Exception e) {
            FxmlHelper.showError("Joint Data Error", e.getMessage());
        }
    }

    public void openJointGlobalParameters(Joint joint) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/javafx/JointGlobalParameters.fxml"
                    )
            );
            DialogPane dialogPane = loader.load();
            JointGlobalParametersController jointGlobalParametersController = loader.getController();
            jointGlobalParametersController.initJointData(joint);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Joint Global Parameters");
            Optional<ButtonType> clicked = dialog.showAndWait();
            if (clicked.get() == ButtonType.APPLY) {
                Boolean updateDisplay = jointGlobalParametersController.getJointDisplay();
                String updateColour = jointGlobalParametersController.getUpdatedColour();
                moCapScene.getFrames().stream()
                        .flatMap(f -> f.getJoints().stream())
                        .filter(j -> j.getId() == joint.getId())
                        .forEach(j -> j.updateDisplayState(updateColour, updateDisplay));
                updateJointsTable();
                updateControls();
            }
        } catch (Exception e) {
            FxmlHelper.showError("Joint Global Parameter Error", e.getMessage());
        }
    }

    public void updateSpatialOffset(SpatialOffset spatialOffset) {
        moCapScene.setSpatialOffset(spatialOffset);
        updateControls();
    }

    public void startPlayer(boolean start) {

        if (timeline != null) {
            timeline.stop();
        }
        if (start) {
            timeline = new Timeline(new KeyFrame(Duration.millis(moCapScene.getPlayState().getFrameDuration()), ev -> {
                moCapScene.getPlayState().incrementCurrentFrame();
                updatePlayer();
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }

        moCapScene.getPlayState().setPlay(start);
        updatePlayer();
    }

    public void updatePlayState() {
        updatePlayer();
    }

    //event handlers

    public void btnUpdateJointsForFrameClick(ActionEvent event) {
        updateJointsTable();
    }

    public void menuOpenFileClick(ActionEvent ae) {
        simpleFileHandler("", FILE_OPEN);
    }

    public void menuSaveFileClick(ActionEvent ae) {
        try {
            MoCapFileHandler.saveSceneData(moCapScene);
        } catch (MoCapFileHandlerException e) {
            FxmlHelper.showError("File Save Error", e.getMessage());
        }
    }

    public void menuSaveFileAsClick(ActionEvent ae) {
        simpleFileHandler(moCapScene.getFilename(), FILE_SAVE_AS);
    }

    private void simpleFileHandler(String filename, int action) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/javafx/SimpleFile.fxml"
                    )
            );
            DialogPane dialogPane = loader.load();
            SimpleFileController controller = loader.getController();
            controller.initFilename(filename);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(action == FILE_OPEN ? "Open File" : "Save File");
            Optional<ButtonType> clicked = dialog.showAndWait();
            if (clicked.get() == ButtonType.YES) {
                String updatedFilename = controller.getFilename();
                if (action == FILE_OPEN) {
                    updatedFilename += ".mcd";
                    loadMoCap(updatedFilename);
                } else {
                    moCapScene.setFilename(updatedFilename);
                    MoCapFileHandler.saveSceneData(moCapScene);
                }
            }
        } catch (Exception e) {
            FxmlHelper.showError(action == FILE_OPEN ? "file open error" : "file save as error", e.getMessage());
        }
    }

    public void menuChangeAxisClick(ActionEvent ae) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/javafx/AxisAssignment.fxml"
                    )
            );
            DialogPane dialogPane = loader.load();
            AxisAssignmentController controller = loader.getController();
            controller.initAxisAssignment(axisAssignment);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Axis");
            Optional<ButtonType> clicked = dialog.showAndWait();
            if (clicked.get() == ButtonType.OK) {
                axisAssignment = controller.getAxisAssignment();
                AxisAssigner.assignAllJointsAxis(moCapScene.getFrames(), axisAssignment);
                updateControls();
            }
        } catch (Exception e)  {
            FxmlHelper.showError("Axis Assignment error", e.getMessage());
        }
    }

    public void menuGenerateFourierClick(ActionEvent ae) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/javafx/GenerateFourier.fxml"
                    )
            );
            DialogPane dialogPane = loader.load();
            GenerateFourierController controller = loader.getController();
            PlayState playState = moCapScene.getPlayState();
            controller.initFourierFrames(playState.getStartFrame(), playState.getEndFrame());
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Shadertoy");
            Optional<ButtonType> clicked = dialog.showAndWait();
            if (clicked.get() == ButtonType.OK) {
                int startFrame = controller.getStartFrame();
                int endFrame = controller.getEndFrame();
                int fourierFrames = controller.getFourierFrames();
                boolean loop = controller.isLoop();
                FourierGenerator.generateFourier(moCapScene, startFrame, endFrame, fourierFrames, loop);
            }
        } catch (Exception e) {
            FxmlHelper.showError("Generate Fourier error", e.getMessage());
        }
    }

    //display handlers

    private void updatePlayer() {
        playStateController.updatePlayState(moCapScene.getPlayState());
        viewerController.updateMoCapScene(moCapScene, axisAssignment);
    }

    private void updateJointsTable() {
        int currentFrame = moCapScene.getPlayState().getCurrentFrame();
        jointsTableController.update(moCapScene.getFrames().get(currentFrame).getJoints(), currentFrame);
    }

    private void updateControls() {
        playStateController.updatePlayState(moCapScene.getPlayState());
        viewerController.updateMoCapScene(moCapScene, axisAssignment);
        offsetController.updateSpatialOffset(moCapScene.getSpatialOffset());
        btnUpdateJointsForFrame.setDisable(moCapScene.getPlayState().isPlay());
    }
}