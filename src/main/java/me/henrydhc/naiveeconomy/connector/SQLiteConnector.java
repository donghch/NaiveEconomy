package me.henrydhc.naiveeconomy.connector;

import me.henrydhc.naiveeconomy.NaiveEconomy;
import me.henrydhc.naiveeconomy.account.EcoAccount;
import me.henrydhc.naiveeconomy.account.NaiveAccount;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SQLiteConnector implements Connector {

    private LinkedHashMap<UUID, EcoAccount> accountCache;
    private final int maxCacheCount;
    private final Connection connection;

    public SQLiteConnector(NaiveEconomy plugin) throws Exception{
        accountCache = new LinkedHashMap<>();
        maxCacheCount = 100;

        if (!checkPath()) {
            throw new Exception();
        }

        connection = DriverManager.getConnection("jdbc:sqlite:plugins/NaiveEconomy/data.db");
        initDatabase();
    }

    @Override
    public EcoAccount getAccount(UUID playerID) throws SQLException {

        if (accountCache.containsKey(playerID)) {
            // Refresh this record in cache
            EcoAccount account = accountCache.remove(playerID);
            accountCache.put(playerID, account);
            return account;
        }

        // Try retrieve from the database
        Statement statement = connection.createStatement();
        String getBalanceSQL = String.format("SELECT * FROM balance WHERE player_id='%s'",
            playerID.toString());
        ResultSet resultSet = statement.executeQuery(getBalanceSQL);
        if (!resultSet.next()) {
            return null;
        } else {
            UUID accountID = UUID.fromString(resultSet.getString("account_id"));
            EcoAccount account = new NaiveAccount(playerID, accountID, resultSet.getDouble("balance"));
            saveAccountToCache(account);
            return account;
        }
    }

    @Override
    public boolean setBalance(UUID playerID, double newValue) {
        EcoAccount account;
        try {
            account = getAccount(playerID);
        } catch (SQLException e) {
            return false;
        }

        if (account == null) {
            saveAccountToCache(new NaiveAccount(playerID, UUID.randomUUID(), newValue));
        } else {
            account.setBalance(newValue);
        }
        return true;
    }


    @Override
    public void close() throws SQLException {
        connection.close();
    }

    /**
     * Check and create file path for database
     * @return `True` on success, `False` on failure
     */
    private boolean checkPath() {
        File path = new File("plugins/NaiveEconomy");
        if (path.isDirectory()) {
            return true;
        }
        return path.mkdirs();
    }

    /**
     * Initialize database
     * @throws SQLException When something wrong happens
     */
    private void initDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        String initDbSQL = "CREATE TABLE IF NOT EXISTS balance(" +
            "account_id TEXT PRIMARY KEY NOT NULL,"+
            "player_id TEXT NOT NULL," +
            "balance REAL NOT NULL)";
        statement.execute(initDbSQL);
    }


    @Override
    public boolean hasRecord(UUID playerID) throws SQLException {
        return hasCacheRecord(playerID) || hasDatabaseRecord(playerID);
    }

    public void saveCache() {
        Iterator<Map.Entry<UUID, EcoAccount>> iter = accountCache.entrySet().iterator();

        while (iter.hasNext()) {
            EcoAccount account = iter.next().getValue();
            try {
                saveAccount(account);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save account data into the database
     * @param account Economy Account
     */
    private void saveAccount(EcoAccount account) throws SQLException{
        String accountIDStr = account.getAccountUUID().toString();
        UUID playerID = account.getOwnerUUID();
        double balance = account.getBalance();
        Statement statement = connection.createStatement();
        String saveSQL;

        if (hasDatabaseRecord(account.getOwnerUUID())) {
            saveSQL =
                String.format("UPDATE balance SET balance=%.2f WHERE account_id='%s'",
                    balance, accountIDStr);
        } else {
            saveSQL =
                String.format("INSERT INTO balance VALUES('%s', '%s', %.2f)",
                    accountIDStr, playerID.toString(), balance);
        }
        statement.execute(saveSQL);
    }

    // Cache functions

    /**
     * Save Econ account into cache.
     * If the cache is full, the eldest cache will be ejected and
     * saved to database.
     * @param account Econ account
     */
    private void saveAccountToCache(EcoAccount account) {
        UUID playerID = account.getOwnerUUID();

        // Simply update cache
        if (accountCache.containsKey(playerID)) {
            accountCache.remove(playerID);
            accountCache.put(playerID, account);
            return;
        }

        // Eject one if the cache is full
        if (accountCache.size() >= maxCacheCount) {
            UUID eldestPlayerID = accountCache.entrySet().iterator().next().getKey();
            EcoAccount eldestAccount = accountCache.remove(eldestPlayerID);
            try {
                saveAccount(eldestAccount);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        accountCache.put(playerID, account);

    }

    private boolean hasCacheRecord(UUID playerID) {
        return accountCache.containsKey(playerID);
    }

    private boolean hasDatabaseRecord(UUID playerID) throws SQLException{
        Statement statement = connection.createStatement();
        String hasAccountSQL = String.format("SELECT * FROM balance WHERE player_id='%s'", playerID.toString());
        ResultSet resultSet = statement.executeQuery(hasAccountSQL);
        if (resultSet.next()) {
            UUID accountID = UUID.fromString(resultSet.getString("account_id"));
            double balance = resultSet.getDouble("balance");
            saveAccountToCache(new NaiveAccount(playerID, accountID, balance));
            return true;
        } else {
            return false;
        }
    }
}
