package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;

public class TransactionService {

    private final UserService userService;
    private final AccountService accountService;

    public TransactionService(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public void deposit(Integer amount) {
        User loggedInUser = userService.getLoggedInUser();
        Account userAccount = accountService.getAccount(loggedInUser);
        Integer newBalance = userAccount.getBalance() + amount;
        accountService.update(userAccount, newBalance);
    }

    public void withdraw(Integer amount) {
        User loggedInUser = userService.getLoggedInUser();
        Account userAccount = accountService.getAccount(loggedInUser);
        Integer newBalance = userAccount.getBalance() - amount;
        accountService.update(userAccount, newBalance);
    }

}
