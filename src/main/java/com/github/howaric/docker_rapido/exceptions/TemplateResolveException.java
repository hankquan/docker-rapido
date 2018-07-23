package com.github.howaric.docker_rapido.exceptions;

public class TemplateResolveException extends RuntimeException {

    private static final long serialVersionUID = -1157908936313548989L;

    public TemplateResolveException() {
        super();
    }

    public TemplateResolveException(String message) {
        super(message);
    }

}
