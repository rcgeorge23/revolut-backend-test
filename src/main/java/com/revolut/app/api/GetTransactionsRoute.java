package com.revolut.app.api;

import com.revolut.app.dao.AccountDao;
import com.revolut.app.dao.TransactionDao;
import com.revolut.app.validation.ValidationException;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

import static java.lang.Long.parseLong;
import static java.lang.String.format;

public class GetTransactionsRoute implements Route {

    public static final String PARAMETER_ACCOUNT_ID = ":accountId";

    private final TransactionDao transactionDao;
    private final AccountDao accountDao;

    @Inject
    public GetTransactionsRoute(
            final TransactionDao transactionDao,
            final AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
    }

    @Override
    public Object handle(Request request, Response response) {
        String accountIdString = request.params(PARAMETER_ACCOUNT_ID);

        if (!isLong(accountIdString)) {
            throw new ValidationException(400, format("account id %s cannot be parsed", accountIdString));
        }

        long accountId = parseLong(accountIdString);

        if (!accountDao.findById(accountId).isPresent()) {
            throw new ValidationException(404, format("account id %d cannot be found", accountId));
        }

        return transactionDao.findTransactions(accountId);
    }

    private boolean isLong(String accountIdString) {
        try {
            Long.parseLong(accountIdString);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
