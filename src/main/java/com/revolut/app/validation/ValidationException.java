package com.revolut.app.validation;

public class ValidationException extends RuntimeException {

    private int httpStatusCode;

    public ValidationException(int httpStatusCode, String validationMessage) {
        super(validationMessage);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
