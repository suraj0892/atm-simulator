package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.exception.BusinessException;
import com.dkatalis.atmsimulator.exception.UserNotFoundException;
import com.dkatalis.atmsimulator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        userService =  new UserService(userRepository, accountService);
    }

    @Test
    void login_whenUserNotPresent_createNewUser() {
        User expectedUser = new User("Alice");

        doReturn(Optional.empty()).when(userRepository).findByUserName("Alice");
        doReturn(expectedUser).when(userRepository).save(expectedUser);

        userService.login("Alice");

        assertEquals(expectedUser, userService.getLoggedInUser());
    }

    @Test
    void login_whenUserIsPresent_doSignInSuccessfully() {
        User expectedUser = new User("Alice");
        doReturn(Optional.of(expectedUser)).when(userRepository).findByUserName("Alice");

        userService.login("Alice");

        assertEquals(expectedUser, userService.getLoggedInUser());
    }

    @Test
    void loginNewUser_whenAlreadyUserIsloggedIn_doThrowException() {
        login_whenUserIsPresent_doSignInSuccessfully();

        assertThrows(BusinessException.class, () -> userService.login("Bob"));
    }

    @Test
    void getUser_givenValidUserName_success() {
        User user = new User("Alice");

        doReturn(Optional.of(user)).when(userRepository).findByUserName("Alice");

        assertEquals(user, userService.getUserByUserName("Alice"));
    }

    @Test
    void getUser_givenUserNotExists_throwsException() {
        doReturn(Optional.empty()).when(userRepository).findByUserName("Alice");

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUserName("Alice"));
    }

    @Test
    void getBalance_givenUserLogged_success() {
        login_whenUserIsPresent_doSignInSuccessfully();

        doReturn(100).when(accountService).getBalance(new User("Alice"));

        assertNotNull(userService.getBalance());
    }

    @Test
    void logoutUser_givenNoUserLoggedIn_throwsException() {
        assertThrows(BusinessException.class, () -> userService.logout());
    }

    @Test
    void logoutUser_givenUserLoggedIn_success() {
        login_whenUserIsPresent_doSignInSuccessfully();
        String actual = userService.logout();
        assertEquals("Alice", actual);
        assertNull(userService.getLoggedInUser());
    }
}