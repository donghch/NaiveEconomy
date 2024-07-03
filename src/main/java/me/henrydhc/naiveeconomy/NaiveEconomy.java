package me.henrydhc.naiveeconomy;

import me.henrydhc.naiveeconomy.cmdhandler.CmdDispatcher;
import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.connector.SQLiteConnector;
import me.henrydhc.naiveeconomy.economy.MainEconomy;
import me.henrydhc.naiveeconomy.lang.LangLoader;
import me.henrydhc.naiveeconomy.listeners.EconomyListener;
import me.henrydhc.naiveeconomy.permission.PermissionManager;
import me.henrydhc.naiveeconomy.tabcomplete.TabCompletor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class NaiveEconomy extends JavaPlugin {

    private final Logger logger = getLogger();
    private Connector connector;
    private Economy economy;
    private CoreType coreType;

    @Override
    public void onEnable() {

        detectSeverCore();

        if (!LangLoader.loadLang("zh-cn", this)) {
            logger.severe("Failed to load language file. NaiveEconomy would not be able to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!detectVault()) {
            logger.severe("Can't find Vault. NaiveEconomy would not be able to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        logger.info("Hooked to Vault");


        try {
            connector = new SQLiteConnector(this);
        } catch (Exception e) {
            logger.severe("Failed to connect to database. NaiveEconomy would not be able to work");
            logger.severe(e.toString());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        logger.info("Database connected.");

        economy = new MainEconomy(connector, this);
        logger.info(economy.toString());


        if (!registerService()) {
            logger.severe("Can't register economy service. NaiveEconomy would not be able to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        logger.info("Economy Service Registered");

        // Register permissions
        PermissionManager.registerPermissions();
        logger.info("Permission Registered.");

        // Register commands
        getCommand("economy").setExecutor(new CmdDispatcher(economy, connector));
        getCommand("economy").setTabCompleter(new TabCompletor());

        // Register Listeners
        getServer().getPluginManager().registerEvents(new EconomyListener(economy), this);
    }

    @Override
    public void onDisable() {
        if (coreType != CoreType.SPIGOT) {
            Bukkit.getAsyncScheduler().cancelTasks(this);
        } else {
            Bukkit.getScheduler().cancelTasks(this);
        }

        try {
            logger.info("Saving balance cache");
            connector.saveCache();
            connector.close();
        } catch (Exception e) {
            logger.severe("Failed to conduct database cleanup. Data might be lost");
        }
        getServer().getServicesManager().unregisterAll(this);
        logger.info("Economy Service Unregistered");
    }

    /**
     * Get server core type
     * @return Server core type
     */
    public CoreType getCoreType() {
        return coreType;
    }

    /**
     * Detect whether vault exists
     * @return `True` if yes, otherwise `False`
     */
    private boolean detectVault() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        return plugin != null;
    }

    private boolean registerService() {
        getServer().getServicesManager().register(Economy.class, economy, this, ServicePriority.Normal);
        return true;
    }

    private void detectSeverCore() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            coreType = CoreType.FOLIA;
            logger.info("Current server core is Folia");
            return;
        } catch (Exception ignored) {

        }

        try {
            Class.forName("io.papermc.paper.plugin.PermissionManager");
            coreType = CoreType.PAPER;
            logger.info("Current server core is Paper");
            return;
        } catch (Exception ignored) {

        }

        coreType = CoreType.SPIGOT;
        logger.info("Current server core is Spigot");
    }

}
