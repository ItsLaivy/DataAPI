package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.variables.receptors.TextVariableReceptor;
import net.redewhite.lvdataapi.variables.loaders.InactivePlayerLoader;
import net.redewhite.lvdataapi.variables.loaders.PlayerVariableLoader;
import net.redewhite.lvdataapi.variables.loaders.InactiveTextLoader;
import net.redewhite.lvdataapi.variables.loaders.TextVariableLoader;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;

import static net.redewhite.lvdataapi.developers.PlayerVariablesAPI.isLoaded;
import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.*;
import static net.redewhite.lvdataapi.database.DatabaseConnection.*;
import static net.redewhite.lvdataapi.developers.GeneralAPI.*;
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
        } else {
            ArrayList<String> uses = new ArrayList<>();
            boolean success = false;
            for (String f : databasetype.replace(" ", "").split("!>")) {
                if (!success) {
                    if (uses.contains(f)) {
                        Bukkit.getPluginManager().disablePlugin(instance);
                        throw new IllegalArgumentException("You can't execute a database type two times! if then fail, the connection using that (failed) database cannot be estabilished again.");
                    } uses.add(f);
                    if (f.equalsIgnoreCase("MYSQL")) {
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

    public static void deleteTextVariables(TextVariableReceptor textVariable) {
        ArrayList<TextVariableLoader> variables = getTextTypeVariablesArrayList(textVariable);
        ArrayList<InactiveTextLoader> arraytwo = new ArrayList<>();

        for (TextVariableLoader var : variables) getTextVariables().remove(var);
        for (InactiveTextLoader var : getInactiveTextVariables().keySet()) {
            if (var.getOwner() == textVariable) arraytwo.add(var);
        }
        for (InactiveTextLoader var : arraytwo) getInactiveTextVariables().remove(var);
    }
    public static void deletePlayerVariables(Player player) {
        ArrayList<PlayerVariableLoader> variables = getPlayerTypeVariablesArrayList(player);
        ArrayList<InactivePlayerLoader> arraytwo = new ArrayList<>();

        for (PlayerVariableLoader var : variables) getPlayerVariables().remove(var);
        for (InactivePlayerLoader var : getInactivePlayerVariables().keySet()) {
            if (var.getOwner() == player) arraytwo.add(var);
        }
        for (InactivePlayerLoader var : arraytwo) getInactivePlayerVariables().remove(var);
    }

    public static Boolean databaseSaveText(TextVariableReceptor textVariable) {
        if (database_type == SQLITE || database_type == MYSQL) {
            return executeQuery(getTextSaverQuery(textVariable, getTextTypeVariablesArrayList(textVariable)));
        }
        return false;
    }
    public static Boolean databaseSavePlayer(Player player) {
        if (database_type == SQLITE || database_type == MYSQL) {
            return executeQuery(getPlayerSaverQuery(player, getPlayerTypeVariablesArrayList(player)));
        }
        return false;
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

    public static Boolean tryRegisterTextVariableInDatabase(TextVariableReceptor textVariable) {
        if (getTextDatabaseVariablesAmount(textVariable) != null) return null;

        boolean success = true;

        if (database_type == MYSQL || database_type == SQLITE) {
            success = insertDefaultTextValues(textVariable);
        }

        return success;
    }
    public static Boolean tryRegisterPlayerInDatabase(Player player) {
        if (getPlayerDatabaseVariablesAmount(player) != null) return null;
        boolean success = true;

        if (database_type == MYSQL || database_type == SQLITE) {
            success = insertDefaultPlayerValues(player);
        }

        return success;
    }

    public static Boolean unloadDatabaseTextVariables(TextVariableReceptor textVariable) {
        if (!databaseSaveText(textVariable)) {
            broadcastColoredMessage("§cDatabase attempted unload text variable '§4" + textVariable.getName() + "§c' without success.");
            if (config.getBoolean("Delete variables if unload fail")) {
                deleteTextVariables(textVariable);
                return false;
            }
        }

        deleteTextVariables(textVariable);
        return true;
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

    public static Boolean loadDatabaseTextVariables(TextVariableReceptor textVariable) {
        tryRegisterTextVariableInDatabase(textVariable);
        if (database_type == SQLITE || database_type == MYSQL) {
            Statement statement = createStatement();
            assert statement != null;

            try (ResultSet result = statement.executeQuery("SELECT * FROM '" + tableNameText + "' WHERE name = '" + textVariable.getVariableName() + "';")) {
                while (result.next()) {
                    ResultSetMetaData rmtd = result.getMetaData();
                    for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                        if (row > 3) {
                            String e = getVariableUnhashedValue(result.getObject(row)).toString();
                            if (getVariableUnhashedValue(result.getObject(row)).toString().equals("null")) e = null;
                            new InactiveTextLoader(rmtd.getColumnName(row), e, textVariable);
                        }
                    }
                }
                return true;
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
                return false;
            }
        }
        return false;
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
                            new InactivePlayerLoader(rmtd.getColumnName(row), e, player);
                        }
                    }
                }
                return true;
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
                return false;
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

                    if (number == 0) return null;
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

                    if (number == 0) return null;
                    return number;
                }
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
            }
        }
        return null;
    }

}
