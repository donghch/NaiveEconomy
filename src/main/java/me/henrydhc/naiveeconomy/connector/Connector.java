package me.henrydhc.naiveeconomy.connector;

import me.henrydhc.naiveeconomy.account.EcoAccount;

import java.sql.SQLException;

public interface Connector {

    /**
     * Get player account
     * @param playerID Player UUID
     * @return Player account instance
     */
    public EcoAccount getAccount(String playerID) throws Exception;

    /**
     * Set a player's balance
     * @param playerID Player UUID
     * @param newValue Player's new balance
     */
    public boolean setBalance(String playerID, double newValue);

    /**
     * Check if this player has record in the database
     * @param playerID Player ID
     * @return `True` if yes, otherwise `False`
     */
    public boolean hasRecord(String playerID) throws Exception;

    /**
     * Save balance cache into the database
     */
    public void saveCache() throws Exception;

    /**
     * Close database connection
     */
    public void close() throws Exception;

}
