package com.github.howaric.docker_rapido.exceptions;

public class ContainerStartingFailedException extends RuntimeException {

    private static final long serialVersionUID = -1157908936313548989L;

    public ContainerStartingFailedException() {
        super();
    }

    public ContainerStartingFailedException(String message) {
        super(message);
    }

}
