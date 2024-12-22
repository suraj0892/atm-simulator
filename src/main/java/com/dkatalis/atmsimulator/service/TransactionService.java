package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.enums.TransactionType;
import com.dkatalis.atmsimulator.exception.BusinessException;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


public class TransactionService {

    private final UserService userService;
    private final AccountService accountService;

    public TransactionService(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public void deposit(Integer amount) {
        User loggedInUser = userService.getLoggedInUser();
        Account userAccount = accountService.getAccount(loggedInUser);
        Integer newBalance = calculateNewBalance(TransactionType.CREDIT, amount, userAccount);
        accountService.update(userAccount, newBalance);

        for (Map.Entry<User, Integer> entry : userAccount.getCreditMap().entrySet()) {
            newBalance = userService.getBalance();
            Integer transferAmount = entry.getValue();
            User user = entry.getKey();
            if (transferAmount < 0) {
                if (newBalance > Math.abs(transferAmount)) {
                    transfer(user.getUserName(), Math.abs(transferAmount));
                } else {
                    transfer(user.getUserName(), newBalance);
                }
            }
        }
    }

    public void withdraw(Integer amount) {
        User loggedInUser = userService.getLoggedInUser();
        Account userAccount = accountService.getAccount(loggedInUser);
        Integer newBalance = calculateNewBalance(TransactionType.DEBIT, amount, userAccount);

        if (userAccount.getBalance() <= 0 || newBalance <= 0) {
            throw new BusinessException("No enough funds to withdraw");
        }
        accountService.update(userAccount, newBalance);
    }


    public void transfer(String userName, Integer amount) {
        User loggedInUser = userService.getLoggedInUser();
        Account loggedInUserAccount = accountService.getAccount(loggedInUser);

        validate(userName, loggedInUser, loggedInUserAccount);
        User beneficiary = userService.getUserByUserName(userName);
        Account beneficiaryAccount =  accountService.getAccount(beneficiary);

        Integer loggedInUserNewBalance = calculateNewBalance(TransactionType.DEBIT, amount, loggedInUserAccount);
        Integer beneficiaryNewBalance = calculateNewBalance(TransactionType.CREDIT, amount, beneficiaryAccount);

        if (loggedInUserNewBalance <= 0) {
            accountService.update(loggedInUserAccount, 0);
            Map<User, Integer> creditMap = loggedInUserAccount.getCreditMap();
            if (creditMap.containsKey(beneficiary)) {
              Integer amountOwed =  creditMap.get(beneficiary) + loggedInUserNewBalance;
                creditMap.put(beneficiary, amountOwed);
            }
            else
            {
                creditMap.put(beneficiary, loggedInUserNewBalance);
            }

            beneficiaryNewBalance =  beneficiaryNewBalance + loggedInUserNewBalance;
            accountService.update(beneficiaryAccount, beneficiaryNewBalance);
            Map<User, Integer> beneficiaryCreditMap = beneficiaryAccount.getCreditMap();
            if (beneficiaryCreditMap.containsKey(loggedInUser)) {
                Integer amountOwed =  beneficiaryCreditMap.get(loggedInUser) + loggedInUserNewBalance;
                beneficiaryCreditMap.put(loggedInUser, amountOwed);
            }
            else
            {
                beneficiaryCreditMap.put(loggedInUser, Math.abs(loggedInUserNewBalance));
            }
        }
        else {
            Map<User, Integer> beneficiaryCreditMap = beneficiaryAccount.getCreditMap();
            if (beneficiaryCreditMap.containsKey(loggedInUser)) {
                Integer amountOwed = beneficiaryCreditMap.get(loggedInUser) + amount;
                if (amountOwed < 0) {
                    beneficiaryCreditMap.put(loggedInUser, amountOwed);
                    beneficiaryNewBalance =  beneficiaryNewBalance - amount ;
                }
                else {
                    beneficiaryCreditMap.remove(loggedInUser);
                    beneficiaryNewBalance = beneficiaryNewBalance - amount + amountOwed;
                }
            }

            Map<User, Integer> creditMap = loggedInUserAccount.getCreditMap();
            if (creditMap.containsKey(beneficiary)) {
                Integer amountOwed =  creditMap.get(beneficiary) - amount;
                if (amountOwed <= 0) {
                    creditMap.remove(beneficiary);
                    loggedInUserNewBalance =  loggedInUserNewBalance + amount + amountOwed;

                }
                else{
                    creditMap.put(beneficiary, amountOwed);
                    loggedInUserNewBalance =  loggedInUserNewBalance + amount;

                }
            }
            accountService.update(loggedInUserAccount, loggedInUserNewBalance);
            accountService.update(beneficiaryAccount, beneficiaryNewBalance);
        }
    }

    private void validate(String userName, User loggedInUser, Account account) {
        if (Objects.equals(loggedInUser.getUserName(), userName)) {
            throw new BusinessException("Invalid operation");
        }
        if (account.getBalance() <= 0) {
            throw new BusinessException("No enough funds to transfer");
        }
    }

    private Integer calculateNewBalance(TransactionType transactionType, Integer amount, Account account) {
        if(transactionType == TransactionType.CREDIT) {
            return account.getBalance() + amount;
        }
        return account.getBalance() - amount;
    }

}
