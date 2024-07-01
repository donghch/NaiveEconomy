package me.henrydhc.naiveeconomy.cmdhandler;

import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.lang.LangLoader;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAccountHandler {

    private final Connector connector;
    private final Economy economy;

    public CmdAccountHandler(Connector connector, Economy economy) {
        this.connector = connector;
        this.economy = economy;
    }

    public boolean setBalance(CommandSender sender, String[] args) {

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage("Target player does not exist");
            return true;
        }

        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN()) {
                sender.sendMessage("Invalid Amount");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid Amount");
            return true;
        }

        try {
            connector.setBalance(targetPlayer.getUniqueId().toString(), amount);
        } catch (Exception e) {
            sender.sendMessage("Failed to set balance.");
            return false;
        }
        sender.sendMessage(LangLoader.getMessage("setBalance").replace("{PLAYER}", targetPlayer.getName())
            .replace("{BALANCE}", economy.format(amount))
            .replace("{UNIT}", "金币"));
        return true;
    }

    public boolean giveMoney(CommandSender sender, String[] args) {

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage("Target player does not exist");
            return true;
        }

        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN() || amount < 0) {
                sender.sendMessage("Invalid Amount");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid Amount");
            return true;
        }

        EconomyResponse response = economy.depositPlayer(targetPlayer, amount);
        if (!response.transactionSuccess()) {
            sender.sendMessage("Failed to give money");
            return true;
        }
        sender.sendMessage(LangLoader.getMessage("setBalance").replace("{PLAYER}", targetPlayer.getName())
            .replace("{BALANCE}", economy.format(response.balance))
            .replace("{UNIT}", "金币"));
        return true;
    }

    public boolean takeMoney(CommandSender sender, String[] args) {

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage("Target player does not exist");
            return true;
        }

        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN()) {
                sender.sendMessage("Invalid Amount");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid Amount");
            return true;
        }

        EconomyResponse response = economy.withdrawPlayer(targetPlayer, amount);
        if (!response.transactionSuccess()) {
            sender.sendMessage("Failed to take money");
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
            result = connector.getBalance(player.getUniqueId().toString());
        } catch (Exception e) {
            player.sendMessage("Failed to get your balance");
            return true;
        }
        player.sendMessage(LangLoader.getMessage("balance").replace("{PLAYER}", player.getName())
            .replace("{BALANCE}", String.format("%.2f", result))
            .replace("{UNIT}", "金币"));
        return true;
    }


}
