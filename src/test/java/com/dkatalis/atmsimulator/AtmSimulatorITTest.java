package com.dkatalis.atmsimulator;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.repository.AccountRepository;
import com.dkatalis.atmsimulator.repository.UserRepository;
import com.dkatalis.atmsimulator.service.AccountService;
import com.dkatalis.atmsimulator.service.TransactionService;
import com.dkatalis.atmsimulator.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AtmSimulatorITTest {

    final UserRepository userRepository = new UserRepository(new ArrayList<>());
    final AccountRepository accountRepository = new AccountRepository(new ArrayList<>());
    final AccountService accountService = new AccountService(accountRepository);
    final UserService userService = new UserService(userRepository, accountService);
    final TransactionService transactionService = new TransactionService(userService, accountService);


    @Test
    void performTransactions() {
        //login Alice and deposit amount to alice
        userService.login("Alice");
        transactionService.deposit(100);
        userService.logout();

        Account alice = accountService.getAccount(userService.getUserByUserName("Alice"));
        assertEquals(100, alice.getBalance());
        assertEquals(0, alice.getCreditMap().size());

        //login Alice and deposit amount to alice
        userService.login("Bob");
        transactionService.deposit(80);
        userService.logout();

        Account bob = accountService.getAccount(userService.getUserByUserName("Bob"));
        assertEquals(80, bob.getBalance());
        assertEquals(0, bob.getCreditMap().size());

        //login Alice and deposit amount to alice
        userService.login("Bob");
        transactionService.transfer("Alice", 50);
        alice = accountService.getAccount(userService.getUserByUserName("Alice"));
        bob = accountService.getAccount(userService.getUserByUserName("Bob"));

        assertEquals(150, alice.getBalance());
        assertEquals(30, bob.getBalance());

        transactionService.transfer("Alice", 100);
        alice = accountService.getAccount(userService.getUserByUserName("Alice"));
        bob = accountService.getAccount(userService.getUserByUserName("Bob"));

        assertEquals(180, alice.getBalance());
        assertEquals(70, alice.getCreditMap().get(userService.getUserByUserName("Bob")));
        assertEquals(0, bob.getBalance());
        assertEquals(-70, bob.getCreditMap().get(userService.getUserByUserName("Alice")));

        transactionService.deposit(30);
        alice = accountService.getAccount(userService.getUserByUserName("Alice"));
        bob = accountService.getAccount(userService.getUserByUserName("Bob"));

        assertEquals(210, alice.getBalance());
        assertEquals(40, alice.getCreditMap().get(userService.getUserByUserName("Bob")));
        assertEquals(0, bob.getBalance());
        assertEquals(-40, bob.getCreditMap().get(userService.getUserByUserName("Alice")));

        userService.logout();

        userService.login("Alice");
        transactionService.transfer("Bob", 30);
        alice = accountService.getAccount(userService.getUserByUserName("Alice"));
        bob = accountService.getAccount(userService.getUserByUserName("Bob"));

        assertEquals(210, alice.getBalance());
        assertEquals(10, alice.getCreditMap().get(userService.getUserByUserName("Bob")));
        assertEquals(0, bob.getBalance());
        assertEquals(-10, bob.getCreditMap().get(userService.getUserByUserName("Alice")));

        userService.logout();


        userService.login("Bob");
        transactionService.deposit(100);
        alice = accountService.getAccount(userService.getUserByUserName("Alice"));
        bob = accountService.getAccount(userService.getUserByUserName("Bob"));

        assertEquals(220, alice.getBalance());
        assertNull(alice.getCreditMap().get(userService.getUserByUserName("Bob")));
        assertEquals(90, bob.getBalance());
        assertNull(bob.getCreditMap().get(userService.getUserByUserName("Alice")));

        transactionService.withdraw(20);
        bob = accountService.getAccount(userService.getUserByUserName("Bob"));
        assertEquals(70, bob.getBalance());

        userService.logout();
    }
}
