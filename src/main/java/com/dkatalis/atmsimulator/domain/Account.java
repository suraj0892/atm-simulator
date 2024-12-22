package com.dkatalis.atmsimulator.domain;

import java.util.Map;
import java.util.Objects;

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

    public Map<User, Integer> getCreditMap() {
        return creditMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return user.equals(account.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public String toString() {
        return "Account{" +
                "user=" + user +
                ", balance=" + balance +
                ", creditMap=" + creditMap +
                '}';
    }
}
