package com.shau.mocap.ui.controller;

import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.SpatialOffset;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class JointsTableController implements Initializable {

    private List<Joint> joints;
    private int currentFrame;

    private MainController mainController;

    @FXML private Label lblCurrentFrameId;
    @FXML private TableView<Joint> tblJoints;
    @FXML private TableColumn<Joint, Integer> jointId;
    @FXML private TableColumn<Joint, String> colour;
    @FXML private TableColumn<Joint, Boolean> display;
    @FXML private TableColumn<Joint, Void> jointData;
    @FXML private TableColumn<Joint, Void> globalParameters;
    @FXML private TableColumn<Joint, Void> centerJoint;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jointId.setCellValueFactory(new PropertyValueFactory<Joint, Integer>("id"));
        colour.setCellValueFactory(new PropertyValueFactory<Joint, String>("colour"));
        display.setCellValueFactory(new PropertyValueFactory<Joint, Boolean>("display"));
        addButtonToTableCell(jointData, "Joint Data", "JOINT_DATA");
        addButtonToTableCell(globalParameters, "Global Parameters", "GLOBAL_PARAMETERS");
        addButtonToTableCell(centerJoint, "Set as Center Joint", "CENTER_JOINT");
    }

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void update(List<Joint> joints, int currentFrame) {
        this.joints = joints;
        this.currentFrame = currentFrame;
        updateDisplay();
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    private void updateDisplay() {
        lblCurrentFrameId.setText("Current Frame ID: " + currentFrame);
        tblJoints.setItems(FXCollections.observableList(joints));
        tblJoints.refresh();
    }

    private void addButtonToTableCell(TableColumn tableColumn, String name, String buttonTypeId) {

        Callback<TableColumn<Joint, Void>,
                        TableCell<Joint, Void>> cellFactory = new Callback<TableColumn<Joint, Void>,
                TableCell<Joint, Void>>() {
            @Override
            public TableCell<Joint, Void> call(final TableColumn<Joint, Void> param) {
                final TableCell<Joint, Void> cell = new TableCell<Joint, Void>() {
                    private final Button btn = new Button(name);
                    {
                        btn.setId(buttonTypeId);
                        btn.setOnAction(ae -> {
                            String id = ((Node) ae.getSource()).getId();
                            Joint joint = getTableView().getItems().get(getIndex());
                            if (id.equals("JOINT_DATA")) {
                                mainController.openJointDataView(joint);
                            } else if (id.equals("GLOBAL_PARAMETERS")) {
                                mainController.openJointGlobalParameters(joint);
                            } else if (id.equals("CENTER_JOINT")) {
                                mainController.updateSpatialOffset(new SpatialOffset(joint.getId()));
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        tableColumn.setCellFactory(cellFactory);
    }
}
