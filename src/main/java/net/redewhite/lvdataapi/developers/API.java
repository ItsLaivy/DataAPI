package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.variables.loaders.InactivePlayerVariable;
import net.redewhite.lvdataapi.variables.loaders.PlayerVariable;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;

import static net.redewhite.lvdataapi.developers.DatabaseAPI.databaseSavePlayer;
import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.MYSQL;
import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.SQLITE;
import static net.redewhite.lvdataapi.LvDataAPI.variableType.*;
import static net.redewhite.lvdataapi.developers.DatabaseAPI.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

@SuppressWarnings("unused")
public class API {

    public static Boolean isLoaded(Player player) {
        for (PlayerVariable var : getPlayers().keySet()) {
            if (var.getPlayer() == player) return true;
        }
        return false;
    }

    public static void savePlayer(Player player, Boolean async) {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(instance, () -> databaseSavePlayer(player));
        else databaseSavePlayer(player);
    }

    public static void loadPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> loadDatabasePlayerVariables(player));
    }
    public static void loadPlayer(Player player, Boolean async) {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(instance, () -> loadDatabasePlayerVariables(player));
        else loadDatabasePlayerVariables(player);
    }

    public static void unloadPlayer(Player player) {
        unloadDatabasePlayerVariables(player);
    }

    public static ArrayList<String> getArrayVariable(Plugin plugin, String name, Player player) {
        PlayerVariable var = getPlayerVariable(plugin, player, name);

        ArrayList<String> array = new ArrayList<>();
        if (var != null) {
            if (var.getVariable().getType() == ARRAY) {
                String[] split = var.getValue().toString().split("<SPLIT!>");
                for (String e : split) array.add(getVariableUnhashedValue(e).toString());
            }
        }
        return array;
    }

    public static Object getVariable(Plugin plugin, String name, Player player) {
        PlayerVariable var = getPlayerVariable(plugin, player, name);

        if (var != null) {
            return getVariableUnhashedValue(var.getValue());
        } else {
            return "";
        }
    }

    public static Boolean setVariable(Plugin plugin, String name, Player player, Object value) {
        PlayerVariable var = getPlayerVariable(plugin, player, name);
        assert var != null;

        if (var.getVariable().getType() == NORMAL) {
            if (database_type == SQLITE || database_type == MYSQL) {
                try {
                    Integer.parseInt(value.toString());
                    if (!var.getVariable().getSQLDefaultSymbol().equals("INT")) return false;
                } catch (IllegalArgumentException e) {
                    if (!var.getVariable().getSQLDefaultSymbol().equals("TEXT")) return false;
                }
            }

            var.setValue(value);
            return true;
        } else if (var.getVariable().getType() == ARRAY) {
            ArrayList<Object> array = new ArrayList<>();
            if (value != null) if (value instanceof ArrayList<?>) {
                for (Object e : new ArrayList<>(Arrays.asList(((ArrayList<?>) value).toArray()))) {
                    array.add(getVariableHashedValue(e).toString());
                }
            }

            var.setValue(array.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", ""));
            return true;
        } else if (var.getVariable().getType() == TEMPORARY) {
            var.setValue(value);
            return true;
        }

        return false;
    }

    public static Object getVariable(String inactive_variable_name, Player player) {
        for (InactivePlayerVariable var : getPlayerInactiveVariables(player)) {
            if (var.getName().equals(inactive_variable_name)) {
                if (var.getValue().equals("")) return null;
                return var.getValue();
            }
        }
        return null;
    }

    public static void stopPlugin() {
        if (config.getBoolean("close-server")) {
            instance.getServer().shutdown();
        } else {
            instance.getPluginLoader().disablePlugin(instance);
        }
    }

}
