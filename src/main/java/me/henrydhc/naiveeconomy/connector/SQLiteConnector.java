package me.henrydhc.naiveeconomy.connector;

import me.henrydhc.naiveeconomy.CoreType;
import me.henrydhc.naiveeconomy.NaiveEconomy;
import me.henrydhc.naiveeconomy.account.EcoAccount;
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

public class SQLiteConnector implements Connector {

    private ConcurrentMap<String, EcoAccount> accounts;
    private final int cacheLife;
    private final Connection connection;
    private final Plugin plugin;

    public SQLiteConnector(NaiveEconomy plugin) throws Exception{
        accounts = new ConcurrentHashMap<>();
        cacheLife = 120;
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
    public EcoAccount getAccount(String playerID) throws SQLException {
        if (accounts.containsKey(playerID)) {
            return accounts.get(playerID);
        }

        // Try retrieve from the database
        Statement statement = connection.createStatement();
        String getBalanceSQL = String.format("SELECT balance FROM balance WHERE player_id='%s'",
            playerID);
        ResultSet resultSet = statement.executeQuery(getBalanceSQL);
        if (resultSet.next()) {
            return null;
        } else {
            EcoAccount account = new NaiveAccount(playerID, resultSet.getDouble("balance"));
            accounts.put(playerID, account);
            return account;
        }
    }

    @Override
    public boolean setBalance(String playerID, double newValue) {
        EcoAccount account;
        try {
            account = getAccount(playerID);
        } catch (SQLException e) {
            return false;
        }

        if (account == null) {
            accounts.put(playerID, new NaiveAccount(playerID, newValue));
        } else {
            account.setBalance(newValue);
        }
        return true;
    }

    /**
     * Save balance cache into the database
     */
    @Override
    public void saveCache() throws SQLException {
        Map<String, EcoAccount> copy = new HashMap<>(accounts);
        Statement statement = connection.createStatement();
        for (Map.Entry<String, EcoAccount> entry: copy.entrySet()) {
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
        ConcurrentMap<String, EcoAccount> result = new ConcurrentHashMap<>();
        long currTime = new Date().getTime();
        for (Map.Entry<String, EcoAccount> account: accounts.entrySet()) {
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


    @Override
    public boolean hasRecord(String playerID) throws SQLException {
        if (accounts.containsKey(playerID)) {
            return true;
        }
        Statement statement = connection.createStatement();
        String hasAccountSQL = String.format("SELECT * FROM balance WHERE player_id='%s'", playerID);
        ResultSet resultSet = statement.executeQuery(hasAccountSQL);
        if (resultSet.next()) {
            accounts.put(playerID, new NaiveAccount(playerID, resultSet.getDouble("balance")));
            return true;
        } else {
            return false;
        }
    }
}
