package com.dkatalis.atmsimulator.repository;

import com.dkatalis.atmsimulator.domain.Account;
import com.dkatalis.atmsimulator.domain.User;

import java.util.List;
import java.util.Optional;

public class AccountRepository {

    private final List<Account> accounts;

    public AccountRepository(List<Account> accounts) {
        this.accounts = accounts;
    }


    public void create(Account account) {
        accounts.add(account);
    }

    public void update(Account outdatedAccount, Account updatedAccount) {
        if (accounts.contains(outdatedAccount)) {
            accounts.remove(outdatedAccount);
            accounts.add(updatedAccount);
        }
    }

    public Optional<Account> getAccountByUser(User user) {
        return accounts.stream().filter(account -> account.getUser().equals(user)).findFirst();
    }

}
