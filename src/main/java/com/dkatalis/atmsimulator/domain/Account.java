package com.dkatalis.atmsimulator.domain;

import java.util.Map;

public class Account {

    private final User user;
    private final Integer balance;
    private final Map<User, Integer> creditMap;

    public Account(User user, Integer balance, Map<User, Integer> creditMap) {
        this.balance = balance;
        this.creditMap = creditMap;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Integer getBalance() {
        return balance;
    }
}
