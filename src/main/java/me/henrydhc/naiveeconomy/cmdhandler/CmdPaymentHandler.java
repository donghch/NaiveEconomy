package me.henrydhc.naiveeconomy.cmdhandler;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdPaymentHandler {

    private final Economy economy;

    public CmdPaymentHandler(Economy economy) {
        this.economy = economy;
    }

    public boolean doPayment(Player payer, String[] args) {

        if (args.length < 3) {
            payer.sendMessage("Invalid arguments");
            return true;
        }

        Player receiver = Bukkit.getPlayer(args[1]);
        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN() || amount <= 0) {
                payer.sendMessage("Invalid amount");
            }
        } catch (NumberFormatException e) {
            payer.sendMessage("Invalid amount");
            return true;
        }

        if (receiver == null) {
            payer.sendMessage("Target player is not online!");
            return true;
        }

        EconomyResponse result = economy.withdrawPlayer(payer, amount);
        if (!result.transactionSuccess()) {
            payer.sendMessage("Failed to conduct payment");
            return true;
        }

        economy.depositPlayer(receiver, amount);
        payer.sendMessage("Transaction Complete");
        if (receiver.isOnline()) {
            receiver.sendMessage(String.format("You get %s from %s", amount.toString(), payer.getName()));
        }
        return true;

    }

}
