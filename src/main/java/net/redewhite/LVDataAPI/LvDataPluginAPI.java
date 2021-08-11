package net.redewhite.lvdataapi;

import net.redewhite.lvdataapi.database.ArrayVariable;
import net.redewhite.lvdataapi.database.DatabaseConnection;
import net.redewhite.lvdataapi.database.PlayerVariable;
import net.redewhite.lvdataapi.database.Variable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import static net.redewhite.lvdataapi.LvDataPlugin.broadcastColoredMessage;
import static net.redewhite.lvdataapi.LvDataPlugin.playerapi;
import static net.redewhite.lvdataapi.database.DatabaseConnection.createStatement;

public class LvDataPluginAPI {

    public static void registerPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
            Statement statement = createStatement();
            assert statement != null;

            try (ResultSet result = statement.executeQuery("SELECT * FROM `wn_data` WHERE uuid = '" + player.getUniqueId() + "';")) {
                while (result.next()) { return; }
            } catch (SQLException e) {
                if (LvDataPlugin.debug) e.printStackTrace();
            }

            try {
                PreparedStatement pstmt;

                if (LvDataPlugin.database_type.equals("MySQL")) {
                    pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO `wn_data` (id, uuid, nickname, last_update) VALUES (DEFAULT, '" + player.getUniqueId() + "', '" + player.getName() + "', '" + LvDataPlugin.now + "');");
                } else {
                    pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO `wn_data` (uuid, nickname, last_update) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', '" + LvDataPlugin.now + "');");
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
            if (api.getPlugin() == plugin) {
                if (api.getPlayer() == player) {
                    if (api.getName().equalsIgnoreCase(name)) {

                        if (api.getVariableName().contains("_ARRAYLIST_")) {
                            return false;
                        }

                        String type = null;
                        for (Variable var : LvDataPlugin.variables.keySet()) {
                            if (api.getVariableName().equals(var.getVariableName())) {
                                type = var.getType();
                            }
                        } assert type != null;

                        try {
                            Integer.parseInt(value.toString());
                            if (!type.equalsIgnoreCase("INT")) {
                                return false;
                            }
                        } catch (IllegalArgumentException ignore) {
                            if (!type.equalsIgnoreCase("TEXT")) {
                                return false;
                            }
                        }

                        api.setValue(value);
                        return true;

                    }
                }
            }
        }
        return false;
    }

    public static Boolean setArrayVariable(Plugin plugin, Player player, String name, ArrayList value) {
        for (PlayerVariable api : playerapi.keySet()) {
            if (api.getPlugin() == plugin) {
                if (api.getPlayer() == player) {
                    if (api.getName().equalsIgnoreCase(name)) {

                        if (!(api.getVariableName().contains("_ARRAYLIST_"))) {
                            return false;
                        }

                        api.setValue(value.toString());
                        return true;

                    }
                }
            }
        }
        return false;
    }

    public static Object getVariable(Plugin plugin, Player player, String name) {
        for (PlayerVariable i : playerapi.keySet()) {
            if (i.getPlugin() == plugin) {
                if (i.getPlayer() == player) {
                    if (i.getName().equalsIgnoreCase(ChatColor.AQUA + name)) {
                        return i.getValue();
                    }
                }
            }
        }
        return null;
    }
    public static ArrayList<String> getArrayVariable(Plugin plugin, Player player, String name) {
        for (PlayerVariable i : playerapi.keySet()) {
            if (i.getPlugin() == plugin) {
                if (i.getPlayer() == player) {
                    if (i.getName().equalsIgnoreCase(name)) {
                        if (i.getValue().toString().equalsIgnoreCase("[]")) return new ArrayList<>();
                        return new ArrayList<>(Arrays.asList(i.getValue().toString().replace("[", "").replace("]", "").split(", ")));
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public static void unloadPlayer(Player player) {
        if (isLoaded(player)) {
            String query = "";

            ArrayList<PlayerVariable> array = new ArrayList<>();
            for (PlayerVariable api : playerapi.keySet()) {
                if (api.getPlayer() == player) {
                    query = query + api.getVariableName() + " = '" + api.getValue() + "', ";
                    array.add(api);
                }
            }
            for (PlayerVariable api : array) {
                playerapi.remove(api);
            }


            query = "UPDATE `wn_data` SET " + query + "last_update = '" + LvDataPlugin.now + "' WHERE uuid = '" + player.getUniqueId() + "';";
            try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement(query)) {
                pst.execute();
            } catch (SQLException e) {
                if (LvDataPlugin.debug) e.printStackTrace();
                broadcastColoredMessage("§cSQLite failed when tried save variables of the player '§4" + player.getName() + "§c'.");
            }
        } else {
            broadcastColoredMessage("§cThe player '§4" + player.getName() + "§c' is not loaded!");
        }
    }

    public static void loadPlayer(Player player) {
        if (!isLoaded(player)) {
            registerPlayer(player);
            Statement statement = createStatement();

            try {
                assert statement != null;
                ResultSet result = statement.executeQuery("SELECT * FROM `wn_data` WHERE uuid = '" + player.getUniqueId() + "';");
                while (result.next()) {
                    for (Variable api : LvDataPlugin.variables.keySet()) {
                        new PlayerVariable(player, api.getPlugin(), api.getName(), result.getObject(api.getVariableName()), "VARIABLE");
                    }
                    for (ArrayVariable api : LvDataPlugin.arrayvariables.keySet()) {
                        new PlayerVariable(player, api.getPlugin(), api.getName(), result.getObject(api.getVariableName()), "ARRAY VARIABLE");
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

    public static void savePlayer(Player player) {
        if (isLoaded(player)) {
            Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
                String query = "";

                for (PlayerVariable api : playerapi.keySet()) {
                    if (api.getPlayer() == player) {
                        query = query + api.getVariableName() + " = '" + api.getValue() + "', ";
                    }
                }

                query = "UPDATE `wn_data` SET " + query + "last_update = '" + LvDataPlugin.now + "' WHERE uuid = '" + player.getUniqueId() + "';";
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
