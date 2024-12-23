package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @BeforeEach
    void init() {
        transactionService = new TransactionService(userService, accountService);
    }

    @Test
    void depositAmount_givenUserLoggedIn_success() {
        User alice = getNewUser("Alice");
        Account aliceAccount = getAccountForUser(alice, 100);

        doReturn(alice).when(userService).getLoggedInUser();
        doReturn(aliceAccount).when(accountService).getAccount(alice);
        doNothing().when(accountService).update(aliceAccount, 400);

        transactionService.deposit(300);

        verify(userService).getLoggedInUser();
        verify(accountService).getAccount(alice);
        verify(accountService).update(aliceAccount, 400);
    }

    @Test
    void withdrawAmount_givenUserLoggedIn_success() {
        User alice = getNewUser("Alice");
        Account aliceAccount = getAccountForUser(alice, 100);

        doReturn(alice).when(userService).getLoggedInUser();
        doReturn(aliceAccount).when(accountService).getAccount(alice);
        doNothing().when(accountService).update(aliceAccount, 0);

        transactionService.withdraw(100);

        verify(userService).getLoggedInUser();
        verify(accountService).getAccount(alice);
        verify(accountService).update(aliceAccount, 0);
    }

    @Test
    void withdrawAmount_givenUserLoggedInWithInsufficientFunds_throwsException() {
        User alice = getNewUser("Alice");
        Account aliceAccount = getAccountForUser(alice, 50);

        doReturn(alice).when(userService).getLoggedInUser();
        doReturn(aliceAccount).when(accountService).getAccount(alice);

        assertThrows(BusinessException.class, () -> transactionService.withdraw(100));

        verify(accountService).getAccount(alice);

        User bob = getNewUser("Bob");
        Account bobAccount = getAccountForUser(bob, 0);

        doReturn(bob).when(userService).getLoggedInUser();
        doReturn(bobAccount).when(accountService).getAccount(bob);

        assertThrows(BusinessException.class, () -> transactionService.withdraw(100));

        verify(userService, times(2)).getLoggedInUser();
        verify(accountService).getAccount(bob);
    }

    @Test
    void transferAmount_givenBeneficiarySameAsLoggedInUser_exception() {
        User alice = getNewUser("Alice");
        Account aliceAccount = getAccountForUser(alice, 50);

        doReturn(alice).when(userService).getLoggedInUser();
        doReturn(aliceAccount).when(accountService).getAccount(alice);

        assertThrows(BusinessException.class, () -> transactionService.transfer("Alice", 100));
    }

    @Test
    void transferAmount_givenLoggedInUserHasZeroBalance_exception() {
        User alice = getNewUser("Alice");
        Account aliceAccount = getAccountForUser(alice, 0);

        doReturn(alice).when(userService).getLoggedInUser();
        doReturn(aliceAccount).when(accountService).getAccount(alice);

        assertThrows(BusinessException.class, () -> transactionService.transfer("Bob", 100));
    }

    @Test
    void transferAmount_givenValidUsersWithFunds_success() {
        User alice = getNewUser("Alice");
        Account aliceAccount = getAccountForUser(alice, 100);

        User bob = getNewUser("Bob");
        Account bobAccount = getAccountForUser(bob, 100);

        doReturn(alice).when(userService).getLoggedInUser();
        doReturn(aliceAccount).when(accountService).getAccount(alice);
        doReturn(bob).when(userService).getUserByUserName("Bob");
        doReturn(bobAccount).when(accountService).getAccount(bob);
        doNothing().when(accountService).update(aliceAccount, 50);
        doNothing().when(accountService).update(bobAccount, 150);

        transactionService.transfer("Bob", 50);

        verify(accountService).update(aliceAccount, 50);
        verify(accountService).update(bobAccount, 150);

    }

    @Test
    void transferAmount_givenValidUsersWithFundsInDebt_success() {
        User alice = getNewUser("Alice");
        Account aliceAccount = getAccountForUser(alice, 100);

        User bob = getNewUser("Bob");
        Account bobAccount = getAccountForUser(bob, 100);

        doReturn(alice).when(userService).getLoggedInUser();
        doReturn(aliceAccount).when(accountService).getAccount(alice);
        doReturn(bob).when(userService).getUserByUserName("Bob");
        doReturn(bobAccount).when(accountService).getAccount(bob);
        doNothing().when(accountService).update(aliceAccount, 0);
        doNothing().when(accountService).update(bobAccount, 200);

        transactionService.transfer("Bob", 200);

        verify(accountService).update(aliceAccount, 0);
        verify(accountService).update(bobAccount, 200);

        assertEquals(-100, aliceAccount.getCreditMap().get(bob));
        assertEquals(100, bobAccount.getCreditMap().get(alice));
    }


    @Test
    void transferAmount_givenValidUsersWithDebtForBeneficiary_success() {
        User alice = getNewUser("Alice");
        User bob = getNewUser("Bob");

        Account aliceAccount = getAccountForUser(alice, 0);
        aliceAccount.getCreditMap().put(bob, -70);
        Account bobAccount = getAccountForUser(bob, 100);
        bobAccount.getCreditMap().put(alice, 70);

        doReturn(bob).when(userService).getLoggedInUser();
        doReturn(bobAccount).when(accountService).getAccount(bob);
        doReturn(alice).when(userService).getUserByUserName("Alice");
        doReturn(aliceAccount).when(accountService).getAccount(alice);

        doNothing().when(accountService).update(aliceAccount, 0);
        doNothing().when(accountService).update(bobAccount, 100);

        transactionService.transfer("Alice", 50);

        verify(accountService).update(aliceAccount, 0);
        verify(accountService).update(bobAccount, 100);

        assertEquals(-20, aliceAccount.getCreditMap().get(bob));
        assertEquals(20, bobAccount.getCreditMap().get(alice));
    }

    @Test
    void transferAmount_givenValidUsersWithAmountTransferredMoreThanDebtForBeneficiary_success() {
        User alice = getNewUser("Alice");
        User bob = getNewUser("Bob");

        Account aliceAccount = getAccountForUser(alice, 0);
        aliceAccount.getCreditMap().put(bob, -70);
        Account bobAccount = getAccountForUser(bob, 200);
        bobAccount.getCreditMap().put(alice, 70);

        doReturn(bob).when(userService).getLoggedInUser();
        doReturn(bobAccount).when(accountService).getAccount(bob);
        doReturn(alice).when(userService).getUserByUserName("Alice");
        doReturn(aliceAccount).when(accountService).getAccount(alice);

        doNothing().when(accountService).update(aliceAccount, 50);
        doNothing().when(accountService).update(bobAccount, 150);

        transactionService.transfer("Alice", 120);

        verify(accountService).update(aliceAccount, 50);
        verify(accountService).update(bobAccount, 150);

        assertNull( aliceAccount.getCreditMap().get(bob));
        assertNull( bobAccount.getCreditMap().get(alice));
    }

    private User getNewUser(String userName) {
        return new User(userName);
    }

    private Account getAccountForUser(User user, Integer balance) {
        return new Account(user, balance, new LinkedHashMap<>());
    }
}