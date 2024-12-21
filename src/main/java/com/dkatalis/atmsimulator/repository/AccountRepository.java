package com.dkatalis.atmsimulator.repository;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.exception.AccountNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AccountRepository {

    private final List<Account> accounts;

    public AccountRepository(List<Account> accounts) {
        this.accounts = accounts;
    }


    public void create(User user) {
        Account account = new Account(user, 0, new HashMap<>());
        accounts.add(account);
    }

    public Integer getBalance(User user) {
        Optional<Account> optionalAccount =  accounts.stream().filter(account -> account.getUser().equals(user)).findFirst();

        if(!optionalAccount.isPresent()) {
            throw new AccountNotFoundException("Account does not exist for user " + user.getUserName());
        }

        return optionalAccount.get().getBalance();
     }
}
