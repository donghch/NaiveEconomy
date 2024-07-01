package me.henrydhc.naiveeconomy.connector;

import java.sql.SQLException;

public interface Connector {

    /**
     * Get player balance from the database
     * @param playerID Player UUID
     * @return Player balance
     */
    public double getBalance(String playerID) throws Exception;

    /**
     * Set a player's balance
     * @param playerID Player UUID
     * @param newValue Player's new balance
     */
    public void setBalance(String playerID, double newValue);

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
