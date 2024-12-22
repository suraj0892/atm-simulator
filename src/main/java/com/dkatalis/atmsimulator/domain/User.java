package com.dkatalis.atmsimulator.domain;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class User {

    private final String userName;
    private final UUID userId;

    public User(String userName) {
        this.userName = userName;
        this.userId = UUID.randomUUID();
    }

    public String getUserName() {
        return userName;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName +
                '}';
    }
}
