package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PrintServiceTest {

    @InjectMocks
    private PrintService printService;

    @Mock
    private UserService userService;

    @Mock
    private AccountService accountService;

    private final PrintStream consoleOutput = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void init() {
        printService = new PrintService(userService, accountService);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(consoleOutput);
    }

    @Test
    void printWelcomeMessage_ForLoggedInUser_success() {
        doReturn(new User("Alice")).when(userService).getLoggedInUser();

        printService.printWelcomeMessage();

        assertEquals("Hello, Alice", outputStreamCaptor.toString().trim());
    }

    @Test
    void printAccountStatement_ForLoggedInUser_success() {
        String expectedResponse = "Your balance is $100\n" +
                "Owed $10 from Bob\n" +
                "Owed $10 to John";
        Map<User, Integer> debtMap = new LinkedHashMap<>();
        debtMap.put(new User("Bob"), 10);
        debtMap.put(new User("John"), -10);

        Account account = new Account(new User("Alice"), 100, debtMap);
        doReturn(100).when(userService).getBalance();
        doReturn(new User("Alice")).when(userService).getLoggedInUser();
        doReturn(account).when(accountService).getAccount(new User("Alice"));

        printService.printAccountStatement();

        assertEquals(expectedResponse, outputStreamCaptor.toString().trim());

    }
}