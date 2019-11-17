package com.revolut.app.validation;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class ExceptionHandler implements spark.ExceptionHandler {

    private Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    private final Gson gson;

    @Inject
    public ExceptionHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handle(Exception exception, Request request, Response response) {
        if (exception instanceof ValidationException) {
            log.warn("A validation error occurred: {}", exception.getMessage());
            log.debug("Validation error cause", exception);

            ValidationException validationException = (ValidationException) exception;
            response.status(validationException.getHttpStatusCode());
            response.body(gson.toJson(new Validation(validationException.getMessage())));
            return;
        }

        log.error("An unexpected error occurred", exception);
        throw new RuntimeException(exception);
    }

}
