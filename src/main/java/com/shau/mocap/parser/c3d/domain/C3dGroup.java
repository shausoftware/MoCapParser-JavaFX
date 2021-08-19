package com.shau.mocap.parser.c3d.domain;

import java.util.ArrayList;
import java.util.List;

public class C3dGroup {

    private int id;
    private String name;
    private String description;

    private List<C3dParameter> parameters = new ArrayList<>();

    public C3dGroup(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void addParameter(C3dParameter parameter) {
        parameters.add(parameter);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<C3dParameter> getParameters() {
        return new ArrayList<>(parameters);
    }
}
