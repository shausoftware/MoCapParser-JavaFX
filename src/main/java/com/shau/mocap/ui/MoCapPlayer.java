package com.shau.mocap.ui;

import com.shau.mocap.domain.*;
import com.shau.mocap.ui.utility.FxmlHelper;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MoCapPlayer extends Canvas {

    private MoCapScene moCapScene;
    private PlayState playState;
    private String[] axisAssignment;

    public MoCapPlayer() {
        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }

    public void updateMoCapScene(MoCapScene moCapScene, String[] axisAssignment) {
        this.moCapScene = moCapScene;
        playState = moCapScene.getPlayState();
        this.axisAssignment = axisAssignment;
        draw();
    }

    private void draw() {

        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, width);
        Font font = new Font("Serif", 10);
        gc.setFont(font);

        if (moCapScene != null) {
            String view = playState.getView();
            float scale = playState.getScale();
            Frame frame = moCapScene.getFrames().get(playState.getCurrentFrame());
            Bounds bounds = moCapScene.getBounds();

            SpatialOffset spatialOffset = moCapScene.getSpatialOffset();
            Double[] sceneOffset = new Double[] {bounds.getMinX(), bounds.getMinY(), bounds.getMinZ()};
            Integer[] screenOffset = new Integer[] {0, 0};
            if (SpatialOffset.OFFSET_XYZ == spatialOffset.getOffsetMode()) {
                sceneOffset = new Double[] {spatialOffset.getOffsetPointX(),
                        spatialOffset.getOffsetPointY(),
                        spatialOffset.getOffsetPointZ()};
                screenOffset = new Integer[] {(int) width / 2, (int) height / 2};
            } else if (SpatialOffset.OFFSET_JOINT == spatialOffset.getOffsetMode()) {
                Joint offset = frame.getJoints().get(spatialOffset.getOffsetJointId() - 1);
                sceneOffset = new Double[] {offset.getX(), offset.getY(), offset.getZ()};
                screenOffset = new Integer[] {(int) width / 2, (int) height / 2};
            }

            for (Joint joint : frame.getJoints()) {
                if (joint.isDisplay()) {
                    Integer x = (int) ((joint.getX().intValue() - sceneOffset[0].intValue()) * scale);
                    Integer y = (int) ((joint.getY().intValue() - sceneOffset[1].intValue()) * scale);
                    Integer z = (int) ((joint.getZ().intValue() - sceneOffset[2].intValue()) * scale);
                    Integer xPos = (view.equals("XY") ? x : z) + screenOffset[0];
                    Integer yPos = ((int) height - y) - screenOffset[1];

                    gc.setFill(FxmlHelper.getColourByName(joint.getColour()));
                    gc.fillOval(xPos - 4, yPos - 4, 8, 8);
                    gc.fillText("J:" + joint.getId(), xPos + 5, yPos + 5);
                }
            }
        }
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}
