package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;

import java.util.Map;

public class PrintService {

    private final UserService userService;
    private final AccountService accountService;

    public PrintService(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public void printWelcomeMessage() {
        System.out.println("Hello, " + userService.getLoggedInUser().getUserName());
    }

    public void printAccountStatement() {
        System.out.println("Your balance is $" + userService.getBalance());

        Account account = accountService.getAccount(userService.getLoggedInUser());

        for (Map.Entry<User, Integer> entry: account.getCreditMap().entrySet()) {
            if (entry.getValue() < 0) {
                System.out.println("Owed $" + Math.abs(entry.getValue()) + " to " + entry.getKey().getUserName());
            }
            else {
                System.out.println("Owed $" + Math.abs(entry.getValue()) + " from " + entry.getKey().getUserName());
            }
        }
    }
}
