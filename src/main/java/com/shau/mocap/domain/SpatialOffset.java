package com.shau.mocap.domain;

import java.io.Serializable;

public class SpatialOffset implements Serializable {

    public static final int OFFSET_NONE = 0;
    public static final int OFFSET_JOINT = 1;
    public static final int OFFSET_XYZ = 2;

    private int offsetMode = OFFSET_NONE;

    private Integer jointId;
    private Double offsetPointX = null;
    private Double offsetPointY = null;
    private Double offsetPointZ = null;

    public SpatialOffset() {
        offsetMode = OFFSET_NONE;
        jointId = null;
        this.offsetPointX = null;
        this.offsetPointY = null;
        this.offsetPointZ = null;
    }

    public SpatialOffset(Integer jointId) {
        offsetMode = OFFSET_JOINT;
        this.jointId = jointId;
        this.offsetPointX = null;
        this.offsetPointY = null;
        this.offsetPointZ = null;
    }

    public SpatialOffset(Double offsetPointX, Double offsetPointY, Double offsetPointZ) {
        offsetMode = OFFSET_XYZ;
        this.jointId = null;
        this.offsetPointX = offsetPointX;
        this.offsetPointY = offsetPointY;
        this.offsetPointZ = offsetPointZ;
    }

    public int getOffsetMode() {
        return offsetMode;
    }

    public Integer getOffsetJointId() {
        return jointId;
    }

    public Double getOffsetPointX() {
        return offsetPointX;
    }

    public Double getOffsetPointY() {
        return offsetPointY;
    }

    public Double getOffsetPointZ() {
        return offsetPointZ;
    }
}
