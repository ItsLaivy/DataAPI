package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.variables.receptors.TextVariableReceptor;
import net.redewhite.lvdataapi.variables.loaders.InactivePlayerLoader;
import net.redewhite.lvdataapi.variables.loaders.PlayerVariableLoader;
import net.redewhite.lvdataapi.variables.loaders.InactiveTextLoader;
import net.redewhite.lvdataapi.variables.loaders.TextVariableLoader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.UUID;

import static net.redewhite.lvdataapi.database.DatabaseConnection.createStatement;
import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.SQLITE;
import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.MYSQL;
import static net.redewhite.lvdataapi.LvDataAPI.variableType.*;
import static net.redewhite.lvdataapi.developers.DatabaseAPI.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

@SuppressWarnings("unused")
public class GeneralAPI {

    public static void stopPlugin() {
        if (config.getBoolean("close-server")) {
            instance.getServer().shutdown();
        } else {
            instance.getPluginLoader().disablePlugin(instance);
        }
    }

    public static ArrayList<String> getTextTypeArrayVariable(Plugin plugin, String name, String textVariableName) {
        TextVariableLoader var = getTextTypeVariableLoader(plugin, name, textVariableName);

        if (var != null) {
            ArrayList<String> array = new ArrayList<>();
            if (var.getVariable().getType() == ARRAY) {
                String[] split = var.getValue().toString().split("<SPLIT!>");
                for (String e : split) {
                    if (!e.equals("")) {
                        array.add(getVariableUnhashedValue(e).toString());
                    }
                }
            }
            return array;
        }
        throw new NullPointerException("That array variable doesn't exists or is not an array variable type!");
    }
    public static ArrayList<String> getPlayerTypeArrayVariable(Plugin plugin, String name, Player player) {
        PlayerVariableLoader var = getPlayerTypeVariableLoader(plugin, name, player);

        if (var != null) {
            ArrayList<String> array = new ArrayList<>();
            if (var.getVariable().getType() == ARRAY) {
                String[] split = var.getValue().toString().split("<SPLIT!>");
                for (String e : split) {
                    if (!e.equals("")) {
                        array.add(getVariableUnhashedValue(e).toString());
                    }
                }
            }
            return array;
        }
        throw new NullPointerException("That array variable doesn't exists or is not an array variable type!");
    }

    public static Boolean isTextTypeVariableNull(Plugin plugin, String name, String textVariableName) {
        TextVariableLoader var = getTextTypeVariableLoader(plugin, name, textVariableName);
        if (var != null) return var.getValue().equals("");
        return null;
    }
    public static Boolean isPlayerTypeVariableNull(Plugin plugin, String name, Player player) {
        PlayerVariableLoader var = getPlayerTypeVariableLoader(plugin, name, player);
        if (var != null) return var.getValue().equals("");
        return null;
    }

    public static Object getPlayerTypeVariableValue(Plugin plugin, String name, Player player) {
        PlayerVariableLoader var = getPlayerTypeVariableLoader(plugin, name, player);

        if (var != null) {
            return getVariableUnhashedValue(var.getValue());
        } else {
            if (config.getBoolean("Null variables if blank")) {
                return null;
            } else {
                return "";
            }
        }
    }
    public static Object getTextTypeVariableValue(Plugin plugin, String name, String textVariableName) {
        TextVariableLoader var = getTextTypeVariableLoader(plugin, name, textVariableName);

        if (var != null) {
            return getVariableUnhashedValue(var.getValue());
        } else {
            if (config.getBoolean("Null variables if blank")) {
                return null;
            } else {
                return "";
            }
        }
    }

