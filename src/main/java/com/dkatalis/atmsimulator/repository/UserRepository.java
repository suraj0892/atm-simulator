package com.dkatalis.atmsimulator.repository;

import com.dkatalis.atmsimulator.domain.User;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    private final List<User> users;

    public UserRepository(List<User> users) {
        this.users = users;
    }

    public Optional<User> findByUserName(String userName) {
        return users.stream().filter(user -> user.getUserName().equals(userName)).findFirst();
    }

    public User save(User user) {
        users.add(user);
        return user;
    }
}
