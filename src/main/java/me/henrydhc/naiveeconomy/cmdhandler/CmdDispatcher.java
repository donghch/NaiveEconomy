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

        switch (strings[0]) {
            case "pay":
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage("Only player can use this command!");
                    return true;
                }
                return paymentHandler.doPayment((Player) commandSender, strings);
            case "set":
                if (strings.length < 3) {
                    return true;
                }
                if (!commandSender.hasPermission("naiveeconomy.set")) {
                    commandSender.sendMessage(LangLoader.getMessage("noPerm"));
                    return true;
                }
                return accountHandler.setBalance(commandSender, strings);
            case "give":
                if (strings.length < 3) {
                    return true;
                }
                if (!commandSender.hasPermission("naiveeconomy.give")) {
                    commandSender.sendMessage(LangLoader.getMessage("noPerm"));
                    return true;
                }
                return accountHandler.giveMoney(commandSender, strings);
            case "take":
                if (strings.length < 3) {
                    return true;
                }
                if (!commandSender.hasPermission("naiveeconomy.take")) {
                    commandSender.sendMessage(LangLoader.getMessage("noPerm"));
                    return true;
                }
                return accountHandler.takeMoney(commandSender, strings);
            case "balance":
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage("Only player can use this command!");
                    return true;
                }
                return accountHandler.getBalance((Player) commandSender);
            case "info":
                commandSender.sendMessage(LangLoader.getPluginInfo());
                return true;
            default:
                return true;
        }
    }
}
