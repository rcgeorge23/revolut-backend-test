package com.revolut.app.dao;

import com.revolut.app.model.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountDao {

    private Map<Long, Account> accounts = new HashMap<>();

    public AccountDao() {
        accounts.put(1L, new Account(1L));
        accounts.put(2L, new Account(2L));
        accounts.put(3L, new Account(3L));
        accounts.put(4L, new Account(4L));
    }

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

}
