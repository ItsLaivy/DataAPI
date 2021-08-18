package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.variables.loaders.InactivePlayerVariable;
import net.redewhite.lvdataapi.variables.loaders.PlayerVariable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.File;

import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.*;
import static net.redewhite.lvdataapi.LvDataAPI.variableType.NORMAL;
import static net.redewhite.lvdataapi.database.DatabaseConnection.*;
import static net.redewhite.lvdataapi.LvDataAPI.variableType.ARRAY;
import static net.redewhite.lvdataapi.developers.API.stopPlugin;
import static net.redewhite.lvdataapi.developers.API.isLoaded;
import static net.redewhite.lvdataapi.utils.YamlDatabaseAPI.*;
import static net.redewhite.lvdataapi.utils.SQLDatabaseAPI.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

@SuppressWarnings("UnusedReturnValue")
public class DatabaseAPI {

    public static void connection() {
        String databasetype = config.getString("Database Type");
        if (databasetype.equalsIgnoreCase("MYSQL")) {
            if (!connect(MYSQL)) stopPlugin();
        } else if (databasetype.equalsIgnoreCase("SQLITE")) {
            if (!connect(SQLITE)) stopPlugin();
        } else if (databasetype.equalsIgnoreCase("YAML")) {
            if (!connect(YAML)) stopPlugin();
        } else {
            ArrayList<String> uses = new ArrayList<>();
            boolean success = false;
            for (String f : databasetype.replace(" ", "").split("!>")) {
                if (!success) {
                    if (uses.contains(f)) {
                        Bukkit.getPluginManager().disablePlugin(instance);
                        throw new IllegalArgumentException("You can't execute a database type two times! if then fail, the connection using that (failed) database cannot be estabilished again.");
                    } uses.add(f);
                    if (f.equalsIgnoreCase("YAML")) {
                        if (connect(YAML)) success = true;
                    } else if (f.equalsIgnoreCase("MYSQL")) {
                        if (connect(MYSQL)) success = true;
                    } else if (f.equalsIgnoreCase("SQLITE")) {
                        if (connect(SQLITE)) success = true;
                    } else {
                        broadcastColoredMessage("§cWrong config.yml configuration at '§4Database Type§c'. Stopping plugin...");
                        stopPlugin();
                        return;
                    }
                }
            }
            if (!success) {
                broadcastColoredMessage("§cA database connection could not be established, closing the plugin...");
                stopPlugin();
            }
        }
    }

    public static void deletePlayerVariables(Player player) {
        ArrayList<PlayerVariable> variables = getPlayerVariablesArrayList(player);
        ArrayList<InactivePlayerVariable> arraytwo = new ArrayList<>();

        for (PlayerVariable var : variables) getPlayers().remove(var);
        for (InactivePlayerVariable var : getInactiveVariables().keySet()) {
            if (var.getOwner() == player) arraytwo.add(var);
        }
        for (InactivePlayerVariable var : arraytwo) getInactiveVariables().remove(var);
    }

    public static ArrayList<PlayerVariable> getPlayerVariablesArrayList(Player player) {
        ArrayList<PlayerVariable> variables = new ArrayList<>();
        for (PlayerVariable var : getPlayers().keySet()) {
            if (var.getPlayer() == player) {
                if (var.getVariable().getType() == NORMAL || var.getVariable().getType() == ARRAY) {
                    variables.add(var);
                }
            }
        }
        return variables;
    }

    public static Boolean databaseSavePlayer(Player player) {
        if (database_type == SQLITE || database_type == MYSQL) {
            return executeQuery(getSaverQuery(player, getPlayerVariablesArrayList(player)));
        } else if (database_type == YAML) {
            return savePlayerVariables(player);
        }
        return false;
    }

    public static PlayerVariable getPlayerVariable(Plugin plugin, Player player, String name) {
        for (PlayerVariable var : getPlayers().keySet()) {
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

    public static ArrayList<InactivePlayerVariable> getPlayerInactiveVariables(Player player) {
        ArrayList<InactivePlayerVariable> array = new ArrayList<>();
        for (InactivePlayerVariable var : getInactiveVariables().keySet()) {
            if (var.getOwner() == player) array.add(var);
        } return array;
    }

    public static Object getVariableHashedValue(Object value) {
        if (value != null) {
            return value.toString().replace(",", "<!COMMA>").replace("[", "<!RIGHTBRACKET>").replace("]", "<!LEFTBRACKET>").replace("'", "<!SIMPLECOMMA>");
        }
        return "";
    }
    public static Object getVariableUnhashedValue(Object value) {
        if (value != null) {
            return value.toString().replace("<!COMMA>", ",").replace("<!RIGHTBRACKET>", "[").replace("<!LEFTBRACKET>", "]").replace("<!SIMPLECOMMA>", "'");
        }
        return "";
    }

    public static Boolean tryRegisterPlayerInDatabase(Player player) {
        if (getPlayerDatabaseVariablesAmount(player) != null) return null;
        boolean success = true;

        if (database_type == MYSQL || database_type == SQLITE) {
            success = insertDefaultValues(player);
        } else if (database_type == YAML) {
            if (getPlayerFile(player) == null) success = false;
        }

        return success;
    }

    public static Boolean unloadDatabasePlayerVariables(Player player) {
        if (!isLoaded(player)) return false;

        if (!databaseSavePlayer(player)) {
            broadcastColoredMessage("§cDatabase attempted unload player '§4" + player.getName() + "§c' without success.");
            if (config.getBoolean("Delete variables if unload fail")) {
                deletePlayerVariables(player);
                return false;
            }
        }

        deletePlayerVariables(player);
        return true;
    }

    public static Boolean loadDatabasePlayerVariables(Player player) {
        if (isLoaded(player)) return false;

        tryRegisterPlayerInDatabase(player);
        if (database_type == SQLITE || database_type == MYSQL) {
            Statement statement = createStatement();
            assert statement != null;

            try (ResultSet result = statement.executeQuery("SELECT * FROM '" + tableNamePlayers + "' WHERE uuid = '" + player.getUniqueId() + "';")) {
                while (result.next()) {
                    ResultSetMetaData rmtd = result.getMetaData();
                    for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                        if (row > 4) {
                            String e = getVariableUnhashedValue(result.getObject(row)).toString();
                            if (getVariableUnhashedValue(result.getObject(row)).toString().equals("null")) e = null;
                            new InactivePlayerVariable(rmtd.getColumnName(row), e, player);
                        }
                    }
                }
                return true;
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
                return false;
            }
        } else if (database_type == YAML) {
            loadPlayerVariables(player);
            return true;
        }
        return false;
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

                    if (number == 0) return null;
                    return number;
                }
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
            }
        } else if (database_type == YAML) {
            File file = getPlayerFile(player);
            assert file != null;

            YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
            if (configFile.getConfigurationSection(player.getUniqueId().toString() + ".variables") != null) {

                int number = 0;
                for (Object ignore : configFile.getConfigurationSection(player.getUniqueId().toString() + ".variables").getKeys(false)) {
                    number++;
                }

                if (number == 0) return null;
                return number;
            } else {
                return 0;
            }
        }
        return null;
    }

}
