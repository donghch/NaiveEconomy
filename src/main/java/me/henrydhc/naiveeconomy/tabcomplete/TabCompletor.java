package me.henrydhc.naiveeconomy.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TabCompletor implements TabCompleter {

    private static List<String> FIRST_LEVEL = List.of(
        "pay"
    );

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            return null;
        }

        switch (strings.length) {
            case 1:
                return FIRST_LEVEL;
            default:
                return null;
        }
    }
}
