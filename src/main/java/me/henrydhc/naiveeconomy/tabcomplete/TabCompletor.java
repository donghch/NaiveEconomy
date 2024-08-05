package me.henrydhc.naiveeconomy.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class TabCompletor implements TabCompleter {

    private static final List<String> FIRST_LEVEL = List.of(
        "pay", "set", "take",
        "give", "balance"
    );

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
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
