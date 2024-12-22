package com.dkatalis.atmsimulator.service;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;
import com.dkatalis.atmsimulator.enums.TransactionType;
import com.dkatalis.atmsimulator.exception.BusinessException;

import java.util.Map;


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
        settleDebtsForPendingCredits(userAccount);
    }

    private void settleDebtsForPendingCredits(Account userAccount) {
        Integer newBalance;
        for (Map.Entry<User, Integer> entry : userAccount.getCreditMap().entrySet()) {
            newBalance = userService.getBalance();
            if(newBalance <= 0) {
                break;
            }
            final Integer transferAmount = entry.getValue();
            final User user = entry.getKey();
            if (transferAmount < 0) {
                Integer amountToTransfer = (newBalance > Math.abs(transferAmount)) ? Math.abs(transferAmount) : newBalance;
                transfer(user.getUserName(), amountToTransfer);
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
        User beneficiary = userService.getUserByUserName(userName);
        Account beneficiaryAccount = accountService.getAccount(beneficiary);

        if (loggedInUserAccount.getBalance() < 0) {
            throw new BusinessException("No Enough Funds to transfer exception");
        }

        Integer amountDebitedForBeneficiary = debitFromLoggedInUser(amount, loggedInUserAccount, beneficiary);

        creditToBeneficiary(amount, amountDebitedForBeneficiary, beneficiaryAccount);
    }

    private void creditToBeneficiary(Integer actualAmount, Integer receivedAmount, Account beneficaryAccount) {
        Integer newBeneficiaryBalance = beneficaryAccount.getBalance() + receivedAmount;
        Integer amountTobeSettled =  actualAmount - receivedAmount;
        User loggedInUser = userService.getLoggedInUser();
        Integer amountInDebt = beneficaryAccount.getCreditMap().get(loggedInUser);

        if (amountInDebt!= null && amountInDebt != 0) {
            if (receivedAmount >= 0) {
                amountInDebt = 0;
                newBeneficiaryBalance = newBeneficiaryBalance - receivedAmount;
            }
            else{
                amountInDebt =  amountInDebt + amountTobeSettled - receivedAmount;
                newBeneficiaryBalance = newBeneficiaryBalance - receivedAmount;
            }
        }
        else
        {
            amountInDebt = amountTobeSettled;
        }

        if (amountInDebt != 0) {
            if(beneficaryAccount.getCreditMap().containsKey(loggedInUser)) {
                Integer amountToBeSettled = beneficaryAccount.getCreditMap().get(loggedInUser);
                beneficaryAccount.getCreditMap().put(loggedInUser, amountToBeSettled - amountInDebt);
            }
            else {
                beneficaryAccount.getCreditMap().put(loggedInUser, amountTobeSettled);
            }
        }
        else {
            beneficaryAccount.getCreditMap().remove(loggedInUser);
        }
        accountService.update(beneficaryAccount, newBeneficiaryBalance);
    }

    private Integer debitFromLoggedInUser(Integer amount, Account loggedInUserAccount, User beneficiary) {
        Integer newLoggedInUserBalance = loggedInUserAccount.getBalance() - amount;
        Integer pendingAmountToBeneficiary = loggedInUserAccount.getCreditMap().get(beneficiary);

        Integer amountSettled;
        Integer amountInDebt;

        if(pendingAmountToBeneficiary != null){
            if(Math.abs(pendingAmountToBeneficiary) > amount ) {
                amountSettled = amount;
                amountInDebt = -amount;
                newLoggedInUserBalance = loggedInUserAccount.getBalance();
            }
           else {
               amountSettled = amount - pendingAmountToBeneficiary;
               amountInDebt = 0;
               newLoggedInUserBalance = loggedInUserAccount.getBalance() - amountSettled;
            }
        }
        else if(newLoggedInUserBalance < 0) {
            amountSettled = loggedInUserAccount.getBalance();
            amountInDebt = newLoggedInUserBalance;
            newLoggedInUserBalance = 0;
        }
        else{
            amountSettled = amount;
            amountInDebt = 0;
        }

        if (amountInDebt != 0) {
            if(loggedInUserAccount.getCreditMap().containsKey(beneficiary)) {
                Integer amountToBeSettled = loggedInUserAccount.getCreditMap().get(beneficiary);
                loggedInUserAccount.getCreditMap().put(beneficiary, amountToBeSettled + amountInDebt);
            }
            else {
                loggedInUserAccount.getCreditMap().put(beneficiary, amountInDebt);
            }
        }
        else {
            loggedInUserAccount.getCreditMap().remove(beneficiary);
        }

        accountService.update(loggedInUserAccount, newLoggedInUserBalance);
        return amountSettled;
    }


    private Integer calculateNewBalance(TransactionType transactionType, Integer amount, Account account) {
        if (transactionType == TransactionType.CREDIT) {
            return account.getBalance() + amount;
        }
        return account.getBalance() - amount;
    }

}
