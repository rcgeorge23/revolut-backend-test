package com.revolut.app.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public class Transaction {

    private Long id;
    private Timestamp transactionTimestamp;
    private Account sourceAccount;
    private Account destinationAccount;
    private BigDecimal amount;

    public Transaction(Long id, Timestamp transactionTimestamp, Account sourceAccount, Account destinationAccount, BigDecimal amount) {
        this.id = id;
        this.transactionTimestamp = transactionTimestamp;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(transactionTimestamp, that.transactionTimestamp) &&
                Objects.equals(sourceAccount, that.sourceAccount) &&
                Objects.equals(destinationAccount, that.destinationAccount) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionTimestamp, sourceAccount, destinationAccount, amount);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionTimestamp=" + transactionTimestamp +
                ", sourceAccount=" + sourceAccount +
                ", destinationAccount=" + destinationAccount +
                ", amount=" + amount +
                '}';
    }
}
