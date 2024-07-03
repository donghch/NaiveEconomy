package me.henrydhc.naiveeconomy.account;

import java.util.Date;

public class NaiveAccount implements EcoAccount{

    private final String playerID;
    private double balance;
    private long lastModified;

    public NaiveAccount(String playerID, double balance) {
        this.playerID = playerID;
        this.balance = balance;
        this.lastModified = new Date().getTime();
    }

    public String getOwnerUUID() {
        return playerID;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        this.lastModified = new Date().getTime();
    }

    /**
     * Get last modified time
     * @return Last modified time
     */
    public long getLastModified() {
        return this.lastModified;
    }

}
