package net.redewhite.lvdataapi.utils;

import net.redewhite.lvdataapi.variables.loaders.InactivePlayerVariable;
import net.redewhite.lvdataapi.variables.loaders.PlayerVariable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.File;

import static net.redewhite.lvdataapi.developers.DatabaseAPI.getPlayerVariablesArrayList;
import static net.redewhite.lvdataapi.LvDataAPI.*;

public class YamlDatabaseAPI {

    public static Boolean savePlayerVariables(Player player) {
        File file = getPlayerFile(player);
        assert file != null;

        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        for (PlayerVariable var : getPlayerVariablesArrayList(player)) {
            configFile.set(player.getUniqueId().toString() + ".variables." + var.getVariable().getVariableName() + ".name", var.getVariable().getName());
            configFile.set(player.getUniqueId().toString() + ".variables." + var.getVariable().getVariableName() + ".value", var.getValue());
        }

        try {
            configFile.save(file);
            return true;
        } catch (IOException e) {
            if (debug) e.printStackTrace();
            return false;
        }

    }

    public static void loadPlayerVariables(Player player) {
        File file = getPlayerFile(player);
        assert file != null;
        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        int s = 0;
        for (VariableCreationController var : getVariables().keySet()) {
            if (var.getType() != variableType.TEMPORARY) {
                if (configFile.getString(player.getUniqueId().toString() + ".variables." + var.getVariableName() + ".name") == null) {
                    configFile.set(player.getUniqueId().toString() + ".variables." + var.getVariableName() + ".name", var.getName());
                    configFile.set(player.getUniqueId().toString() + ".variables." + var.getVariableName() + ".value", var.getValue());
                    s++;
                }
            }
        }

        if (s != 0) {
            try {
                configFile.save(file);
            } catch (IOException e) {
                if (debug) e.printStackTrace();
            }
        }


        if (configFile.getConfigurationSection(player.getUniqueId().toString() + ".variables") != null) {
            for (Object vars : configFile.getConfigurationSection(player.getUniqueId().toString() + ".variables").getKeys(false)) {
                new InactivePlayerVariable(vars.toString(), configFile.get(player.getUniqueId().toString() + ".variables." + vars + ".value"), player);
            }
        }
    }

    public static void loadPlayerVariable(VariableCreationController variable) {
        for (Player player : instance.getServer().getOnlinePlayers()) {
            File file = getPlayerFile(player);
            assert file != null;

            YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
            if (configFile.get(player.getUniqueId().toString() + ".variables." + variable.getVariableName() + ".name") == null) {
                configFile.set(player.getUniqueId().toString() + ".variables." + variable.getVariableName() + ".name", variable.getName());
                configFile.set(player.getUniqueId().toString() + ".variables." + variable.getVariableName() + ".value", variable.getValue());

                try {
                    configFile.save(file);
                } catch (IOException e) {
                    if (debug) e.printStackTrace();
                }

            }
            Bukkit.broadcastMessage("Loaded!");
            new PlayerVariable(player, variable, configFile.get(player.getUniqueId().toString() + ".variables." + variable.getVariableName() + ".value"));
        }
    }

    public static File getPlayerFile(Player player) {
        String playerName = getPlayerConfigName(player);
        File file;
        if (config.getBoolean("File for every Player.Enabled")) {
            file = new File(instance.getDataFolder() + config.getString("File for every Player.Files path") + playerName + ".yml");
        } else {
            file = new File(instance.getDataFolder() + config.getString("File for every Player.Files path") + config.getString("Database Name") + ".yml");
        }
        if (file.getParentFile().mkdirs()) {
            YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
            if (configFile.get(player.getUniqueId().toString() + ".nickname") == null) {
                configFile.set(player.getUniqueId().toString() + ".nickname", player.getName());
                configFile.set(player.getUniqueId().toString() + ".last-update", now);
                try {
                    configFile.save(file);
                } catch (IOException e) {
                    if (debug) e.printStackTrace();
                    return null;
                }
            }
        }
        return file;
    }

    public static String getPlayerConfigName(Player player) {
        if (config.getBoolean("Save players as UUID")) return player.getUniqueId().toString();
        else return player.getName();
    }
}
