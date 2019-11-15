package com.revolut.app.api;

import com.revolut.app.dao.AccountDao;
import com.revolut.app.dao.TransactionDao;
import com.revolut.app.model.Account;
import com.revolut.app.model.Transaction;
import com.revolut.app.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import static java.lang.Long.parseLong;
import static java.time.ZoneId.of;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class GetTransactionsRouteTest {

    private static final Account SOURCE_ACCOUNT_1 = new Account(100L);
    private static final Account DESTINATION_ACCOUNT_1 = new Account(101L);

    private static final Transaction TRANSACTION_1 = new Transaction(
            1L,
            new Timestamp(now(of("UTC")).toInstant().toEpochMilli()),
            SOURCE_ACCOUNT_1,
            DESTINATION_ACCOUNT_1,
            new BigDecimal("100.00")
    );

    private static final Transaction TRANSACTION_2 = new Transaction(
            2L,
            new Timestamp(now(of("UTC")).toInstant().toEpochMilli()),
            SOURCE_ACCOUNT_1,
            DESTINATION_ACCOUNT_1,
            new BigDecimal("200.00")
    );

    private static final String ACCOUNT_ID = "100";
    public static final String UNPARSEABLE_ACCOUNT_ID = "UNPARSEABLE_ACCOUNT_ID";

    private GetTransactionsRoute testObj;

    @Mock
    private TransactionDao transactionDaoMock;

    @Mock
    private AccountDao accountDaoMock;

    @Mock
    private Request requestMock;

    @Mock
    private Response responseMock;

    @Mock
    private Account accountMock;

    @BeforeEach
    public void beforeEach() {
        when(accountDaoMock.findById(parseLong(ACCOUNT_ID))).thenReturn(Optional.of(accountMock));
        testObj = new GetTransactionsRoute(transactionDaoMock, accountDaoMock);
    }

    @Test
    public void returnsEmptyCollectionWhenAccountHasNoTransactions() {
        when(requestMock.params(":accountId")).thenReturn(ACCOUNT_ID);
        assertEquals(emptyList(), testObj.handle(requestMock, responseMock));
    }

    @Test
    public void throws404ValidationExceptionWhenAccountCannotBeFound() {
        when(requestMock.params(":accountId")).thenReturn(ACCOUNT_ID);
        when(accountDaoMock.findById(parseLong(ACCOUNT_ID))).thenReturn(Optional.empty());
        ValidationException validationException = assertThrows(ValidationException.class, () -> testObj.handle(requestMock, responseMock));
        assertEquals(404, validationException.getHttpStatusCode());
        assertEquals("account id " + ACCOUNT_ID + " cannot be found", validationException.getMessage());
    }

    @Test
    public void throws400ValidationExceptionWhenAccountIdCannotBeParsed() {
        when(requestMock.params(":accountId")).thenReturn(UNPARSEABLE_ACCOUNT_ID);
        ValidationException validationException = assertThrows(ValidationException.class, () -> testObj.handle(requestMock, responseMock));
        assertEquals(400, validationException.getHttpStatusCode());
        assertEquals("account id " + UNPARSEABLE_ACCOUNT_ID + " cannot be parsed", validationException.getMessage());
    }

    @Test
    public void rendersTransactionJsonForOneAccountWhenTransactionIsFoundForAccount() {
        when(requestMock.params(":accountId")).thenReturn(ACCOUNT_ID);
        when(transactionDaoMock.findTransactions(parseLong(ACCOUNT_ID))).thenReturn(singletonList(TRANSACTION_1));
        assertEquals(singletonList(TRANSACTION_1), testObj.handle(requestMock, responseMock));
    }

    @Test
    public void rendersTransactionJsonForTwoAccountWhenTwoTransactionsAreFoundForAccount() {
        when(requestMock.params(":accountId")).thenReturn(ACCOUNT_ID);
        when(transactionDaoMock.findTransactions(parseLong(ACCOUNT_ID))).thenReturn(asList(TRANSACTION_1, TRANSACTION_2));
        assertEquals(asList(TRANSACTION_1, TRANSACTION_2), testObj.handle(requestMock, responseMock));
    }
}