package me.henrydhc.naiveeconomy.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.connector.SQLiteConnector;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class AsyncCacheSaveTask implements Consumer<ScheduledTask> {

    private final Connector connector;
    private final Plugin plugin;

    public AsyncCacheSaveTask(Connector connector, Plugin plugin) {
        this.connector = connector;
        this.plugin = plugin;
    }

    @Override
    public void accept(ScheduledTask scheduledTask) {
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
