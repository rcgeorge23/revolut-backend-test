package com.revolut.app;

import com.google.inject.Guice;
import com.revolut.app.api.CreateTransactionRoute;
import com.revolut.app.api.GetTransactionsRoute;
import com.revolut.app.module.ApplicationModule;
import com.revolut.app.module.GsonResponseTransformer;
import com.revolut.app.validation.ValidationException;
import com.revolut.app.validation.ValidationExceptionHandler;

import javax.inject.Inject;

import static com.revolut.app.api.GetTransactionsRoute.PARAMETER_ACCOUNT_ID;
import static java.lang.String.format;
import static spark.Spark.*;

public class Application {

    private final GetTransactionsRoute getTransactionsRoute;
    private final CreateTransactionRoute createTransactionRoute;
    private final GsonResponseTransformer gsonResponseTransformer;
    private final ValidationExceptionHandler validationExceptionHandler;

    @Inject
    Application(
            final GetTransactionsRoute getTransactionsRoute,
            final CreateTransactionRoute createTransactionRoute,
            final GsonResponseTransformer gsonResponseTransformer,
            final ValidationExceptionHandler validationExceptionHandler) {
        this.getTransactionsRoute = getTransactionsRoute;
        this.createTransactionRoute = createTransactionRoute;
        this.gsonResponseTransformer = gsonResponseTransformer;
        this.validationExceptionHandler = validationExceptionHandler;
    }

    void start(final int port) {
        port(port);

        before((request, response) -> response.type("application/json"));

        exception(ValidationException.class, validationExceptionHandler);

        get(format("/transactions/%s", PARAMETER_ACCOUNT_ID), getTransactionsRoute, gsonResponseTransformer);

        post("/transaction", createTransactionRoute, gsonResponseTransformer);
    }

    public static void main(String[] args) {
        Guice.createInjector(new ApplicationModule())
                .getInstance(Application.class)
                .start(8080);
    }
}
