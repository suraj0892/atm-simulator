package com.dkatalis.atmsimulator;

import com.dkatalis.atmsimulator.repository.AccountRepository;
import com.dkatalis.atmsimulator.repository.UserRepository;
import com.dkatalis.atmsimulator.service.AccountService;
import com.dkatalis.atmsimulator.service.PrintService;
import com.dkatalis.atmsimulator.service.TransactionService;
import com.dkatalis.atmsimulator.service.UserService;

import java.util.ArrayList;
import java.util.Scanner;

public class AtmSimulatorApplication {

    public static void main(String[] args) {

        final UserRepository userRepository = new UserRepository(new ArrayList<>());
        final AccountRepository accountRepository = new AccountRepository(new ArrayList<>());
        final AccountService accountService = new AccountService(accountRepository);
        final UserService userService = new UserService(userRepository, accountService);
        final TransactionService transactionService = new TransactionService(userService, accountService);
        final PrintService printService = new PrintService(userService, accountService);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Dkatalis ATM \n");

        while (true) {
            System.out.print("$ ");
            String command = scanner.nextLine().trim();
            String[] commandParts = command.split(" ");

            if (commandParts.length == 0) {
                System.out.println("Invalid command. Try again.");
                continue;
            }

            switch (commandParts[0].toLowerCase()) {
                case "login":
                    if (commandParts.length == 2) {
                        userService.login(commandParts[1]);
                        printService.printWelcomeMessage();
                        printService.printAccountStatement();
                    } else {
                        System.out.println("Usage: login [name]");
                    }
                    break;
                case "deposit":
                    if (commandParts.length == 2) {
                        try {
                            Integer amount = Integer.parseInt(commandParts[1]);
                            transactionService.deposit(amount);
                            printService.printAccountStatement();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount.");
                        }
                    } else {
                        System.out.println("Usage: deposit [amount]");
                    }
                    break;
                case "withdraw":
                    if (commandParts.length == 2) {
                        try {
                            Integer amount = Integer.parseInt(commandParts[1]);
                            transactionService.withdraw(amount);
                            printService.printAccountStatement();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount.");
                        }
                    } else {
                        System.out.println("Usage: withdraw [amount]");
                    }
                    break;
                case "transfer":
                    if (commandParts.length == 3) {
                        try {
                            Integer amount = Integer.parseInt(commandParts[2]);
                            transactionService.transfer(commandParts[1], amount);
                            printService.printAccountStatement();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount.");
                        }
                    } else {
                        System.out.println("Usage: transfer [target] [amount]");
                    }
                    break;
                case "logout":
                    String loggedOutUser = userService.logout();
                    System.out.println("Good Bye, " + loggedOutUser);
                    break;
                case "exit":
                    if(userService.getLoggedInUser() == null) {
                        System.exit(0);
                    }
                    else {
                       System.out.println("please logout to exit");
                    }
                    break;
                default:
                    System.out.println("Invalid command. Try again.");
            }
        }
    }
}
