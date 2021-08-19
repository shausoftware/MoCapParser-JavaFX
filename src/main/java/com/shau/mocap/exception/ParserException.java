package com.shau.mocap.exception;

public class ParserException extends Exception {

    public static final int GENERAL_EXCEPTION = 1;
    public static final int STOP_PARSING_EXCEPTION = 2;

    private int exceptionType = GENERAL_EXCEPTION;

    public ParserException() {
        super();
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, int exceptionType) {
        super(message);
        this.exceptionType = exceptionType;
    }

    public int getExceptionType() {
        return exceptionType;
    }
}
