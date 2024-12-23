package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.exception.AccountNotFoundException;
import com.dkatalis.atmsimulator.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    void init() {
        accountService = new AccountService(accountRepository);
    }

    @Test
    void createAccount_givenValidData_sucess() {
        User user =  new User("Alice");
        Account account = new Account(user, 0, new LinkedHashMap<>());
        doNothing().when(accountRepository).create(account);

        accountService.createAccount(user);

        verify(accountRepository).create(account);
    }

    @Test
    void getAccount_givenValidUser_returnsAccount() {
        User user =  new User("Alice");
        Account account = new Account(user, 0, new LinkedHashMap<>());

        doReturn(Optional.of(account)).when(accountRepository).getAccountByUser(user);

        Account actual = accountService.getAccount(user);

        assertEquals(actual, account);
        verify(accountRepository).getAccountByUser(user);
    }

    @Test
    void getBalance_givenValidUser_returnsAccount() {
        User user =  new User("Alice");
        Account account = new Account(user, 100, new LinkedHashMap<>());

        doReturn(Optional.of(account)).when(accountRepository).getAccountByUser(user);

        Integer actual = accountService.getBalance(user);

        assertEquals(100, actual);
    }

    @Test
    void getAccount_givenUserNotPresent_throwsException() {
        User user =  new User("Alice");

        doReturn(Optional.empty()).when(accountRepository).getAccountByUser(user);

        assertThrows(AccountNotFoundException.class , () -> accountService.getAccount(user));

        verify(accountRepository).getAccountByUser(user);
    }

    @Test
    void checkAccount_givenValidUser_returnsAccount() {
        User bob = new User("Bob");
        User user =  new User("Alice");
        Account account = new Account(user, 100, new LinkedHashMap<>());

        doReturn(Optional.of(account)).when(accountRepository).getAccountByUser(user);
        doReturn(Optional.empty()).when(accountRepository).getAccountByUser(bob);

        assertTrue(accountService.checkAccountIfPresentByUser(user));
        assertFalse(accountService.checkAccountIfPresentByUser(bob));
    }

    @Test
    void updateAccount_givenUpdatedAccountDetails_sucess() {
        User user = new User("Alice");
        Account account = new Account(user, 100, Collections.emptyMap());
        Account expectedAccount = new Account(user, 150, Collections.emptyMap());

        doNothing().when(accountRepository).update(account, expectedAccount);

        accountService.update(account, 150);

        verify(accountRepository).update(account, expectedAccount);
    }
}