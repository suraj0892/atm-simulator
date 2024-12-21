package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.exception.AccountNotFoundException;
import com.dkatalis.atmsimulator.repository.AccountRepository;

import java.util.LinkedHashMap;
import java.util.Optional;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void createAccount(User user) {
        Account account = new Account(user, 0, new LinkedHashMap<>());
        accountRepository.create(account);
    }

    public Integer getBalance(User user) {
        return getAccount(user).getBalance();
    }

    public Account getAccount(User user) {
        Optional<Account> optionalAccount = accountRepository.getAccountByUser(user);
        if (!optionalAccount.isPresent()) {
            throw new AccountNotFoundException("Account does not exist for user " + user.getUserName());
        }
        return optionalAccount.get();
    }

    public void update(Account outdatedAccount, Integer updatedBalance) {
        Account updatedAccount = new Account(outdatedAccount.getUser(), updatedBalance, outdatedAccount.getCreditMap());
        accountRepository.update(outdatedAccount, updatedAccount);
    }


}
