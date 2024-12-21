package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.repository.AccountRepository;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void createAccount(User user) {
        accountRepository.create(user);
    }

    public Integer getBalance(User user) {
        return accountRepository.getBalance(user);
    }
}
