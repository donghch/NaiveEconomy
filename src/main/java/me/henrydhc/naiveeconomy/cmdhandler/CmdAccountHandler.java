package me.henrydhc.naiveeconomy.cmdhandler;

import me.henrydhc.naiveeconomy.config.ConfigLoader;
import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.economy.MainEconomy;
import me.henrydhc.naiveeconomy.lang.LangLoader;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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


        // Check if that account exists
        if (!economy.hasAccount(targetPlayer)) {
            sender.sendMessage(LangLoader.getMessage("playerNotExist"));
            return true;
        }

        // Try parse amount data
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


        // Try set balance

        if(!connector.setBalance(targetPlayer.getUniqueId(), amount)) {
            sender.sendMessage("Failed to set money.");
        }

        FileConfiguration config = ConfigLoader.getConfiguration();
        String msg = LangLoader.getMessage("setBalance").replace("{PLAYER}", targetPlayer.getName())
            .replace("{BALANCE}", economy.format(amount))
            .replace("{UNIT}", amount > 1 ? config.getString("currency-plural"):config.getString("currency-singular"));

        sender.sendMessage(msg);
        return true;
    }

    public boolean giveMoney(CommandSender sender, String[] args) {


        // Check if that player has account

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
        if (!economy.hasAccount(targetPlayer)) {
            sender.sendMessage(LangLoader.getMessage("playerNotExist"));
            return true;
        }

        // Try parsing amount data
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

        // Deposit
        EconomyResponse response = economy.depositPlayer(targetPlayer, amount);
        if (!response.transactionSuccess()) {
            sender.sendMessage(LangLoader.getMessage("noMoney"));
            return true;
        }

        FileConfiguration config = ConfigLoader.getConfiguration();
        String msg = LangLoader.getMessage("setBalance").replace("{PLAYER}", targetPlayer.getName())
            .replace("{BALANCE}", economy.format(response.balance))
            .replace("{UNIT}", response.balance > 1 ? config.getString("currency-plural"):config.getString("currency-singular"));

        sender.sendMessage(msg);
        return true;
    }

    public boolean takeMoney(CommandSender sender, String[] args) {


        // Check if player has account

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
        if (!economy.hasAccount(targetPlayer)) {
            sender.sendMessage(LangLoader.getMessage("playerNotExist"));
            return true;
        }

        // Try parsing amount data
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

        // Take money
        EconomyResponse response = economy.withdrawPlayer(targetPlayer, amount);
        if (!response.transactionSuccess()) {
            sender.sendMessage(LangLoader.getMessage("noMoney"));
            return true;
        }
        FileConfiguration config = ConfigLoader.getConfiguration();
        String msg = LangLoader.getMessage("setBalance").replace("{PLAYER}", targetPlayer.getName())
            .replace("{BALANCE}", economy.format(response.balance))
            .replace("{UNIT}", response.balance > 1 ? config.getString("currency-plural"):config.getString("currency-singular"));

        sender.sendMessage(msg);
        return true;
    }

    public boolean getBalance(CommandSender sender, String[] args) {
        double result;
        OfflinePlayer targetPlayer;


        // Check this player or that player?

        if (args.length == 2) {
            targetPlayer = Bukkit.getOfflinePlayer(args[1]);
        } else {
            targetPlayer = (Player)sender;
        }


        // Check if player has account
        if (!economy.hasAccount(targetPlayer)) {
            sender.sendMessage(LangLoader.getMessage("playerNotExist"));
            return true;
        }

        try {
            result = economy.getBalance(targetPlayer);
        } catch (Exception e) {
            sender.sendMessage("Failed to get your balance");
            return true;
        }

        FileConfiguration config = ConfigLoader.getConfiguration();
        String msg = LangLoader.getMessage("balance").replace("{PLAYER}", targetPlayer.getName())
            .replace("{BALANCE}", economy.format(result))
            .replace("{UNIT}", result > 1 ? config.getString("currency-plural"):config.getString("currency-singular"));

        sender.sendMessage(msg);
        return true;
    }


}
