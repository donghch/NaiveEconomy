package me.henrydhc.naiveeconomy.economy;

import me.henrydhc.naiveeconomy.connector.Connector;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class MainEconomy implements Economy {

    private final Connector connector;
    private final Plugin plugin;

    public MainEconomy(Connector connector, Plugin plugin) {
        this.connector = connector;
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "NaiveEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        NumberFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(v);
    }

    @Override
    public String currencyNamePlural() {
        return "金币";
    }

    @Override
    public String currencyNameSingular() {
        return "金币";
    }

    @Override
    public boolean hasAccount(String s) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(s);
        try {
            return connector.hasRecord(player.getUniqueId().toString());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        String playerID = offlinePlayer.getUniqueId().toString();
        try {
            return connector.hasRecord(playerID);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String worldName) {
        return hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String s) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(s);
        return getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        String playerID = offlinePlayer.getUniqueId().toString();
        try {
            if (hasAccount(playerID))
                return connector.getBalance(playerID);
            connector.setBalance(playerID, 0);
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return has(plugin.getServer().getOfflinePlayer(playerName), amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        String playerID = offlinePlayer.getUniqueId().toString();
        double currBalance;
        if (!hasAccount(playerID)) {
            currBalance = 0;
            connector.setBalance(playerID, 0);
        } else {
            try {
                currBalance = connector.getBalance(playerID);
            } catch (Exception e) {
                return false;
            }
        }
        return currBalance >= amount;
    }

    @Override
    public boolean has(String playerName, String s1, double v) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        return has(player, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return withdrawPlayer(plugin.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        String playerID = offlinePlayer.getUniqueId().toString();
        double currentBalance;
        try {
            currentBalance = connector.getBalance(playerID);
        } catch (Exception e) {
            return new EconomyResponse(0, -1, EconomyResponse.ResponseType.FAILURE, "Failed to connect to database");
        }

        if (currentBalance < v) {
            return new EconomyResponse(0, currentBalance, EconomyResponse.ResponseType.FAILURE, null);
        }

        connector.setBalance(playerID, currentBalance - v);
        return new EconomyResponse(v, currentBalance - v, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(plugin.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        return depositPlayer(plugin.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        String playerID = offlinePlayer.getUniqueId().toString();
        double currentBalance;
        try {
            currentBalance = connector.getBalance(playerID);
        } catch (Exception e) {
            return new EconomyResponse(0, -1, EconomyResponse.ResponseType.FAILURE, "Failed to connect to database");
        }

        connector.setBalance(playerID, currentBalance + v);
        return new EconomyResponse(v, currentBalance + v, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(plugin.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return createPlayerAccount(plugin.getServer().getOfflinePlayer(s));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        try {
            if (hasAccount(offlinePlayer)) {
                return true;
            }
            connector.setBalance(offlinePlayer.getUniqueId().toString(), 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return createPlayerAccount(plugin.getServer().getOfflinePlayer(s));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return createPlayerAccount(offlinePlayer);
    }
}
