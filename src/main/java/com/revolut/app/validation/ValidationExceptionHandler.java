package com.revolut.app.validation;

import com.google.gson.Gson;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class ValidationExceptionHandler implements ExceptionHandler {

    private final Gson gson;

    @Inject
    public ValidationExceptionHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handle(Exception exception, Request request, Response response) {
        if (exception instanceof ValidationException) {
            ValidationException validationException = (ValidationException) exception;
            response.status(validationException.getHttpStatusCode());
            response.body(gson.toJson(new Validation(validationException.getMessage())));
            return;
        }

        throw new RuntimeException(exception);
    }

}
