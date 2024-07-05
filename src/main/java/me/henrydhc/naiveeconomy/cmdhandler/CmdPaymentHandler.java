package me.henrydhc.naiveeconomy.cmdhandler;

import me.henrydhc.naiveeconomy.config.ConfigLoader;
import me.henrydhc.naiveeconomy.lang.LangLoader;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CmdPaymentHandler {

    private final Economy economy;

    public CmdPaymentHandler(Economy economy) {
        this.economy = economy;
    }

    public boolean doPayment(Player payer, String[] args) {

        if (args.length < 3) {
            payer.sendMessage(LangLoader.getMessage("invalidAmount"));
            return true;
        }

        Player receiver = Bukkit.getPlayer(args[1]);
        Double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount.isNaN() || amount <= 0) {
                payer.sendMessage(LangLoader.getMessage("invalidAmount"));
                return true;
            }
        } catch (NumberFormatException e) {
            payer.sendMessage(LangLoader.getMessage("invalidAmount"));
            return true;
        }

        if (receiver == null) {
            payer.sendMessage(LangLoader.getMessage("playerNotExist"));
            return true;
        }

        EconomyResponse result = economy.withdrawPlayer(payer, amount);
        if (!result.transactionSuccess()) {
            payer.sendMessage(LangLoader.getMessage("noMoney"));
            return true;
        }

        economy.depositPlayer(receiver, amount);

        FileConfiguration config = ConfigLoader.getConfiguration();
        payer.sendMessage(LangLoader.getMessage("onSenderSend").replace("{PLAYER}", receiver.getName())
            .replace("{AMOUNT}", economy.format(amount))
            .replace("{UNIT}", amount > 1 ? config.getString("currency-plural"):config.getString("currency-singular")));
        if (receiver.isOnline()) {
            payer.sendMessage(LangLoader.getMessage("onReceiverGet").replace("{PLAYER}", payer.getName())
                .replace("{AMOUNT}", economy.format(amount))
                .replace("{UNIT}", amount > 1 ? config.getString("currency-plural"):config.getString("currency-singular")));
        }
        return true;

    }

}
