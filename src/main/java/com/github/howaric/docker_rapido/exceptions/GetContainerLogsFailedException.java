package com.github.howaric.docker_rapido.exceptions;

public class GetContainerLogsFailedException extends RuntimeException {

    private static final long serialVersionUID = -1157908936313548989L;

    public GetContainerLogsFailedException() {
        super();
    }

    public GetContainerLogsFailedException(String message) {
        super(message);
    }

}
