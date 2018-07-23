package com.github.howaric.docker_rapido.exceptions;

public class IllegalDeployPolicyException extends RuntimeException {

    private static final long serialVersionUID = -1157908936313548989L;

    public IllegalDeployPolicyException() {
        super();
    }

    public IllegalDeployPolicyException(String message) {
        super(message);
    }

}
