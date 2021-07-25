package net.redewhite.LVDataAPI.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.redewhite.LVDataAPI.API.*;

public class BukkitDefaultEvents implements Listener {

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent e) {
        unloadPlayer(e.getPlayer());
    }

}
