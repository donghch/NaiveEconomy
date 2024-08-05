package me.henrydhc.naiveeconomy.connector;

import me.henrydhc.naiveeconomy.account.EcoAccount;

import java.sql.SQLException;
import java.util.UUID;

public interface Connector {

    /**
     * Get player account
     * @param playerID Player UUID
     * @return Player account instance
     */
    public EcoAccount getAccount(UUID playerID) throws Exception;

    /**
     * Set a player's balance
     * @param playerID Player UUID
     * @param newValue Player's new balance
     */
    public boolean setBalance(UUID playerID, double newValue);

    /**
     * Check if this player has record in the database
     * @param playerID Player ID
     * @return `True` if yes, otherwise `False`
     */
    public boolean hasRecord(UUID playerID) throws Exception;

    /**
     * Close database connection
     */
    public void close() throws Exception;

    /**
     * Save all accounts into the database
     */
    public void saveCache();

}
