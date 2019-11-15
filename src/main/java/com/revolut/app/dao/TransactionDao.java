package com.revolut.app.dao;

import com.revolut.app.model.Transaction;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TransactionDao {

    private List<Transaction> transactions = new ArrayList<>();

    public List<Transaction> findTransactions(Long accountId) {
        return transactions.stream().filter(transaction ->
                accountId.equals(transaction.getSourceAccount().getId()) ||
                        accountId.equals(transaction.getDestinationAccount().getId()))
                .collect(toList());
    }

    public void addTransaction(Transaction transaction) {
        transaction.setId(nextAvailableTransactionId());
        transactions.add(transaction);
    }

    private long nextAvailableTransactionId() {
        return transactions.isEmpty() ? 1L : transactions.get(transactions.size() - 1).getId();
    }
}
