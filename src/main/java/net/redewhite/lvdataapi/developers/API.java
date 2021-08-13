package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.LvDataPlugin;
import net.redewhite.lvdataapi.events.PlayerLoadEvent;
import net.redewhite.lvdataapi.events.PlayerUnloadEvent;
import net.redewhite.lvdataapi.events.VariableChangeEvent;
import net.redewhite.lvdataapi.variables.ArrayVariable;
import net.redewhite.lvdataapi.database.DatabaseConnection;
import net.redewhite.lvdataapi.variables.PlayerVariable;
import net.redewhite.lvdataapi.variables.TempVariable;
import net.redewhite.lvdataapi.variables.Variable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;

import static net.redewhite.lvdataapi.LvDataPlugin.*;
import static net.redewhite.lvdataapi.LvDataPlugin.variableChangeErrorTypes.*;
import static net.redewhite.lvdataapi.LvDataPlugin.variableType.*;
import static net.redewhite.lvdataapi.database.DatabaseConnection.createStatement;

public class API {

    public static Boolean isLoaded(Player player) {
        for (PlayerVariable api : playerapi.keySet()) {
            if (api.getPlayer() == player) {
                return true;
            }
        }
        return false;
    }

    public static Boolean setVariable(Plugin plugin, Player player, String name, Object value) {

        for (PlayerVariable api : playerapi.keySet()) {
            if (api.getPlayer() == player) {
                if (api.getPlugin() == plugin) {
                    if (api.getName().equals(name)) {
                        if (api.getVariableType() == NORMAL) {

                            Object finale;
                            VariableChangeEvent event = new VariableChangeEvent(plugin, player, name, api.getVariable(), value, getVariable(plugin, player, name));

                            String type = null;
                            for (Variable var : LvDataPlugin.variables.keySet()) {
                                if (api.getVariableName().equals(var.getVariableName())) {
                                    type = var.getType();
                                }
                            } assert type != null;

                            if (value != null) {
                                try {
                                    Integer.parseInt(value.toString());
                                    if (!type.equalsIgnoreCase("INT")) {
                                        event.setError(NOT_INT);
                                        return false;
                                    }
                                } catch (IllegalArgumentException ignore) {
                                    if (!type.equalsIgnoreCase("TEXT")) {
                                        event.setError(NOT_TEXT);
                                        return false;
                                    }
                                }
                                finale = value;
                            } else {
                                finale = "";
                            }
                            instance.getServer().getPluginManager().callEvent(event);
                            if (event.isCancelled()) return false;
                            api.setValue(finale);
                            return true;
                        } else if (api.getVariableType() == ARRAY) {

                            ArrayList<String> array = new ArrayList<>();
                            VariableChangeEvent event = new VariableChangeEvent(plugin, player, name, api.getVariable(), value, getVariable(plugin, player, name));
                            if (value != null) {
                                if (value instanceof ArrayList) {
                                    for (Object str : ((ArrayList<?>) value).toArray()) {
                                        String strFinal = str.toString().replace(",", "<COMMA>");
                                        array.add(strFinal);
                                    }
                                } else {
                                    event.setError(NOT_ARRAY);
                                    return false;
                                }
                            } else {
                                array.add("");
                            }
                            instance.getServer().getPluginManager().callEvent(event);
                            if (event.isCancelled()) return false;

                            api.setValue(array.toString().replace("[", "").replace("]", ""));
                            return true;

                        } else if (api.getVariableType() == TEMPORARY) {
                            VariableChangeEvent event = new VariableChangeEvent(plugin, player, name, api.getVariable(), value, getVariable(plugin, player, name));
                            instance.getServer().getPluginManager().callEvent(event);
                            if (event.isCancelled()) return false;
                            if (value != null) {
                                api.setValue(value);
                            } else api.setValue("");
                            return true;
                        }
                        VariableChangeEvent event = new VariableChangeEvent(plugin, player, name, api.getVariable(), value, getVariable(plugin, player, name));
                        event.setError(UNKNOWN);
                        instance.getServer().getPluginManager().callEvent(event);
                        return false;
                    }
                }
            }
        }
        VariableChangeEvent event = new VariableChangeEvent(plugin, player, name, null, value, null);
        event.setError(UNKNOWN_VARIABLE);
        instance.getServer().getPluginManager().callEvent(event);
        return false;
    }

