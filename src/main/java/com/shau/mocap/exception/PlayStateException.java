package com.shau.mocap.exception;

public class PlayStateException extends Exception {

    public PlayStateException() {
        super();
    }

    public PlayStateException(String message) {
        super(message);
    }

    public PlayStateException(String name, int value, int min, int max) {
        super(name + " (" + value + ") outside of limits: " + min + " - " + max);
    }
}
