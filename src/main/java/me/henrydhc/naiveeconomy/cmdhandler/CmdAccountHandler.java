package me.henrydhc.naiveeconomy.cmdhandler;

import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.economy.MainEconomy;
import me.henrydhc.naiveeconomy.lang.LangLoader;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAccountHandler {

    private final Connector connector;
    private final MainEconomy economy;

    public CmdAccountHandler(Connector connector, MainEconomy economy) {
        this.connector = connector;
        this.economy = economy;
    }

    public boolean setBalance(CommandSender sender, String[] args) {

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);

        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN()) {
                sender.sendMessage(LangLoader.getMessage("invalidAmount"));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(LangLoader.getMessage("invalidAmount"));
            return true;
        }

        if(!connector.setBalance(targetPlayer.getUniqueId().toString(), amount)) {
            sender.sendMessage("Failed to set money.");
        }

        sender.sendMessage(LangLoader.getMessage("setBalance").replace("{PLAYER}", targetPlayer.getName())
            .replace("{BALANCE}", economy.format(amount))
            .replace("{UNIT}", "金币"));
        return true;
    }

    public boolean giveMoney(CommandSender sender, String[] args) {

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);

        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN() || amount < 0) {
                sender.sendMessage(LangLoader.getMessage("invalidAmount"));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(LangLoader.getMessage("invalidAmount"));
            return true;
        }

        EconomyResponse response = economy.depositPlayer(targetPlayer, amount);
        if (!response.transactionSuccess()) {
            sender.sendMessage(LangLoader.getMessage("noMoney"));
            return true;
        }
        sender.sendMessage(LangLoader.getMessage("setBalance").replace("{PLAYER}", targetPlayer.getName())
            .replace("{BALANCE}", economy.format(response.balance))
            .replace("{UNIT}", "金币"));
        return true;
    }

    public boolean takeMoney(CommandSender sender, String[] args) {

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);

        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN()) {
                sender.sendMessage(LangLoader.getMessage("invalidAmount"));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(LangLoader.getMessage("invalidAmount"));
            return true;
        }

        EconomyResponse response = economy.withdrawPlayer(targetPlayer, amount);
        if (!response.transactionSuccess()) {
            sender.sendMessage(LangLoader.getMessage("noMoney"));
            return true;
        }
        sender.sendMessage(LangLoader.getMessage("setBalance").replace("{PLAYER}", targetPlayer.getName())
            .replace("{BALANCE}", economy.format(response.balance))
            .replace("{UNIT}", "金币"));
        return true;
    }

    public boolean getBalance(Player player) {
        double result;
        try {
            result = economy.getBalance(player);
        } catch (Exception e) {
            player.sendMessage("Failed to get your balance");
            return true;
        }
        player.sendMessage(LangLoader.getMessage("balance").replace("{PLAYER}", player.getName())
            .replace("{BALANCE}", economy.format(result))
            .replace("{UNIT}", "金币"));
        return true;
    }


}
