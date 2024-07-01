package me.henrydhc.naiveeconomy.connector;

import me.henrydhc.naiveeconomy.task.AsyncCacheSaveTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SQLiteConnector implements Connector {

    private final Map<String, Double> balanceCache;
    private final Map<String, Long> cacheTime;
    private final Connection connection;
    private final Plugin plugin;
    private final AsyncCacheSaveTask task;

    public SQLiteConnector(Plugin plugin) throws Exception{
        balanceCache = new ConcurrentHashMap<>();
        cacheTime = new ConcurrentHashMap<>();
        this.plugin = plugin;

        if (!checkPath()) {
            throw new Exception();
        }

        connection = DriverManager.getConnection("jdbc:sqlite:plugins/NaiveEconomy/data.db");
        initDatabase();
        task = new AsyncCacheSaveTask(this, plugin);
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public double getBalance(String playerID) throws SQLException {
        if (balanceCache.containsKey(playerID)) {
            return balanceCache.get(playerID);
        }
        if (!hasRecord(playerID)) {
            return 0;
        }
        double balance = getBalanceFromDb(playerID);
        balanceCache.put(playerID, balance);
        cacheTime.put(playerID, new Date().getTime());
        return balance;
    }

    @Override
    public void setBalance(String playerID, double newValue) {
        balanceCache.put(playerID, newValue);
        cacheTime.put(playerID, new Date().getTime());
    }

    /**
     * Save balance cache into the database
     */
    @Override
    public void saveCache() throws SQLException {
        Statement statement = connection.createStatement();
        for (Map.Entry<String, Double> entry: balanceCache.entrySet()) {
            String updateSQL = String.format("UPDATE balance SET balance=%.2f WHERE player_id='%s'", entry.getValue(), entry.getKey());
            String newAccountSQL = String.format("INSERT INTO balance VALUES('%s', %.2f)", entry.getKey(), entry.getValue());
            statement.execute(updateSQL);
            try {
                statement.execute(newAccountSQL);
            } catch (SQLException e) {
                continue;
            }
        }
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
            "player_id TEXT PRIMARY KEY NOT NULL, " +
            "balance REAL NOT NULL)";
        statement.execute(initDbSQL);
    }

    /**
     * Get player balance from database
     * @param playerID Player UUID. Must be valid
     * @return Player balance
     */
    private double getBalanceFromDb(String playerID) throws SQLException {
        Statement statement = connection.createStatement();
        String getBalance = String.format("SELECT balance FROM balance WHERE player_id='%s'", playerID);
        ResultSet resultSet = statement.executeQuery(getBalance);
        if (resultSet.next()) {
            return resultSet.getDouble("balance");
        }
        return -1;
    }

    @Override
    public boolean hasRecord(String playerID) throws SQLException {
        if (balanceCache.containsKey(playerID)) {
            return true;
        }
        Statement statement = connection.createStatement();
        String hasAccountSQL = String.format("SELECT * FROM balance WHERE player_id='%s'", playerID);
        ResultSet resultSet = statement.executeQuery(hasAccountSQL);
        return resultSet.next();
    }
}
