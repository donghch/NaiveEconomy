package me.henrydhc.naiveeconomy.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigLoader {

    private static List<String> configFields = List.of();
    private static FileConfiguration configuration;

    public static boolean loadConfig() {
        FileConfiguration config = new YamlConfiguration();

        if (!checkPath()) {
            return false;
        }
        return false;

    }

    /**
     * Check and generate necessary folders for config file
     * @return `True` if those folders exists. Otherwise false.
     */
    private static boolean checkPath() {
        File path = new File("plugins/NaiveEconomy");
        if (path.isDirectory()) {
            return true;
        }
        return path.mkdirs();
    }

}
