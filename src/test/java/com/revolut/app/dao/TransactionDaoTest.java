package com.revolut.app.dao;

import com.revolut.app.model.Account;
import com.revolut.app.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static java.sql.Timestamp.from;
import static java.time.ZoneId.of;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TransactionDaoTest {

    private static final Account SOURCE_ACCOUNT_1 = new Account(100L);
    private static final Account DESTINATION_ACCOUNT_1 = new Account(101L);
    private static final Account SOURCE_ACCOUNT_2 = new Account(200L);
    private static final Account DESTINATION_ACCOUNT_2 = new Account(201L);

    private static final Long ACCOUNT_ID_WITH_NO_TRANSACTIONS = 300L;

    private Transaction transaction1 = new Transaction(
            null,
            from(now(of("UTC")).toInstant()),
            SOURCE_ACCOUNT_1,
            DESTINATION_ACCOUNT_1,
            new BigDecimal("100.00")
    );

    private Transaction transaction2 = new Transaction(
            null,
            from(now(of("UTC")).toInstant()),
            SOURCE_ACCOUNT_2,
            DESTINATION_ACCOUNT_2,
            new BigDecimal("200.00")
    );

    private TransactionDao testObj;

    @BeforeEach
    public void beforeEach() {
        testObj = new TransactionDao();
    }

    @Test
    public void findTransactionsReturnsEmptyListWhenTransactionNotFoundForAccountId() {
        assertEquals(emptyList(), testObj.findTransactions(ACCOUNT_ID_WITH_NO_TRANSACTIONS));
    }

    @Test
    public void canAddAndRetrieveATransactionUsingTheSourceAccountId() {
        testObj.addTransaction(transaction1);
        testObj.addTransaction(transaction2);
        assertEquals(singletonList(transaction1), testObj.findTransactions(SOURCE_ACCOUNT_1.getId()));
    }

    @Test
    public void canAddAndRetrieveATransactionUsingTheDestinationAccountId() {
        testObj.addTransaction(transaction1);
        testObj.addTransaction(transaction2);
        assertEquals(singletonList(transaction1), testObj.findTransactions(DESTINATION_ACCOUNT_1.getId()));
    }

    @Test
    public void nextAvailableTransactionIdIsAssignedToNewTransaction() {
        testObj.addTransaction(transaction1);
        assertEquals(1L, transaction1.getId());
    }
}