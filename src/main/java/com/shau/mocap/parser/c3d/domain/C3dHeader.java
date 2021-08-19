package com.shau.mocap.parser.c3d.domain;

public class C3dHeader {

    private int numberOfPoints;
    private int analogueMeasurementsPerFrame;
    private int rawFirstFrame;
    private int rawLastFrame;
    private int maxInterpolationGap;
    private float scale;
    private int dataStartBlock;
    private int analogueSamplesPerFrame;
    private float frameRate;

    public C3dHeader(int numberOfPoints,
                     int analogueMeasurementsPerFrame,
                     int rawFirstFrame,
                     int rawLastFrame,
                     int maxInterpolationGap,
                     float scale,
                     int dataStartBlock,
                     int analogueSamplesPerFrame,
                     float frameRate) {

        this.numberOfPoints = numberOfPoints;
        this.analogueMeasurementsPerFrame = analogueMeasurementsPerFrame;
        this.rawFirstFrame = rawFirstFrame;
        this.rawLastFrame = rawLastFrame;
        this.maxInterpolationGap = maxInterpolationGap;
        this.scale = scale;
        this.dataStartBlock = dataStartBlock;
        this.analogueSamplesPerFrame = analogueSamplesPerFrame;
        this.frameRate = frameRate;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public int getAnalogueMeasurementsPerFrame() {
        return analogueMeasurementsPerFrame;
    }

    public int getRawFirstFrame() {
        return rawFirstFrame;
    }

    public int getRawLastFrame() {
        return rawLastFrame;
    }

    public int getMaxInterpolationGap() {
        return maxInterpolationGap;
    }

    public float getScale() {
        return scale;
    }

    public int getDataStartBlock() {
        return dataStartBlock;
    }

    public int getAnalogueSamplesPerFrame() {
        return analogueSamplesPerFrame;
    }

    public float getFrameRate() {
        return frameRate;
    }
}
