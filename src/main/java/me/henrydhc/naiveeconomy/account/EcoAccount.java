package me.henrydhc.naiveeconomy.account;

/**
 * Defines what an economy account should do
 */
public interface EcoAccount {

    /**
     * Get account owner;s uuid.
     * @return Player uuid
     */
    public String getOwnerUUID();

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

    /**
     * Get last modified time
     * @return Last modified time
     */
    public long getLastModified();

}
