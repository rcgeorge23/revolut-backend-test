#####Create a transaction
```curl -X POST localhost:8080/transaction -d '{"transactionTimestamp":1573838917528,"sourceAccount":{"id":1},"destinationAccount":{"id":2},"amount":100.00}'```

#####Get all transactions for an account
```curl -X GET localhost:8080/transactions/1```

#####Implementation notes / limitations
*