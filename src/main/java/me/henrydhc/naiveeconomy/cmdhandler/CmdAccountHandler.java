package me.henrydhc.naiveeconomy.cmdhandler;

import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.lang.LangLoader;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdAccountHandler {

    private final Connector connector;
    private final Economy economy;

    public CmdAccountHandler(Connector connector, Economy economy) {
        this.connector = connector;
        this.economy = economy;
    }

    public boolean setBalance(Player player, String[] args) {

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            player.sendMessage("Target player does not exist");
            return true;
        }

        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN()) {
                player.sendMessage("Invalid Amount");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid Amount");
            return true;
        }

        try {
            connector.setBalance(targetPlayer.getUniqueId().toString(), amount);
        } catch (Exception e) {
            player.sendMessage("Failed to set balance.");
            return false;
        }
        player.sendMessage(LangLoader.getMessage("setBalance").replace("{PLAYER}", player.getName())
            .replace("{BALANCE}", String.valueOf(amount))
            .replace("{UNIT}", "金币"));
        return true;
    }

    public boolean giveMoney(Player player, String[] args) {

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            player.sendMessage("Target player does not exist");
            return true;
        }

        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN() || amount < 0) {
                player.sendMessage("Invalid Amount");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid Amount");
            return true;
        }

        EconomyResponse response = economy.depositPlayer(player, amount);
        if (!response.transactionSuccess()) {
            player.sendMessage("Failed to give money");
            return true;
        }
        player.sendMessage(LangLoader.getMessage("setBalance").replace("{PLAYER}", player.getName())
            .replace("{BALANCE}", String.valueOf(response.balance))
            .replace("{UNIT}", "金币"));
        return true;
    }

    public boolean takeMoney(Player player, String[] args) {

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            player.sendMessage("Target player does not exist");
            return true;
        }

        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN()) {
                player.sendMessage("Invalid Amount");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid Amount");
            return true;
        }

        EconomyResponse response = economy.withdrawPlayer(player, amount);
        if (!response.transactionSuccess()) {
            player.sendMessage("Failed to take money");
            return true;
        }
        player.sendMessage(LangLoader.getMessage("setBalance").replace("{PLAYER}", player.getName())
            .replace("{BALANCE}", String.valueOf(response.balance))
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
            .replace("{BALANCE}", String.valueOf(result))
            .replace("{UNIT}", "金币"));
        return true;
    }


}