    public static Boolean setTextTypeVariableValue(Plugin plugin, String name, String textVariableName, Object value) {
        TextVariableLoader var = getTextTypeVariableLoader(plugin, name, textVariableName);
        if (var != null) {
            if (var.getVariable().getType() == NORMAL) {
                if (database_type == SQLITE || database_type == MYSQL) {
                    try {
                        Integer.parseInt(value.toString());
                    } catch (IllegalArgumentException ignore) {
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
        }
        return false;
    }

    public static Boolean setPlayerTypeVariableValue(Plugin plugin, String name, Player player, Object value) {
        PlayerVariableLoader var = getPlayerTypeVariableLoader(plugin, name, player);
        if (var != null) {
            if (var.getVariable().getType() == NORMAL) {
                if (database_type == SQLITE || database_type == MYSQL) {
                    try {
                        Integer.parseInt(value.toString());
                    } catch (IllegalArgumentException ignore) {
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
        }
        return false;
    }

    public static PlayerVariableLoader getPlayerTypeVariableLoader(Plugin plugin, String name, Player player) {
        for (PlayerVariableLoader var : getPlayerVariables().keySet()) {
            if (var.getPlayer() == player) {
                if (var.getVariable().getPlugin() == plugin) {
                    if (var.getVariable().getName().equals(name)) {
                        return var;
                    }
                }
            }
        }
        return null;
    }

    public static TextVariableLoader getTextTypeVariableLoader(Plugin plugin, String name, String textVariableName) {
        for (TextVariableLoader var : getTextVariables().keySet()) {
            if (var.getVariable().getPlugin() == plugin) {
                if (var.getTextVariable().getName().equals(textVariableName)) {
                    if (var.getVariable().getName().equals(name)) {
                        Bukkit.broadcastMessage("d");
                        return var;
                    } else {
                        Bukkit.broadcastMessage("12: " + var.getVariable().getName() + " -=- " + name);
                    }
                } else {
                    Bukkit.broadcastMessage("1314: " + var.getTextVariable().getName() + " -=- " + textVariableName);
                }
            } else {
                Bukkit.broadcastMessage("Plugin: " + var.getVariable().getPlugin() + " -=- " + plugin);
            }
        }
        return null;
    }

    public static TextVariableReceptor getTextTypeVariable(Plugin plugin, String name) {
        for (TextVariableReceptor var : getTextVariablesNames().keySet()) {
            if (var.getPlugin() == plugin) {
                if (var.getName().equals(name)) {
                    return var;
                }
            }
        }
        return null;
    }

    public static Object getPlayerTypeInactiveVariable(String variable_brute_name, Player player) {
        for (InactivePlayerLoader var : getInactivePlayerVariables().keySet()) {
            if (var.getName().equals(variable_brute_name)) {
                if (var.getOwner() == player) {
                    if (var.getValue().equals("")) return null;
                    return var.getValue();
                }
            }
        }
        return null;
    }
    public static Object getTextTypeInactiveVariable(String variable_brute_name, TextVariableReceptor textVariable) {
        for (InactiveTextLoader var : getInactiveTextVariables().keySet()) {
            if (var.getName().equals(variable_brute_name)) {
                if (var.getOwner() == textVariable) {
                    if (var.getValue().equals("")) return null;
                    return var.getValue();
                }
            }
        }
        return null;
    }

    public static Object getInactivePlayerTypeVariable(String inactive_variable_name, Player player) {
        for (InactivePlayerLoader var : getPlayerTypeInactiveVariables(player)) {
            if (var.getName().equals(inactive_variable_name)) {
                if (var.getOwner() == player) {
                    if (var.getValue().equals("")) return null;
                    return var.getValue();
                }
            }
        }
        return null;
    }
    public static Object getInactiveTextTypeVariable(String inactive_variable_name, TextVariableReceptor textVariable) {
        for (InactiveTextLoader var : getTextTypeInactiveVariables(inactive_variable_name)) {
            if (var.getName().equals(inactive_variable_name)) {
                if (var.getOwner() == textVariable) {
                    if (var.getValue().equals("")) return null;
                    return var.getValue();
                }
            }
        }
        return null;
    }

    public static ArrayList<InactiveTextLoader> getTextTypeInactiveVariables(String owner) {
        ArrayList<InactiveTextLoader> array = new ArrayList<>();
        for (InactiveTextLoader var : getInactiveTextVariables().keySet()) {
            if (var.getOwner().getName().equals(owner)) array.add(var);
        } return array;
    }
    public static ArrayList<InactivePlayerLoader> getPlayerTypeInactiveVariables(Player owner) {
        ArrayList<InactivePlayerLoader> array = new ArrayList<>();
        for (InactivePlayerLoader var : getInactivePlayerVariables().keySet()) {
            if (var.getOwner() == owner) array.add(var);
        } return array;
    }

    public static ArrayList<TextVariableLoader> getTextTypeVariablesArrayList(TextVariableReceptor textVariable) {
        ArrayList<TextVariableLoader> variables = new ArrayList<>();
        for (TextVariableLoader var : getTextVariables().keySet()) {
            if (var.getTextVariable() == textVariable) {
                if (var.getVariable().getType() != TEMPORARY) {
                    variables.add(var);
                }
            }
        }
        return variables;
    }
    public static ArrayList<PlayerVariableLoader> getPlayerTypeVariablesArrayList(Player player) {
        ArrayList<PlayerVariableLoader> variables = new ArrayList<>();
        for (PlayerVariableLoader var : getPlayerVariables().keySet()) {
            if (var.getPlayer() == player) {
                if (var.getVariable().getType() != TEMPORARY) {
                    variables.add(var);
                }
            }
        }
        return variables;
    }

    public static Boolean isTextTypeReceptorRegistered(Plugin plugin, String name) {
        return isTextTypeReceptorRegistered(plugin.getName() + "_TEXT_" + name);
    }
    public static Boolean isTextTypeReceptorRegistered(String textVariableBruteName) {
        if (database_type == SQLITE || database_type == MYSQL) {
            Statement statement = createStatement();
            assert statement != null;

            try (ResultSet result = statement.executeQuery("SELECT * FROM '" + tableNameText + "' WHERE name = '" + textVariableBruteName + "';")) {
                if (result.next()) {
                    return true;
                }
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
            }
        }
        return false;
    }

    public static Boolean isPlayerTypeMemberRegistered(UUID playerUniqueId) {
        if (database_type == SQLITE || database_type == MYSQL) {
            Statement statement = createStatement();
            assert statement != null;

            try (ResultSet result = statement.executeQuery("SELECT * FROM '" + tableNamePlayers + "' WHERE uuid = '" + playerUniqueId.toString() + "';")) {
                if (result.next()) {
                    return true;
                }
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
            }
        }
        return false;
    }

    public static Integer getTextDatabaseVariablesAmount(TextVariableReceptor textVariable) {
        if (database_type == SQLITE || database_type == MYSQL) {
            Statement statement = createStatement();
            assert statement != null;

            try (ResultSet result = statement.executeQuery("SELECT * FROM '" + tableNameText + "' WHERE name = '" + textVariable.getVariableName() + "';")) {
                if (result.next()) {
                    ResultSetMetaData rmtd = result.getMetaData();
                    int number = 0;

                    for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                        if (row > 3) number++;
                    }

                    return number;
                }
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
            }
        }
        return null;
    }
    public static Integer getPlayerDatabaseVariablesAmount(Player player) {
        if (database_type == SQLITE || database_type == MYSQL) {
            Statement statement = createStatement();
            assert statement != null;

            try (ResultSet result = statement.executeQuery("SELECT * FROM '" + tableNamePlayers + "' WHERE uuid = '" + player.getUniqueId() + "';")) {
                if (result.next()) {
                    ResultSetMetaData rmtd = result.getMetaData();
                    int number = 0;

                    for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                        if (row > 4) number++;
                    }

                    return number;
                }
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
            }
        }
        return null;
    }
}
