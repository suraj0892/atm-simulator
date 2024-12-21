package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.exception.BusinessException;


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

        deposit(amount, userAccount);
    }

    public void withdraw(Integer amount) {
        User loggedInUser = userService.getLoggedInUser();
        Account userAccount = accountService.getAccount(loggedInUser);
        if (userAccount.getBalance() <= 0) {
            throw new BusinessException("No enough funds to withdraw");
        }
        withdraw(amount, userAccount);
    }

    public void transfer(String userName, Integer amount) {
        User loggedInUser = userService.getLoggedInUser();
        User beneficiary = userService.getUserByUserName(userName);
        Account loggedInUserAccount = accountService.getAccount(loggedInUser);
        Account beneficiaryAccount =  accountService.getAccount(beneficiary);
        withdraw(amount, loggedInUserAccount);
        deposit(amount, beneficiaryAccount);
    }

    private void deposit(Integer amount, Account account) {
        Integer newBalance = account.getBalance() + amount;
        accountService.update(account, newBalance);
    }

    private void withdraw(Integer amount, Account account) {
        Integer newBalance = account.getBalance() - amount;
        accountService.update(account, newBalance);
    }
}
