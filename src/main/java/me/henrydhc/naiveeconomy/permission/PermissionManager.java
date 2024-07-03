package me.henrydhc.naiveeconomy.permission;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.Map;

public class PermissionManager {

    private static final Map<String, PermissionDefault> nodes = Map.of(
        "pay", PermissionDefault.TRUE,
        "set", PermissionDefault.OP,
        "give", PermissionDefault.OP,
        "take", PermissionDefault.OP
    );

    public static void registerPermissions() {
        PluginManager manager = Bukkit.getPluginManager();
        for (Map.Entry<String, PermissionDefault> node: nodes.entrySet()) {
            manager.addPermission(new Permission("naiveeconomy." + node.getKey(), node.getValue()));
        }
    }

}
