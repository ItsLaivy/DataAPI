package net.redewhite.lvdataapi;

import net.redewhite.lvdataapi.database.PlayerVariable;
import net.redewhite.lvdataapi.database.SQLiteConnection;
import net.redewhite.lvdataapi.database.Variable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import static net.redewhite.lvdataapi.database.SQLiteConnection.createStatement;

public class LvDataPluginAPI {

    public static void registerPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
            Statement statement = createStatement();
            try (ResultSet result = statement.executeQuery("SELECT * FROM `wn_data` WHERE uuid = '" + player.getUniqueId() + "';")) {
                while (result.next()) { return; }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try (PreparedStatement pstmt = SQLiteConnection.conn.prepareStatement("INSERT INTO `wn_data` (uuid, nickname, last_update) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', '" + LvDataPlugin.now + "');")) {
                pstmt.execute();
                LvDataPlugin.broadcastInfo("Successfully registered player '" + player.getName() + "'.");
            } catch (SQLException e) {
                e.printStackTrace();
                LvDataPlugin.broadcastWarn("Internal error when trying to register player '" + player.getName() + "'. Aborting...");
            }
            loadPlayer(player);
        });
    }

    public static Boolean isLoaded(Player player) {
        for (Map.Entry<PlayerVariable, Player> api : LvDataPlugin.playerapi.entrySet()) {
            if (api.getValue().getPlayer() == player) {
                return true;
            }
        }
        return false;
    }

    public static Boolean setVariable(Plugin plugin, Player player, String name, Object value) {
        for (PlayerVariable api : LvDataPlugin.playerapi.keySet()) {
            if (api.getPlugin() == plugin) {
                if (api.getPlayer() == player) {
                    if (api.getName().equalsIgnoreCase(name)) {

                        String type = null;
                        for (Variable var : LvDataPlugin.dataapi.keySet()) {
                            if (api.getVariableName().equals(var.getVariableName())) {
                                type = var.getType();
                            }
                        } assert type != null;

                        try {
                            Integer.parseInt(name);
                            if (!type.equalsIgnoreCase("INT")) {
                                return false;
                            }
                        } catch (IllegalArgumentException ignore) {
                            if (!type.equalsIgnoreCase("TEXT")) {
                                return false;
                            }
                        }

                        api.setValue(value);
                        Bukkit.broadcastMessage("uhu");
                        return true;

                    }
                }
            }
        }
        return false;
    }

    public static Object getVariable(Plugin plugin, Player player, String name) {
        for (PlayerVariable i : LvDataPlugin.playerapi.keySet()) {
            if (i.getPlugin() == plugin) {
                if (i.getPlayer() == player) {
                    if (i.getName().equalsIgnoreCase(name)) {
                        return i.getValue();
                    }
                }
            }
        }
        return null;
    }

    public static void unloadPlayer(Player player) {
        if (isLoaded(player)) {
            String query = "";

            ArrayList<PlayerVariable> array = new ArrayList<>();
            for (PlayerVariable api : LvDataPlugin.playerapi.keySet()) {
                if (api.getPlayer() == player) {
                    query = query + api.getVariableName() + " = '" + api.getValue() + "', ";
                    array.add(api);
                }
            }
            for (PlayerVariable api : array) {
                LvDataPlugin.playerapi.remove(api);
            }

            query = "UPDATE `wn_data` SET " + query + "last_update = '" + LvDataPlugin.now + "' WHERE uuid = '" + player.getUniqueId() + "';";
            try (PreparedStatement pst = SQLiteConnection.conn.prepareStatement(query)) {
                pst.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                LvDataPlugin.broadcastWarn("SQLite failed when tried save variables of the player '" + player.getName() + "'.");
            }
        } else {
            LvDataPlugin.broadcastWarn("The player '" + player.getName() + "' is not loaded!");
        }
    }

    public static void loadPlayer(Player player) {
        if (!isLoaded(player)) {
            registerPlayer(player);
            Statement statement = createStatement();

            try (ResultSet result = statement.executeQuery("SELECT * FROM `wn_data` WHERE uuid = '" + player.getUniqueId() + "';")) {
                while (result.next()) {
                    for (Variable api : LvDataPlugin.dataapi.keySet()) {
                        try {
                            new PlayerVariable(player, LvDataPlugin.getInstance(), api.getName(), result.getObject(api.getVariableName()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                LvDataPlugin.broadcastWarn("Variables of player '" + player.getName() + "' couldn't be loaded, aborting...");
            }

        } else {
            LvDataPlugin.broadcastWarn("The player '" + player.getName() + "' is already loaded!");
        }
    }

    public static void savePlayer(Player player) {
        if (isLoaded(player)) {
            Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
                String query = "";

                for (PlayerVariable api : LvDataPlugin.playerapi.keySet()) {
                    if (api.getPlayer() == player) { query = query + api.getVariableName() + " = '" + api.getValue() + "', "; }
                }

                query = "UPDATE `wn_data` SET " + query + "last_update = '" + LvDataPlugin.now + "' WHERE uuid = '" + player.getUniqueId() + "';";
                try (PreparedStatement pst = SQLiteConnection.conn.prepareStatement(query)) {
                    pst.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                    LvDataPlugin.broadcastWarn("SQLite attempted save player '" + player.getName() + "' without success.");
                }
            });
        }
    }

}
