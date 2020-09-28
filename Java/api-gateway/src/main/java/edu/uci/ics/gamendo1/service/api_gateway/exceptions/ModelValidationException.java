package edu.uci.ics.gamendo1.service.api_gateway.exceptions;

public class ModelValidationException extends Exception {
    public ModelValidationException() {
    }

    public ModelValidationException(String message) {
        super(message);
    }

    public ModelValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
