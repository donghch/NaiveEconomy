package me.henrydhc.naiveeconomy.task;

import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.connector.SQLiteConnector;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class SyncCachePurgeTask implements Consumer<BukkitTask> {

    private final Connector connector;

    public SyncCachePurgeTask(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        SQLiteConnector sqLiteConnector = (SQLiteConnector) connector;
    }
}
