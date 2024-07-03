package me.henrydhc.naiveeconomy.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class ConfigLoader {

    private static List<String> configFields = List.of(
        "config-version", "lang"
    );

    private static FileConfiguration configuration;

    public static void loadConfig(Plugin plugin) {

        if (!checkPath()) {
            plugin.saveDefaultConfig();
        }

        configuration = plugin.getConfig();

    }

    /**
     * Get config instance
     * @return Config instance
     */
    public static FileConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Check and generate necessary folders for config file
     * @return `True` if those folders exists. Otherwise false.
     */
    private static boolean checkPath() {
        File path = new File("plugins/NaiveEconomy/config.yml");
        if (path.isFile()) {
            return true;
        }
        return false;
    }

}
