package com.revolut.app.api;

import com.google.gson.Gson;
import com.revolut.app.dao.AccountDao;
import com.revolut.app.dao.TransactionDao;
import com.revolut.app.model.Account;
import com.revolut.app.model.Transaction;
import com.revolut.app.validation.ValidationException;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

import static java.lang.String.format;

public class CreateTransactionRoute implements Route {

    private static final String VALIDATION_MESSAGE_ACCOUNT_NOT_FOUND = "%s account could not be found";

    private final TransactionDao transactionDao;
    private final AccountDao accountDao;
    private final Gson gson;

    @Inject
    public CreateTransactionRoute(
            final TransactionDao transactionDao,
            final AccountDao accountDao,
            final Gson gson) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
        this.gson = gson;
    }

    @Override
    public Object handle(Request request, Response response) {
        Transaction transaction;

        try {
            transaction = gson.fromJson(request.body(), Transaction.class);
        } catch (Exception e) {
            throw new ValidationException(400, "payload could not be parsed");
        }

        if (accountNotFound(transaction.getSourceAccount())) {
            throw new ValidationException(400, format(VALIDATION_MESSAGE_ACCOUNT_NOT_FOUND, "source"));
        }

        if (accountNotFound(transaction.getDestinationAccount())) {
            throw new ValidationException(400, format(VALIDATION_MESSAGE_ACCOUNT_NOT_FOUND, "destination"));
        }

        transactionDao.addTransaction(transaction);

        response.status(201);
        return transaction;
    }

    private boolean accountNotFound(Account account) {
        return accountDao.findById(account.getId()).isEmpty();
    }
}
