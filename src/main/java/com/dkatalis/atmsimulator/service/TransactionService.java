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
            if (newBalance <= 0) {
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
        User loggedInUser = userService.getLoggedInUser();
        Integer beneficiaryBalance = beneficaryAccount.getBalance();
        Integer pendingAmountFromSource = beneficaryAccount.getCreditMap().get(loggedInUser);

        if(pendingAmountFromSource != null && pendingAmountFromSource != 0) {
            if (pendingAmountFromSource > 0) {
                if (Math.abs(pendingAmountFromSource) > receivedAmount) {
                    pendingAmountFromSource = pendingAmountFromSource - receivedAmount;
                } else {
                    beneficiaryBalance = beneficiaryBalance + receivedAmount;
                    pendingAmountFromSource = 0;
                }
            }
            else {
                if(Math.abs(pendingAmountFromSource) >  actualAmount){
                    pendingAmountFromSource = pendingAmountFromSource + actualAmount;
                }else {
                    beneficiaryBalance = actualAmount + pendingAmountFromSource;
                    pendingAmountFromSource = 0;
                }
            }
        }
        else {
            if (actualAmount > receivedAmount) {
                beneficiaryBalance = beneficiaryBalance + receivedAmount;
                pendingAmountFromSource = actualAmount - receivedAmount;
            } else {
                pendingAmountFromSource = 0;
                beneficiaryBalance = beneficiaryBalance + receivedAmount;
            }
        }

        updatedCreditMap (pendingAmountFromSource, beneficaryAccount, loggedInUser);
        accountService.update(beneficaryAccount, beneficiaryBalance);
    }

    private Integer debitFromLoggedInUser(Integer amount, Account loggedInUserAccount, User beneficiary) {
        Integer loggedInUserAccountBalance = loggedInUserAccount.getBalance();
        Integer pendingAmountToBeneficiary = loggedInUserAccount.getCreditMap().get(beneficiary);
        Integer debitedAmount;
        if (pendingAmountToBeneficiary != null && pendingAmountToBeneficiary < 0) {
            if (Math.abs(pendingAmountToBeneficiary) > amount) {
                debitedAmount = amount;
                pendingAmountToBeneficiary = pendingAmountToBeneficiary + amount;
                loggedInUserAccountBalance = 0;
            } else {
                debitedAmount = Math.abs(pendingAmountToBeneficiary);
                loggedInUserAccountBalance = loggedInUserAccountBalance + pendingAmountToBeneficiary;
                pendingAmountToBeneficiary = 0;
            }
        } else if(pendingAmountToBeneficiary != null && pendingAmountToBeneficiary > 0) {
            if (Math.abs(pendingAmountToBeneficiary) > amount) {
                debitedAmount = amount;
                pendingAmountToBeneficiary = pendingAmountToBeneficiary - amount;
            } else {
                debitedAmount = Math.abs(pendingAmountToBeneficiary);
                loggedInUserAccountBalance =  loggedInUserAccountBalance - amount + pendingAmountToBeneficiary;
                pendingAmountToBeneficiary = 0;
            }
        }
        else {
            if (loggedInUserAccountBalance > amount) {
                loggedInUserAccountBalance = loggedInUserAccountBalance - amount;
                pendingAmountToBeneficiary = 0;
                debitedAmount = amount;
            } else {
                debitedAmount = Math.abs(loggedInUserAccountBalance);
                pendingAmountToBeneficiary = loggedInUserAccountBalance - amount;
                loggedInUserAccountBalance = 0;
            }
        }

        updatedCreditMap (pendingAmountToBeneficiary, loggedInUserAccount, beneficiary);
        accountService.update(loggedInUserAccount, loggedInUserAccountBalance);
        return debitedAmount;
    }

    private void updatedCreditMap(Integer pendingAmount, Account account, User user) {
        if (pendingAmount != 0) {
            account.getCreditMap().put(user, pendingAmount);
        } else {
            account.getCreditMap().remove(user);
        }

    }


    private Integer calculateNewBalance(TransactionType transactionType, Integer amount, Account account) {
        if (transactionType == TransactionType.CREDIT) {
            return account.getBalance() + amount;
        }
        return account.getBalance() - amount;
    }

}
