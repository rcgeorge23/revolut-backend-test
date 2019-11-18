package com.revolut.app

import com.revolut.app.model.Account
import com.revolut.app.model.Transaction
import groovyx.net.http.RESTClient
import spock.lang.Specification

import java.sql.Timestamp

import static groovyx.net.http.ContentType.JSON
import static java.time.Instant.now

class TransactionIT extends Specification {

    static final Timestamp TRANSACTION_TIMESTAMP = new Timestamp(now().toEpochMilli())
    static final long NON_EXISTENT_ACCOUNT = 100L
    static final long ACCOUNT_1 = 1L
    static final long ACCOUNT_2 = 2L

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
        when: "we create a new transaction"
        def postResponse = restClient.post(
                path: "/transaction",
                body: new Transaction(null, TRANSACTION_TIMESTAMP, Account.of(ACCOUNT_1), Account.of(ACCOUNT_2), new BigDecimal("100.00")),
                requestContentType: JSON
        )

        then: "response payload contains the newly created transaction with an id assigned"
        postResponse.status == 201
        postResponse.data.id == 1
        postResponse.data.sourceAccount.id == 1
        postResponse.data.destinationAccount.id == 2
        postResponse.data.amount == 100

        and: "we can now get the transaction we've created for account 1"
        def getResponse1 = restClient.get(
                path: "/transactions/1",
                requestContentType: JSON
        )

        then:
        getResponse1.status == 200
        getResponse1.data.size == 1
        getResponse1.data[0].id == 1
        getResponse1.data[0].sourceAccount.id == 1
        getResponse1.data[0].destinationAccount.id == 2
        getResponse1.data[0].amount == 100

        and: "we can now get the transaction we've created for account 2"
        def getResponse2 = restClient.get(
                path: "/transactions/2",
                requestContentType: JSON
        )

        then:
        getResponse2.status == 200
        getResponse2.data.size == 1
        getResponse2.data[0].id == 1
        getResponse2.data[0].sourceAccount.id == 1
        getResponse2.data[0].destinationAccount.id == 2
        getResponse2.data[0].amount == 100
    }

    def "create transaction returns 400 when source account does not exist"() {
        expect:

        try {
            restClient.post(
                    path: "/transaction",
                    body: new Transaction(null, TRANSACTION_TIMESTAMP, Account.of(NON_EXISTENT_ACCOUNT), Account.of(ACCOUNT_2), new BigDecimal("100.00")),
                    requestContentType: JSON
            )
        } catch (ex) {
            assert ex.response.status == 400
            assert ex.response.data.message == "sourceAccount could not be found"
        }
    }

    def "create transaction returns 400 when destination account does not exist"() {
        expect:

        try {
            restClient.post(
                    path: "/transaction",
                    body: new Transaction(null, TRANSACTION_TIMESTAMP, Account.of(ACCOUNT_1), Account.of(NON_EXISTENT_ACCOUNT), new BigDecimal("100.00")),
                    requestContentType: JSON
            )
        } catch (ex) {
            assert ex.response.status == 400
            assert ex.response.data.message == "destinationAccount could not be found"
        }
    }
}
