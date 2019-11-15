package com.revolut.app.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spark.Request;
import spark.Response;

import static com.revolut.app.module.ApplicationModule.GSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class ValidationExceptionHandlerTest {

    private ValidationExceptionHandler testObj;

    @Mock
    private Request requestMock;

    @Mock
    private Response responseMock;

    @BeforeEach
    public void beforeEach() {
        testObj = new ValidationExceptionHandler(GSON);
    }

    @Test
    public void validationExceptionReturnsMessageAndHttpStatusCode() {
        testObj.handle(new ValidationException(404, "Not found"), requestMock, responseMock);
        verify(responseMock).status(404);
        verify(responseMock).body(GSON.toJson(new Validation("Not found")));
    }

    @Test
    public void otherExceptionIsWrappedAndRethrown() {
        Exception exception = new Exception();

        Exception actualException = Assertions.assertThrows(RuntimeException.class, () -> testObj.handle(exception, requestMock, responseMock));

        verifyNoMoreInteractions(responseMock);
        assertTrue(actualException instanceof RuntimeException);
        assertEquals(exception, actualException.getCause());
    }

}
