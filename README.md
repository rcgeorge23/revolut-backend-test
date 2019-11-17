#####Create a transaction
```curl -X POST localhost:8080/transaction -d '{"transactionTimestamp":1573838917528,"sourceAccount":{"id":1},"destinationAccount":{"id":2},"amount":100.00}'```

#####Get all transactions for an account
```curl -X GET localhost:8080/transactions/1```

#####Verify with maven
```{project root dir}/mvnw clean verify```

#####Implementation notes / limitations
* There are 4 static test accounts with ids ```1, 2, 3, 4```
* There are no endpoints for account CRUD operations
* Transaction dates are assumed to be UTC timestamps
* Transactions are immutable
* Currency is ignored
* Performance testing is out of scope
