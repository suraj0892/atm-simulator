package com.dkatalis.atmsimulator.exception;

public class UserNotFoundException extends BusinessException{

    public UserNotFoundException(String message) {
        super(message);
    }
}
