package net.redewhite.lvdataapi.listeners;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.redewhite.lvdataapi.developers.API.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

@SuppressWarnings("unused")
public class BukkitDefaultEvents implements Listener {

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        if (config.getBoolean("Plugin auto player loading")) loadPlayer(event.getPlayer(), config.getBoolean("Async variables loading"));
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        if (config.getBoolean("Plugin auto player unloading")) unloadPlayer(event.getPlayer());
    }

}
