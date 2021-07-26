package net.redewhite.lvdataapi.events;

import net.redewhite.lvdataapi.LvDataPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.redewhite.lvdataapi.LvDataPluginAPI.*;

public class BukkitDefaultEvents implements Listener {

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent e) {
        unloadPlayer(e.getPlayer());
    }

    @EventHandler
    public void PlayerChat(AsyncPlayerChatEvent e) {
        if (e.getMessage().equalsIgnoreCase("1")) {
            Bukkit.broadcastMessage((String) getVariable(LvDataPlugin.getInstance(), e.getPlayer(), "examplevar"));
        } else if (e.getMessage().equalsIgnoreCase("2")) {
            setVariable(LvDataPlugin.getInstance(), e.getPlayer(), "examplevar", "eiaporra");
        } else if (e.getMessage().equalsIgnoreCase("3")) {
            savePlayer(e.getPlayer());
        }
    }

}
