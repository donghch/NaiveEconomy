package me.henrydhc.naiveeconomy.account;

import java.util.UUID;

public class NaiveAccount implements EcoAccount{

    private UUID accountID;
    private UUID playerID;
    private double balance;

    public NaiveAccount(UUID playerID) {
        accountID = UUID.randomUUID();
        this.playerID = playerID;
        balance = 0;
    }

    public NaiveAccount(UUID playerID, UUID accountID, double balance) {
        this.accountID = accountID;
        this.playerID = playerID;
        this.balance = balance;
    }

    @Override
    public UUID getAccountUUID() {
        return accountID;
    }

    @Override
    public UUID getOwnerUUID() {
        return playerID;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(double newBalance) {
        this.balance = newBalance;
    }
}
