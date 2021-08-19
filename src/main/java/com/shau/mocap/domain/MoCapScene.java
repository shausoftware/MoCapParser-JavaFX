package com.shau.mocap.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MoCapScene implements Serializable {

    private String filename;
    private List<Frame> frames;
    private PlayState playState;
    private SpatialOffset spatialOffset = new SpatialOffset();

    public MoCapScene(String fileName, List<Frame> frames) {
        this.filename  = fileName;
        this.frames = frames;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSpatialOffset(SpatialOffset spatialOffset) {
        this.spatialOffset = spatialOffset;
    }

    public void displayProperties() {

        System.out.println("File Name:" + filename);
        System.out.println("Number of Frames:" + frames.size());
        System.out.println("*** Frame Summary Start ***");

        for (Frame frame : frames) {
            int firstNonZero = -1;
            int firstEndZero = -1;

            StringBuffer frameSummary = new StringBuffer();
            for (int i = 0; i < frame.getJoints().size(); i++) {
                Joint joint = frame.getJoints().get(i);
                if (joint.getX() == 0.0 && joint.getY() == 0.0 && joint.getZ() == 0.0) {
                    if (firstNonZero != -1 && firstEndZero < 0) {
                        firstEndZero = i;
                    }
                    frameSummary.append("0");
                } else {
                    if (firstNonZero < 0) {
                        firstNonZero = i;
                    }
                    frameSummary.append("1");
                }
            }

            frameSummary.append(" Joints:" + frame.getJoints().size());
            frameSummary.append(" First Non Zero Joint:" + firstNonZero);
            frameSummary.append(" First End Zero:" + (firstEndZero > 0 ? firstEndZero : "No empty Joint data at end"));
            System.out.println(frameSummary.toString());
        }
        System.out.println("*** Frame Summary End ***");

        Bounds bounds = getBounds();
        System.out.println("minX:" + bounds.getMinX());
        System.out.println("maxX:" + bounds.getMaxX());
        System.out.println("minY:" + bounds.getMinY());
        System.out.println("maxY:" + bounds.getMaxY());
        System.out.println("minZ:" + bounds.getMinZ());
        System.out.println("maxZ:" + bounds.getMaxZ());
    }

    public Bounds getBounds() {
        return new Bounds(this);
    }

    public String getFilename() {
        return filename;
    }

    public String getFilePath() {
        return filename + ".mcd";
    }

    public List<Frame> getFrames() {
        return new ArrayList<>(frames);
    }

    public PlayState getPlayState() {
        return playState;
    }

    public void setPlayState(PlayState playState) {
        this.playState = playState;
    }

    public SpatialOffset getSpatialOffset() {
        return spatialOffset;
    }
}
