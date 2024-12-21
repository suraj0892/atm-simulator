package com.dkatalis.atmsimulator;

import com.dkatalis.atmsimulator.repository.AccountRepository;
import com.dkatalis.atmsimulator.repository.UserRepository;
import com.dkatalis.atmsimulator.service.AccountService;
import com.dkatalis.atmsimulator.service.TransactionService;
import com.dkatalis.atmsimulator.service.UserService;

import java.util.ArrayList;

public class AtmSimulatorApplication {

    public static void main(String[] args) {

        final UserRepository userRepository = new UserRepository(new ArrayList<>());
        final AccountRepository accountRepository = new AccountRepository(new ArrayList<>());
        final AccountService accountService = new AccountService(accountRepository);
        final UserService userService = new UserService(userRepository, accountService);
        final TransactionService transactionService = new TransactionService(userService, accountService);

        userService.login("Alice");
        System.out.println("Hello, " + userService.getLoggedInUser().getUserName());
        System.out.println("Your balance is $" + userService.getBalance());

        transactionService.deposit(100);
        System.out.println("Your balance is $" + userService.getBalance());
        transactionService.deposit(100);
        System.out.println("Your balance is $" + userService.getBalance());

        transactionService.withdraw(50);
        System.out.println("Your balance is $" + userService.getBalance());
//
//        user = transactionService.withdraw(10);
//        System.out.println("Your balance is $" + user.getBalance());
//
        String loggedOutUser = userService.logout();
        System.out.println("Good Bye, " + loggedOutUser);

    }
}