    public static Boolean addToVariable(Plugin plugin, Player player, String name, Object value) {
        for (PlayerVariable api : playerapi.keySet()) {
            if (api.getPlayer() == player) {
                if (api.getPlugin() == plugin) {
                    if (api.getName().equalsIgnoreCase(name)) {

                        Object v = getVariable(plugin, player, name);
                        assert v != null;
                        assert value != null;

                        if (api.getVariableType() == NORMAL || api.getVariableType() == TEMPORARY) {
                            try {
                                int e = Integer.parseInt(v.toString());
                                int f = Integer.parseInt(value.toString());

                                api.setValue((e + f));
                                return true;
                            } catch (IllegalArgumentException ignore) {
                                return false;
                            }
                        } else {
                            if (value instanceof ArrayList || v instanceof ArrayList) {
                                ArrayList<Object> values = new ArrayList<>();
                                for (Object str : ((ArrayList<?>) v).toArray()) {
                                    String strFinal = str.toString().replace(",", "<COMMA>");
                                    values.add(strFinal);
                                }
                                for (Object str : ((ArrayList<?>) value).toArray()) {
                                    String strFinal = str.toString().replace(",", "<COMMA>");
                                    values.add(strFinal);
                                }

                                api.setValue(values.toString().replace("[", "").replace("]", ""));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Object getVariable(Plugin plugin, Player player, String name) {
        for (PlayerVariable i : playerapi.keySet()) {
            if (i.getPlayer() == player) {
                if (i.getPlugin() == plugin) {
                    if (i.getName().equals(name)) {
                        if (i.getVariableType() == NORMAL) {
                            if (i.getValue().toString().equalsIgnoreCase("")) return null;
                            return i.getValue();
                        } else if (i.getVariableType() == ARRAY) {
                            if (i.getValue().toString().equalsIgnoreCase("")) return new ArrayList<>();

                            ArrayList<String> array = new ArrayList<>();
                            for (String str : i.getValue().toString().split(", ")) {
                                String strFinal = str.replace("<COMMA>", ",");
                                array.add(strFinal);
                            }

                            return array;
                        } else if (i.getVariableType() == TEMPORARY) {
                            return i.getValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void unloadPlayer(Player player) {
        if (isLoaded(player)) {

            PlayerUnloadEvent event = new PlayerUnloadEvent(player);
            String query = "";

            ArrayList<PlayerVariable> array = new ArrayList<>();
            for (PlayerVariable api : playerapi.keySet()) {
                if (api.getVariableType() != TEMPORARY) {
                    if (api.getPlayer() == player) {
                        query = query + api.getVariableName() + " = '" + api.getValue() + "', ";
                        array.add(api);
                    }
                }
            }

            query = "UPDATE `" + tableName + "` SET " + query + "last_update = '" + LvDataPlugin.now + "' WHERE uuid = '" + player.getUniqueId() + "';";
            try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement(query)) {
                pst.execute();
                for (PlayerVariable api : array) playerapi.remove(api);
            } catch (SQLException e) {
                if (LvDataPlugin.debug) e.printStackTrace();
                broadcastColoredMessage("§cSQLite failed when tried save variables of the player '§4" + player.getName() + "§c'.");
                event.setSuccess(false);
            }

            Bukkit.getPluginManager().callEvent(event);
        } else {
            broadcastColoredMessage("§cThe player '§4" + player.getName() + "§c' is not loaded!");
        }
    }

    public static void loadPlayer(Player player) {
        if (!isLoaded(player)) {
            registerPlayer(player);
            Statement statement = createStatement();

            for (TempVariable api : tempvariables.keySet()) {
                new PlayerVariable(player, api.getPlugin(), api.getName(), api.getValue(), api.getVariableType(), api);
            }

            try {
                PlayerLoadEvent event = new PlayerLoadEvent(player);
                Bukkit.getPluginManager().callEvent(event);

                if (!(event.isCancelled()) ) {
                    assert statement != null;
                    ResultSet result = statement.executeQuery("SELECT * FROM `" + tableName + "` WHERE uuid = '" + player.getUniqueId() + "';");
                    ResultSetMetaData e = result.getMetaData();
                    int r = 1;

                    while (result.next()) {
                        if (e.getColumnName(r).contains("_ARRAYLIST_")) {
                            for (ArrayVariable api : arrayvariables.keySet()) {
                                new PlayerVariable(player, api.getPlugin(), api.getName(), result.getObject(api.getVariableName()), api.getVariableType(), api);
                            }
                        } else {
                            for (Variable api : variables.keySet()) {
                                new PlayerVariable(player, api.getPlugin(), api.getName(), result.getObject(api.getVariableName()), api.getVariableType(), api);
                            }
                        }
                        r++;
                    }
                }

            } catch (SQLException e) {
                if (LvDataPlugin.debug) e.printStackTrace();
                broadcastColoredMessage("§cVariables of player '§4" + player.getName() + "§c' couldn't be loaded, aborting...");
            }
        } else {
            broadcastColoredMessage("§cThe player '§4" + player.getName() + "§c' is already loaded!");
        }
    }

    public static void registerPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
            Statement statement = createStatement();
            assert statement != null;

            try (ResultSet result = statement.executeQuery("SELECT * FROM `" + tableName + "` WHERE uuid = '" + player.getUniqueId() + "';")) {
                while (result.next()) return;
            } catch (SQLException e) {
                if (LvDataPlugin.debug) e.printStackTrace();
            }

            try {
                PreparedStatement pstmt;

                if (LvDataPlugin.database_type.equals("MySQL")) {
                    pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO `" + tableName + "` (id, uuid, nickname, last_update) VALUES (DEFAULT, '" + player.getUniqueId() + "', '" + player.getName() + "', '" + LvDataPlugin.now + "');");
                } else {
                    pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO `" + tableName + "` (uuid, nickname, last_update) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', '" + LvDataPlugin.now + "');");
                }

                pstmt.execute();
                broadcastColoredMessage("§aSuccessfully registered player '§2" + player.getName() + "§a'.");
            } catch (SQLException e) {
                if (LvDataPlugin.debug) e.printStackTrace();
                broadcastColoredMessage("§cInternal error when trying to register player '§4" + player.getName() + "§c'. Aborting...");
            }
            loadPlayer(player);
        });
    }

    public static void savePlayer(Player player) {
        if (isLoaded(player)) {
            Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
                String query = "";

                for (PlayerVariable api : playerapi.keySet()) {
                    if (api.getVariableType() != TEMPORARY) {
                        if (api.getPlayer() == player) {
                            query = query + api.getVariableName() + " = '" + api.getValue() + "', ";
                        }
                    }
                }

                query = "UPDATE `" + tableName + "` SET " + query + "last_update = '" + LvDataPlugin.now + "' WHERE uuid = '" + player.getUniqueId() + "';";
                try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement(query)) {
                    pst.execute();
                } catch (SQLException e) {
                    if (LvDataPlugin.debug) e.printStackTrace();
                    broadcastColoredMessage("§cSQLite attempted save player '§4" + player.getName() + "§c' without success.");
                }
            });
        }
    }

}
