package net.redewhite.lvdataapi.utils;

import net.redewhite.lvdataapi.variables.receptors.TextVariableReceptor;
import net.redewhite.lvdataapi.variables.loaders.PlayerVariableLoader;
import net.redewhite.lvdataapi.variables.loaders.TextVariableLoader;
import net.redewhite.lvdataapi.LvDataAPI.variableType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

import static net.redewhite.lvdataapi.developers.GeneralAPI.getInactivePlayerTypeVariable;
import static net.redewhite.lvdataapi.developers.GeneralAPI.getInactiveTextTypeVariable;
import static net.redewhite.lvdataapi.developers.DatabaseAPI.getVariableHashedValue;
import static net.redewhite.lvdataapi.developers.PlayerVariablesAPI.isLoaded;
import static net.redewhite.lvdataapi.utils.SQLDatabaseAPI.createColumn;
import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.*;
import static net.redewhite.lvdataapi.LvDataAPI.variableType.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

public class VariableCreationController {

    private final boolean textvariable;
    private final variableType type;
    private final String sqltype;
    private final Plugin plugin;
    private final Object value;
    private final String name;
    private String varname;

    public VariableCreationController(Plugin plugin, String name, Object defaultvalue, variableType type, Boolean textvariable) {

        if (plugin == null) throw new NullPointerException("Variable plugin cannot be null!");
        else if (name == null) throw new NullPointerException("Variable name cannot be null!");
        else if (type == null) throw new NullPointerException("Variable data type cannot be null!");

        this.textvariable = textvariable;
        this.plugin = plugin;
        this.name = name;

        if (defaultvalue instanceof ArrayList) {
            ArrayList<String> array = new ArrayList<>();
            for (Object e : ((ArrayList<?>) defaultvalue).toArray()) {
                array.add(getVariableHashedValue(e).toString());
            }
            this.value = array.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
        } else {
            this.value = getVariableHashedValue(defaultvalue);
        }

        this.type = type;
        this.sqltype = parseType(this);

        String[] blocked = "-S,S=S[S]S.S/S*S-S+S;S:".split("S");
        for (String block : blocked) {
            if (name.contains(block)) {
                broadcastColoredMessage("&cVariable '&4" + name + "&c' couldn't be created because it has illegal characters ('&4" + block + "&c')");
                return;
            }
        }

        for (VariableCreationController var : getVariables().keySet()) {
            if (var.getName().equals(name)) {
                if (var.getPlugin() == plugin) {
                    broadcastColoredMessage("&cVariable '&4" + name + "&c' couldn't be created because already some variable of this plugin loaded with that name.");
                    return;
                }
            }
        }

        String message = null;
        String message_error = null;
        if (type == ARRAY) {
            this.varname = plugin.getName() + "_ARRAYLIST_" + name;
            message = "§barray §avariable";
            message_error = "§barray §cvariable";
        } else if (type == TEMPORARY) {
            this.varname = plugin.getName() + "_TEMPORARY_" + name;
            message = "§6temporary §avariable";
            message_error = "§6temporary §cvariable";
        } else if (type == NORMAL) {
            this.varname = plugin.getName() + "_NORMAL_" + name;
            message = "§avariable";
            message_error = "§cvariable";
        }

        if (type == ARRAY || type == NORMAL) {
            int createid = tryCreateColumn(this);
            if (createid == 1) {
                if (textvariable) {
                    for (TextVariableReceptor var : getTextVariablesNames().keySet()) {
                        new TextVariableLoader(var, this, getInactiveTextTypeVariable(varname, var));
                    }
                } else {
                    for (Player player : instance.getServer().getOnlinePlayers()) {
                        if (isLoaded(player)) {
                            new PlayerVariableLoader(player, this, getInactivePlayerTypeVariable(varname, player));
                        }
                    }
                }
                broadcastColoredMessage("&aSuccessfully loaded " + message + " '&2" + name + "&a' of the plugin '&2" + plugin.getName() + "&a'.");
            } else if (createid == 2) {
                if (textvariable) {
                    for (TextVariableReceptor var : getTextVariablesNames().keySet()) {
                        new TextVariableLoader(var, this, value);
                    }
                } else {
                    for (Player player : instance.getServer().getOnlinePlayers()) {
                        if (isLoaded(player)) {
                            new PlayerVariableLoader(player, this, value);
                        }
                    }
                }
                broadcastColoredMessage("&aSuccessfully created " + message + " '&2" + name + "&a' of the plugin '&2" + plugin.getName() + "&a'.");
            } else {
                broadcastColoredMessage("&cColumn named '&4" + varname + "&c' for " + message_error + " '&4" + name + "&c' couldn't be created.");
                return;
            }
        } else if (type == TEMPORARY) {
            if (textvariable) {
                for (TextVariableReceptor var : getTextVariablesNames().keySet()) {
                    new TextVariableLoader(var, this, getInactiveTextTypeVariable(varname, var));
                }
            } else {
                for (Player player : instance.getServer().getOnlinePlayers()) {
                    new PlayerVariableLoader(player, this, value);
                }
            }
            broadcastColoredMessage("&aSuccessfully parsed " + message + " '&2" + name + "&a' of the plugin '&2" + plugin.getName() + "&a'.");
        }

        getVariables().put(this, varname);

    }

    public Boolean getVariableTextType() {
        return textvariable;
    }
    public String getVariableName() {
        return varname;
    }
    public Plugin getPlugin() {
        return plugin;
    }
    public Object getValue() {
        return value;
    }
    public String getName() {
        return name;
    }
    public variableType getType() {
        return type;
    }
    public String getSQLDefaultSymbol() {
        return sqltype;
    }

    public static String parseType(VariableCreationController variable) {
        if (variable.getType() == NORMAL) {
            try {
                Integer.parseInt(String.valueOf(variable.getValue()));
                return "INT";
            } catch (IllegalArgumentException ignore) {
                return "TEXT";
            }
        }
        else if (variable.getType() == ARRAY) return "OBJECT";
        else if (variable.getType() == TEMPORARY) return "OBJECT";
        return null;
    }

    public static Integer tryCreateColumn(VariableCreationController variable) {
        if (database_type == SQLITE || database_type == MYSQL) {
            return createColumn(variable);
        }
        return 0;
    }

}
