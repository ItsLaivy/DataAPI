package net.redewhite.lvdataapi.events;

import net.redewhite.lvdataapi.creators.VariablesTable;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.redewhite.lvdataapi.DataAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.redewhite.lvdataapi.developers.EasyAPI.*;

@SuppressWarnings("unused")
public class BukkitDefaultEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        for (VariablesTable table : DataAPI.getTables().keySet()) {
            try {
                receptorTypeDatabaseLoad(table.getPlugin(), e.getPlayer(), table);
            } catch (NullPointerException ignore) {
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        for (VariablesTable table : DataAPI.getTables().keySet()) {
            try {
                receptorTypeDatabaseUnload(table.getPlugin(), e.getPlayer(), table);
            } catch (NullPointerException ignore) {
            }
        }
    }

}
