package me.henrydhc.naiveeconomy.cmdhandler;

import me.henrydhc.naiveeconomy.connector.Connector;
import me.henrydhc.naiveeconomy.lang.LangLoader;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CmdDispatcher implements CommandExecutor {

    private final CmdPaymentHandler paymentHandler;
    private final CmdAccountHandler accountHandler;


    public CmdDispatcher(Economy economy, Connector connector) {
        this.paymentHandler = new CmdPaymentHandler(economy);
        this.accountHandler = new CmdAccountHandler(connector, economy);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only player can use this command!");
            return true;
        }

        Player player = (Player) commandSender;

        switch (strings[0]) {
            case "pay":
                return paymentHandler.doPayment(player, strings);
            case "set":
                if (strings.length < 3) {
                    return true;
                }
                return accountHandler.setBalance(player, strings);
            case "give":
                if (strings.length < 3) {
                    return true;
                }
                return accountHandler.giveMoney(player, strings);
            case "take":
                if (strings.length < 3) {
                    return true;
                }
                return accountHandler.takeMoney(player, strings);
            case "balance":
                return accountHandler.getBalance(player);
            case "info":
                player.sendMessage(LangLoader.getPluginInfo());
                return true;
            default:
                return true;
        }
    }
}
