package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.exception.BusinessException;
import com.dkatalis.atmsimulator.repository.UserRepository;

import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private User loggedInUser;

    public UserService(UserRepository userRepository, AccountService accountService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.loggedInUser = null;
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }


    public void login(String userName) {
        validateForLogin();
        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (userOptional.isPresent()) {
           setCurrentUser(userOptional.get());
        }
        else{
            setCurrentUser(userRepository.save(userName));
        }
        accountService.createAccount(getLoggedInUser());
    }

    public String logout() {
        validateForLogout();
        final String loggedOutUserName =  getLoggedInUser().getUserName();
        setCurrentUser(null);
        return loggedOutUserName;
    }

    private void validateForLogout() {
        if(getLoggedInUser() == null) {
            throw new BusinessException("No Active logged in user found");
        }
    }

    public Integer getBalance() {
        return accountService.getBalance(getLoggedInUser());
    }

    private void validateForLogin() {
        if (getLoggedInUser() != null) {
            throw new BusinessException("Action Not allowed, as user session is in progress");
        }
    }

    private void setCurrentUser(User user) {
        this.loggedInUser = user;
    }

}
