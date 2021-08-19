package com.shau.mocap.parser.c3d.domain;

import java.util.ArrayList;
import java.util.List;

public class C3dParameter {

    private String name;
    private String description;
    private int groupId;
    private boolean locked;
    private int dimensions;
    private int[] dimensionSizes;
    private List<Object> values;

    public C3dParameter(String name,
                     String description,
                     int groupId,
                     boolean locked,
                     int dimensions,
                     int[] dimensionSizes,
                     List<Object> values) {

        this.name = name;
        this.description = description;
        this.groupId = groupId;
        this.locked = locked;
        this.dimensions = dimensions;
        this.dimensionSizes = dimensionSizes;
        this.values = values;
    }

    public List<Object> getValues() {
        return new ArrayList<>(values);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isLocked() {
        return locked;
    }
}
