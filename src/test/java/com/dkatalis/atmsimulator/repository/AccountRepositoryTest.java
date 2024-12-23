package com.dkatalis.atmsimulator.repository;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private AccountRepository accountRepository;
    private List<Account> accounts;

    @BeforeEach
    void setUp() {
        accounts = new ArrayList<>();
       accountRepository = new AccountRepository(accounts);
    }

    @Test
    void updateAccount_withValidData_success() {
        Account account = new Account(new User("Alice"),100, Collections.emptyMap());
        accounts.add(account);
        Account updatedAccount = new Account(new User("Alice"),90, Collections.emptyMap());

        accountRepository.update(account, updatedAccount);

        assertEquals(1, accounts.size());
        assertEquals(updatedAccount.getBalance(), accounts.get(0).getBalance());

    }

    @Test
    void createAccount_withValidData_success() {
        Account account = new Account(new User("Alice"),100, Collections.emptyMap());

        accountRepository.create(account);

        assertEquals(1, accounts.size());
    }

    @Test
    void getAccountByUser_givenValidUser_returnsAccount() {
        Account aliceAccount = new Account(new User("Alice"),100, Collections.emptyMap());
        accounts.add(aliceAccount);
        Account bobAccount = new Account(new User("Bob"),90, Collections.emptyMap());
        accounts.add(bobAccount);

        Optional<Account> actual = accountRepository.getAccountByUser(new User("Alice"));
        assertTrue(actual.isPresent());
        assertEquals(aliceAccount, actual.get());

        actual = accountRepository.getAccountByUser(new User("John"));
        assertFalse(actual.isPresent());

    }
}