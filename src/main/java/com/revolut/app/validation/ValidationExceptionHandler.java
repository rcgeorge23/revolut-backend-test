package com.revolut.app.validation;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class ValidationExceptionHandler implements ExceptionHandler {

    private Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    private final Gson gson;

    @Inject
    public ValidationExceptionHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handle(Exception exception, Request request, Response response) {
        if (exception instanceof ValidationException) {
            log.warn("A validation error occurred: {}", exception.getMessage());

            ValidationException validationException = (ValidationException) exception;
            response.status(validationException.getHttpStatusCode());
            response.body(gson.toJson(new Validation(validationException.getMessage())));
            return;
        }

        log.error("An expected error occurred", exception);
        throw new RuntimeException(exception);
    }

}
