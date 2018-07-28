package com.github.howaric.docker_rapido.exceptions;

public class IllegalPolicyException extends RuntimeException {

    private static final long serialVersionUID = -1157908936313548989L;

    public IllegalPolicyException() {
        super();
    }

    public IllegalPolicyException(String message) {
        super(message);
    }

}
