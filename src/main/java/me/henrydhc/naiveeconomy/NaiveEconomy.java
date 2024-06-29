package me.henrydhc.naiveeconomy;

import me.henrydhc.naiveeconomy.economy.MainEconomy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class NaiveEconomy extends JavaPlugin {

    private Logger logger = getLogger();

    @Override
    public void onLoad() {

        if (!detectVault()) {
            logger.severe("Can't find Vault. NaiveEconomy would not be able to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!registerService()) {
            logger.severe("Can't register economy service. NaiveEconomy would not be able to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }




    }

    @Override
    public void onEnable() {


    }

    @Override
    public void onDisable() {

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
        getServer().getServicesManager().register(MainEconomy.class, new MainEconomy(), this, ServicePriority.Normal);
        return true;
    }

}
