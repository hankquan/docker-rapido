package com.github.howaric.docker_rapido.exceptions;

public class UnsupportedTypeException extends RuntimeException {

    private static final long serialVersionUID = -1157908936313548989L;

    public UnsupportedTypeException() {
        super();
    }

    public UnsupportedTypeException(String message) {
        super(message);
    }

    public UnsupportedTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}
