package me.henrydhc.naiveeconomy.listeners;

import me.henrydhc.naiveeconomy.lang.LangLoader;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EconomyListener implements Listener {

    private final Economy economy;

    public EconomyListener(Economy economy) {
        this.economy = economy;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!economy.hasAccount(player)) {
            economy.createPlayerAccount(player);
            player.sendMessage(LangLoader.getMessage("onRegister"));
        }
    }

}
