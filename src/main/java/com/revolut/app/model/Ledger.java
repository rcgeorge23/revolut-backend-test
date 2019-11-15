package com.revolut.app.model;

import java.util.ArrayList;
import java.util.List;

public class Ledger {
    private List<Transaction> transactions;

    public Ledger() {
        this.transactions = new ArrayList<>();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
