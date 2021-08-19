package net.redewhite.lvdataapi.utils;

import net.redewhite.lvdataapi.variables.loaders.InactivePlayerLoader;
import net.redewhite.lvdataapi.variables.loaders.PlayerVariableLoader;
import net.redewhite.lvdataapi.variables.loaders.InactiveTextLoader;
import net.redewhite.lvdataapi.variables.loaders.TextVariableLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import net.redewhite.lvdataapi.variables.receptors.TextVariableReceptor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.File;

import static net.redewhite.lvdataapi.developers.GeneralAPI.getPlayerTypeVariablesArrayList;
import static net.redewhite.lvdataapi.developers.GeneralAPI.getTextTypeVariablesArrayList;
import static net.redewhite.lvdataapi.developers.DatabaseAPI.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

public class YamlDatabaseAPI {

    public static Boolean saveTextTypeVariables(TextVariableReceptor textVariable) {
        File file = getTextTypeFile(textVariable);
        assert file != null;

        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        for (TextVariableLoader var : getTextTypeVariablesArrayList(textVariable)) {
            configFile.set(textVariable.getName() + ".variables." + var.getVariable().getVariableName() + ".name", var.getVariable().getName());
            configFile.set(textVariable.getName() + ".variables." + var.getVariable().getVariableName() + ".value", var.getValue());
        }
        try {
            configFile.save(file);
            return true;
        } catch (IOException e) {
            if (debug) e.printStackTrace();
            return false;
        }
    }
    public static Boolean savePlayerTypeVariables(Player player) {
        File file = getPlayerTypeFile(player);
        assert file != null;

        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        for (PlayerVariableLoader var : getPlayerTypeVariablesArrayList(player)) {
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

    public static void loadTextTypeVariables(TextVariableReceptor textVariable) {
        File file = getTextTypeFile(textVariable);
        assert file != null;
        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        int s = 0;
        for (VariableCreationController var : getVariables().keySet()) {
            if (var.getType() != variableType.TEMPORARY) {
                if (var.getVariableTextType()) {
                    if (configFile.get(textVariable.getName() + ".variables." + var.getVariableName() + ".name") == null) {
                        configFile.set(textVariable.getName() + ".variables." + var.getVariableName() + ".name", var.getName());
                        configFile.set(textVariable.getName() + ".variables." + var.getVariableName() + ".value", var.getValue());
                        s++;
                    }
                }
            }
        }

        if (s != 0) saveFile(configFile, file);

        if (configFile.getConfigurationSection(textVariable.getName() + ".variables") != null) {
            for (Object vars : configFile.getConfigurationSection(textVariable.getName() + ".variables").getKeys(false)) {
                new InactiveTextLoader(vars.toString(), configFile.get(textVariable.getName() + ".variables." + vars + ".value"), textVariable);
            }
        }
    }
    public static void loadPlayerTypeVariables(Player player) {
        File file = getPlayerTypeFile(player);
        assert file != null;
        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        int s = 0;
        for (VariableCreationController var : getVariables().keySet()) {
            if (var.getType() != variableType.TEMPORARY) {
                if (!var.getVariableTextType()) {
                    if (configFile.getString(player.getUniqueId().toString() + ".variables." + var.getVariableName() + ".name") == null) {
                        configFile.set(player.getUniqueId().toString() + ".variables." + var.getVariableName() + ".name", var.getName());
                        configFile.set(player.getUniqueId().toString() + ".variables." + var.getVariableName() + ".value", var.getValue());
                        s++;
                    }
                }
            }
        }

        if (s != 0) saveFile(configFile, file);

        if (configFile.getConfigurationSection(player.getUniqueId().toString() + ".variables") != null) {
            for (Object vars : configFile.getConfigurationSection(player.getUniqueId().toString() + ".variables").getKeys(false)) {
                new InactivePlayerLoader(vars.toString(), configFile.get(player.getUniqueId().toString() + ".variables." + vars + ".value"), player);
            }
        }
    }

    public static void loadYamlVariable(VariableCreationController variable) {
        File file;
        if (variable.getVariableTextType()) {
            for (TextVariableLoader var : getTextVariables().keySet()) {
                file = getTextTypeFile(var.getTextVariable());
                assert file != null;
                YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
                if (configFile.get(var.getTextVariable().getName() + ".variables." + variable.getVariableName() + ".name") == null) {
                    configFile.set(var.getTextVariable().getName() + ".variables." + variable.getVariableName() + ".name", variable.getName());
                    configFile.set(var.getTextVariable().getName() + ".variables." + variable.getVariableName() + ".value", variable.getValue());
                    saveFile(configFile, file);
                }
                new TextVariableLoader(var.getTextVariable(), variable, configFile.get(var.getTextVariable().getName() + ".variables." + variable.getVariableName() + ".value"));
            }
        } else {
            for (Player player : instance.getServer().getOnlinePlayers()) {
                file = getPlayerTypeFile(player);
                assert file != null;
                YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
                if (configFile.get(player.getUniqueId().toString() + ".variables." + variable.getVariableName() + ".name") == null) {
                    configFile.set(player.getUniqueId().toString() + ".variables." + variable.getVariableName() + ".name", variable.getName());
                    configFile.set(player.getUniqueId().toString() + ".variables." + variable.getVariableName() + ".value", variable.getValue());
                    saveFile(configFile, file);
                }
                new PlayerVariableLoader(player, variable, configFile.get(player.getUniqueId().toString() + ".variables." + variable.getVariableName() + ".value"));
            }
        }
    }

    public static File getTextTypeFile(TextVariableReceptor textVariable) {
        File file = new File(instance.getDataFolder() + File.separator + config.getString("Text variables files path") + File.separator + textVariable.getVariableName() + ".yml");

        if (file.getParentFile().mkdirs()) {
            YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
            if (configFile.get(textVariable.getName() + ".last-update") == null) {
                configFile.set(textVariable.getName() + ".last-update", now);
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
    public static File getPlayerTypeFile(Player player) {
        String playerName = getPlayerConfigName(player);
        File file;
        if (config.getBoolean("File for every Player.Enabled")) {
            file = new File(instance.getDataFolder() + File.separator + config.getString("File for every Player.Player variables files path") + File.separator + playerName + ".yml");
        } else {
            file = new File(instance.getDataFolder() + File.separator + config.getString("File for every Player.Player variables files path") + File.separator + config.getString("Database Name") + ".yml");
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
