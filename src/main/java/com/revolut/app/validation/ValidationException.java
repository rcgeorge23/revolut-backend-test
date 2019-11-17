package com.revolut.app.validation;

public class ValidationException extends RuntimeException {

    private int httpStatusCode;

    public ValidationException(int httpStatusCode, String validationMessage) {
        this(httpStatusCode, validationMessage, null);
    }

    public ValidationException(int httpStatusCode, String validationMessage, Throwable cause) {
        super(validationMessage, cause);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
