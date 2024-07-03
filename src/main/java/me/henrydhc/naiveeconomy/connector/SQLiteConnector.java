package me.henrydhc.naiveeconomy.connector;

import me.henrydhc.naiveeconomy.CoreType;
import me.henrydhc.naiveeconomy.NaiveEconomy;
import me.henrydhc.naiveeconomy.account.NaiveAccount;
import me.henrydhc.naiveeconomy.task.AsyncCacheSaveTask;
import me.henrydhc.naiveeconomy.task.BukkitAsyncCacheSaveTask;
import me.henrydhc.naiveeconomy.task.SyncCachePurgeTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLiteConnector implements Connector {

    private ConcurrentMap<String, NaiveAccount> accounts;
    private final int cacheLife;
    private final Connection connection;
    private final Lock lock;
    private final Plugin plugin;

    public SQLiteConnector(NaiveEconomy plugin) throws Exception{
        accounts = new ConcurrentHashMap<>();
        cacheLife = 120;
        lock = new ReentrantLock();
        this.plugin = plugin;

        if (!checkPath()) {
            throw new Exception();
        }

        connection = DriverManager.getConnection("jdbc:sqlite:plugins/NaiveEconomy/data.db");
        initDatabase();


        if (plugin.getCoreType() != CoreType.SPIGOT) {
            AsyncCacheSaveTask task = new AsyncCacheSaveTask(this);
            SyncCachePurgeTask purgeTask = new SyncCachePurgeTask(this);
            Bukkit.getScheduler().runTaskTimer(plugin, purgeTask, 60, 20 * 100);
            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task, 0, 1, TimeUnit.MINUTES);
        } else {
            BukkitAsyncCacheSaveTask task = new BukkitAsyncCacheSaveTask(this);
            SyncCachePurgeTask purgeTask = new SyncCachePurgeTask(this);
            Bukkit.getScheduler().runTaskTimer(plugin, purgeTask, 60, 20 * 100);
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, task, 0, 20 * 60);
        }
    }

    @Override
    public double getBalance(String playerID) throws SQLException {
        if (accounts.containsKey(playerID)) {
            return accounts.get(playerID).getBalance();
        }
        if (!hasRecord(playerID)) {
            return 0;
        }
        double balance = getBalanceFromDb(playerID);
        accounts.put(playerID, new NaiveAccount(playerID, balance));
        return balance;
    }

    @Override
    public boolean setBalance(String playerID, double newValue) {
        NaiveAccount account = accounts.get(playerID);
        if (account == null) {
            try {
                if (hasRecord(playerID)) {
                    getBalance(playerID);
                    account = accounts.get(playerID);
                } else {
                    return false;
                }
            } catch (SQLException e) {
                return false;
            }
        }
        account.setBalance(newValue);
        return true;
    }

    /**
     * Save balance cache into the database
     */
    @Override
    public void saveCache() throws SQLException {
        Map<String, NaiveAccount> copy = new HashMap<>(accounts);
        Statement statement = connection.createStatement();
        for (Map.Entry<String, NaiveAccount> entry: copy.entrySet()) {
            String updateSQL = String.format("UPDATE balance SET balance=%.2f WHERE player_id='%s'", entry.getValue().getBalance(),
                entry.getKey());
            String newAccountSQL = String.format("INSERT INTO balance VALUES('%s', %.2f)", entry.getKey(), entry.getValue().getBalance());
            statement.execute(updateSQL);
            try {
                statement.execute(newAccountSQL);
            } catch (SQLException ignored) {
            }
        }
        plugin.getLogger().info(String.format("Saved %d cache records", copy.size()));
    }

    /**
     * Purge local cache
     */
    public void purgeCache() {
        ConcurrentMap<String, NaiveAccount> result = new ConcurrentHashMap<>();
        long currTime = new Date().getTime();
        for (Map.Entry<String, NaiveAccount> account: accounts.entrySet()) {
            if (account.getValue().getLastModified() >= currTime - cacheLife * 1000) {
                result.put(account.getKey(), account.getValue());
            }
        }
        plugin.getLogger().info(String.format("Purged %d cache records.", accounts.size() - result.size()));
        this.accounts = result;
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
        if (accounts.containsKey(playerID)) {
            return true;
        }
        Statement statement = connection.createStatement();
        String hasAccountSQL = String.format("SELECT * FROM balance WHERE player_id='%s'", playerID);
        ResultSet resultSet = statement.executeQuery(hasAccountSQL);
        return resultSet.next();
    }
}
