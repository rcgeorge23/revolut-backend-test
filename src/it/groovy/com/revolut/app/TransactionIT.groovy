package com.revolut.app

import com.revolut.app.model.Account
import com.revolut.app.model.Transaction
import groovyx.net.http.RESTClient
import spock.lang.Specification

import java.sql.Timestamp
import java.time.Instant

import static groovyx.net.http.ContentType.JSON

class TransactionIT extends Specification {

    public static final Timestamp TRANSACTION_TIMESTAMP = new Timestamp(Instant.now().toEpochMilli())
    public static final long NON_EXISTANT_ACCOUNT = 100L
    public static final long ACCOUNT_1 = 1L
    public static final long ACCOUNT_2 = 2L

    RESTClient restClient

    def setup() {
        restClient = new RESTClient("http://localhost:8080", JSON)
    }

    def "get transactions returns empty list when account exists but does not have any transactions"() {
        when:
        def response = restClient.get(
                path: "/transactions/1",
                requestContentType: JSON
        )

        then:
        response.status == 200
        response.data.size == 0
    }

    def "create transaction returns response with http status 201 and body of persisted transaction with id assigned"() {
        when:
        def response = restClient.post(
                path: "/transaction",
                body: new Transaction(null, TRANSACTION_TIMESTAMP, Account.of(ACCOUNT_1), Account.of(ACCOUNT_2), new BigDecimal("100.00")),
                requestContentType: JSON)

        then:
        response.status == 201
        response.data.id == 1
        response.data.sourceAccount.id == 1
        response.data.destinationAccount.id == 2
        response.data.amount == 100
    }

    def "create transaction returns 404 when source account does not exist"() {
        expect:

        try {
            restClient.post(
                    path: "/transaction",
                    body: new Transaction(null, TRANSACTION_TIMESTAMP, Account.of(NON_EXISTANT_ACCOUNT), Account.of(ACCOUNT_2), new BigDecimal("100.00")),
                    requestContentType: JSON)
        } catch (ex) {
            assert ex.response.status == 404
            assert ex.response.data.message == "sourceAccount could not be found"
        }
    }

    def "create transaction returns 404 when destination account does not exist"() {
        expect:

        try {
            restClient.post(
                    path: "/transaction",
                    body: new Transaction(null, TRANSACTION_TIMESTAMP, Account.of(ACCOUNT_1), Account.of(NON_EXISTANT_ACCOUNT), new BigDecimal("100.00")),
                    requestContentType: JSON)
        } catch (ex) {
            assert ex.response.status == 404
            assert ex.response.data.message == "destinationAccount could not be found"
        }
    }
}
