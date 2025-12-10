package com.inditex.site.application.exception;

public class ExternalDependencyUnavailableException extends RuntimeException {
    public ExternalDependencyUnavailableException(String message) {
        super(message);
    }
}
