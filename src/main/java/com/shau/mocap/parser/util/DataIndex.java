package com.shau.mocap.parser.util;

public class DataIndex<T> {

    private int idx;
    private T generatedObject;

    public DataIndex(T generatedObject, int idx) {
        this.generatedObject = generatedObject;
        this.idx = idx;
    }

    public T getGeneratedObject() {
        return generatedObject;
    }

    public int getIdx() {
        return idx;
    }
}
