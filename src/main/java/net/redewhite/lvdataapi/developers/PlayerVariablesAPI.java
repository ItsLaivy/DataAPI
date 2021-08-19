package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.variables.loaders.PlayerVariableLoader;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import static net.redewhite.lvdataapi.developers.DatabaseAPI.databaseSavePlayer;
import static net.redewhite.lvdataapi.developers.DatabaseAPI.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

@SuppressWarnings("unused")
public class PlayerVariablesAPI {

    public static Boolean isLoaded(Player player) {
        for (PlayerVariableLoader var : getPlayerVariables().keySet()) {
            if (var.getPlayer() == player) return true;
        }
        return false;
    }

    public static void savePlayerType(Player player, Boolean async) {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(instance, () -> databaseSavePlayer(player));
        else databaseSavePlayer(player);
    }
    public static void loadPlayerType(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> loadDatabasePlayerVariables(player));
    }
    public static void loadPlayerType(Player player, Boolean async) {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(instance, () -> loadDatabasePlayerVariables(player));
        else loadDatabasePlayerVariables(player);
    }

    public static void unloadPlayerType(Player player) {
        unloadDatabasePlayerVariables(player);
    }

}
