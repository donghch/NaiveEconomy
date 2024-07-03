package me.henrydhc.naiveeconomy.task;

import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.connector.SQLiteConnector;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitAsyncCacheSaveTask extends BukkitRunnable {

    private final Connector connector;
    private final Plugin plugin;

    public BukkitAsyncCacheSaveTask(Connector connector, Plugin plugin) {
        this.connector = connector;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (connector instanceof SQLiteConnector) {
            SQLiteConnector sqLiteConnector = (SQLiteConnector) connector;
            try {
                sqLiteConnector.saveCache();
            } catch (Exception e) {
                return;
            }
        }
    }
}
