package com.dkatalis.atmsimulator.exception;

public class AccountNotFoundException extends BusinessException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
