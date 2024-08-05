package me.henrydhc.naiveeconomy.account;

import java.util.UUID;

/**
 * Defines what an economy account should do
 */
public interface EcoAccount {

    /**
     * Get account uuid
     * @return Account uuids
     */
    public UUID getAccountUUID();

    /**
     * Get account owner;s uuid.
     * @return Player uuid
     */
    public UUID getOwnerUUID();

    /**
     * Get account balance
     * @return Account balance
     */
    public double getBalance();

    /**
     * Set account's balance
     * @param newBalance
     */
    public void setBalance(double newBalance);


}
