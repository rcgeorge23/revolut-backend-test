package com.revolut.app.api;

import com.google.gson.JsonSyntaxException;
import com.revolut.app.dao.AccountDao;
import com.revolut.app.dao.TransactionDao;
import com.revolut.app.model.Account;
import com.revolut.app.model.Transaction;
import com.revolut.app.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
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

import static com.revolut.app.module.ApplicationModule.GSON;
import static java.time.ZoneId.of;
import static java.time.ZonedDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class CreateTransactionRouteTest {

    private static final Account SOURCE_ACCOUNT_1 = new Account(100L);
    private static final Account DESTINATION_ACCOUNT_1 = new Account(101L);

    private static final Transaction TRANSACTION_1 = new Transaction(
            1L,
            new Timestamp(now(of("UTC")).toInstant().toEpochMilli()),
            SOURCE_ACCOUNT_1,
            DESTINATION_ACCOUNT_1,
            new BigDecimal("100.00")
    );

    private CreateTransactionRoute testObj;

    @Mock
    private TransactionDao transactionDaoMock;

    @Mock
    private Request requestMock;

    @Mock
    private Response responseMock;

    @Mock
    private AccountDao accountDaoMock;

    @BeforeEach
    public void beforeEach() {
        testObj = new CreateTransactionRoute(transactionDaoMock, accountDaoMock, GSON);
    }

    @Test
    public void canAddNewTransaction() {
        when(accountDaoMock.findById(SOURCE_ACCOUNT_1.getId())).thenReturn(Optional.of(SOURCE_ACCOUNT_1));
        when(accountDaoMock.findById(DESTINATION_ACCOUNT_1.getId())).thenReturn(Optional.of(DESTINATION_ACCOUNT_1));
        when(requestMock.body()).thenReturn(GSON.toJson(TRANSACTION_1));

        testObj.handle(requestMock, responseMock);

        verify(responseMock).status(201);
        verify(transactionDaoMock).addTransaction(TRANSACTION_1);
    }

    @Test
    public void cannotAddNewTransactionIfSourceAccountCannotBeFound() {
        when(requestMock.body()).thenReturn(GSON.toJson(TRANSACTION_1));
        when(accountDaoMock.findById(SOURCE_ACCOUNT_1.getId())).thenReturn(Optional.empty());

        ValidationException validationException = Assertions.assertThrows(ValidationException.class, () -> testObj.handle(requestMock, responseMock));

        assertEquals(400, validationException.getHttpStatusCode());
        assertEquals("sourceAccount could not be found", validationException.getMessage());
        verifyNoMoreInteractions(transactionDaoMock);
    }

    @Test
    public void cannotAddNewTransactionIfDestinationAccountCannotBeFound() {
        when(requestMock.body()).thenReturn(GSON.toJson(TRANSACTION_1));
        when(accountDaoMock.findById(SOURCE_ACCOUNT_1.getId())).thenReturn(Optional.of(SOURCE_ACCOUNT_1));
        when(accountDaoMock.findById(DESTINATION_ACCOUNT_1.getId())).thenReturn(Optional.empty());

        ValidationException validationException = Assertions.assertThrows(ValidationException.class, () -> testObj.handle(requestMock, responseMock));

        assertEquals(400, validationException.getHttpStatusCode());
        assertEquals("destinationAccount could not be found", validationException.getMessage());
        verifyNoMoreInteractions(transactionDaoMock);
    }

    @Test
    public void cannotAddNewTransactionIfPayloadCannotBeParsedToATransaction() {
        when(requestMock.body()).thenReturn("unparseable payload");

        ValidationException validationException = Assertions.assertThrows(ValidationException.class, () -> testObj.handle(requestMock, responseMock));

        assertEquals(400, validationException.getHttpStatusCode());
        assertEquals("payload could not be parsed", validationException.getMessage());
        Assertions.assertTrue(validationException.getCause() instanceof JsonSyntaxException);
        verifyNoMoreInteractions(transactionDaoMock);
    }
}