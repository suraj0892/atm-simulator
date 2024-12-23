package com.dkatalis.atmsimulator.repository;

import com.dkatalis.atmsimulator.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;
    private List<User> users;

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();
        userRepository = new UserRepository(users);
    }

    @Test
    void saveNewUser_givenValidData_success() {
        User user = new User("Alice");
        User actual = userRepository.save(user);
        assertEquals(actual, user);
        assertEquals(1, users.size());
    }

    @Test
    void findByUserName_givenValidInput_sucess() {
        users.add(new User("Alice"));
        users.add(new User("Bob"));

        Optional<User> actual = userRepository.findByUserName("Bob");
        assertTrue(actual.isPresent());
        assertEquals("Bob", actual.get().getUserName());

        actual = userRepository.findByUserName("John");
        assertFalse(actual.isPresent());

    }
}