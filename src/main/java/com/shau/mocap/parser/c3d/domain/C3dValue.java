package com.shau.mocap.parser.c3d.domain;

import java.util.List;

public class C3dValue {

    private List<C3dSpatialPoint> spatialPoints;
    private List<Float> analogValues;

    public C3dValue(List<C3dSpatialPoint> spatialPoints, List<Float> analogValues) {
        this.spatialPoints = spatialPoints;
        this.analogValues = analogValues;
    }

    public List<C3dSpatialPoint> getSpatialPoints() {
        return spatialPoints;
    }

    public List<Float> getAnalogValues() {
        return analogValues;
    }
}
