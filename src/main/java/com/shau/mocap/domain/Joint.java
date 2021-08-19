package com.shau.mocap.domain;

import java.io.Serializable;

public class Joint implements Serializable {

    private int id;
    private Double x;
    private Double y;
    private Double z;
    private String colour = "White";
    private boolean display = true;

    public Joint (int id, Double x, Double y, Double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void updateDisplayState(String colour, boolean display) {
        this.colour = colour;
        this.display = display;
    }

    public void updatePosition(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public String getColour() {
        return colour;
    }

    public boolean isDisplay() {
        return display;
    }
}
