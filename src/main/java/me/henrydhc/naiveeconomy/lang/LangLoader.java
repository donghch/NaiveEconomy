package me.henrydhc.naiveeconomy.lang;

import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangLoader {

    private static String[] LANG_FIELDS = {
        "title", "onRegister", "setBalance",
        "balance", "onSenderSend", "onReceiverGet",
        "noPerm"
    };

    private static List<String> SUPPORTED_LANG = List.of(
        "zh-cn", "en"
    );

    private static Map<String, String> messages = new HashMap<>();

    public static boolean loadLang(String lang, Plugin plugin) {
        if (!SUPPORTED_LANG.contains(lang)) {
            lang = "en";
        }

        if (!makeLang(lang, plugin)) {
            return false;
        }

        FileConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(new File(String.format("plugins/NaiveEconomy/lang/%s.yml", lang)));
        } catch (Exception e) {
            return false;
        }
        for (String field: LANG_FIELDS) {
            messages.put(field, configuration.getString(field));
        }
        return true;
    }

    private static boolean makeLang(String lang, Plugin plugin) {
        File path = new File("plugins/NaiveEconomy/lang");
        if (!path.isDirectory()) {
            if (!path.mkdirs()) {
                return false;
            }
        }

        File langFile = new File(String.format("plugins/NaiveEconomy/lang/%s.yml", lang));
        if (!langFile.exists()) {
            try {
                InputStream inputStream = plugin.getResource(String.format("lang/%s.yml", lang));
                OutputStream outputStream = new FileOutputStream(langFile);
                ByteStreams.copy(inputStream, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static String getMessage(String field) {
        String data = messages.get(field);
        if (data == null) {
            return "Unknown Lang";
        }
        return ChatColor.translateAlternateColorCodes('&',messages.get("title") + " " + data);
    }

    public static String getPluginInfo() {
        return new StringBuilder()
            .append("=".repeat(10)).append("NaiveEconomy").append("=".repeat(10)).append("\n")
            .append("Author(作者): dieshenken").append("\n")
            .append("Version(版本): 1.0.0").append("\n")
            .append("=".repeat(20 + "NaiveEconomy".length())).toString();
    }

}
