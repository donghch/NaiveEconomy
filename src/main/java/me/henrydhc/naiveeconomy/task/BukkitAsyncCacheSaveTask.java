package me.henrydhc.naiveeconomy.task;

import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.connector.SQLiteConnector;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitAsyncCacheSaveTask extends BukkitRunnable {

    private final Connector connector;

    public BukkitAsyncCacheSaveTask(Connector connector) {
        this.connector = connector;
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
