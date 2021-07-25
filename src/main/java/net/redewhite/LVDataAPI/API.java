package net.redewhite.LVDataAPI;

import net.redewhite.LVDataAPI.Database.Players;
import net.redewhite.LVDataAPI.Database.SQLiteConnection;
import net.redewhite.LVDataAPI.Database.Variable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import static net.redewhite.LVDataAPI.Database.SQLiteConnection.createStatement;

public class API {

    public static void registerPlayer(Player player) {
        PreparedStatement pstmt;
        Statement statement = createStatement();
        ResultSet result;

        try {
            assert statement != null;
            result = statement.executeQuery("SELECT * FROM `wn_data` WHERE uuid = '" + player.getUniqueId() + "';");
            while (result.next()) { return; }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            pstmt = SQLiteConnection.conn.prepareStatement("INSERT INTO `wn_data` (uuid, nickname, last_update) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', 'now');");
            pstmt.execute();
            pstmt.close();
            Main.broadcastInfo("Successfully registered player '" + player.getName() + "'.");
            loadPlayer(player);
        } catch (SQLException e) {
            e.printStackTrace();
            Main.broadcastWarn("Internal error when trying to register player '" + player.getName() + "'. Aborting...");
        }
    }

    public static Boolean isLoaded(Player player) {
        for (Map.Entry<Players, Player> api : Main.playerapi.entrySet()) {
            if (api.getValue().getPlayer() == player) {
                return true;
            }
        }
        return false;
    }
    
    public static Boolean setVariable(Plugin plugin, Player player, String name, Object value) {
        for (Players api : Main.playerapi.keySet()) {
            if (api.getPlugin() == plugin) {
                if (api.getPlayer() == player) {
                    if (api.getName().equalsIgnoreCase(name)) {

                        String type = null;
                        for (Variable var : Main.dataapi.keySet()) {
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
                        return true;

                    }
                }
            }
        }
        return false;
    }

    public static Object getVariable(Plugin plugin, Player player, String name) {
        for (Players i : Main.playerapi.keySet()) {
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
            PreparedStatement pst;
            String query = "";

            ArrayList<Players> array = new ArrayList<>();
            for (Players api : Main.playerapi.keySet()) {
                if (api.getPlayer() == player) {
                    query = query + api.getVariableName() + " = '" + api.getValue() + "', ";
                    array.add(api);
                }
            }
            for (Players api : array) {
                Main.playerapi.remove(api);
            }

            query = "UPDATE `wn_data` SET " + query + "last_update = 'now' WHERE uuid = '" + player.getUniqueId() + "';";
            try {
                pst = SQLiteConnection.conn.prepareStatement(query);
                pst.execute();
                pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Main.broadcastWarn("SQLite failed when tried save variables of the player '" + player.getName() + "'.");
            }

        } else {
            Main.broadcastWarn("The player '" + player.getName() + "' is not loaded!");
        }
    }

    public static void loadPlayer(Player player) {
        if (!isLoaded(player)) {
            registerPlayer(player);
            Statement statement = createStatement();
            ResultSet result;

            try {
                assert statement != null;
                result = statement.executeQuery("SELECT * FROM `wn_data` WHERE uuid = '" + player.getUniqueId() + "';");
                while (result.next()) {
                    for (Variable api : Main.dataapi.keySet()) {
                        try {
                            new Players(player, Main.getInstance(), api.getName(), result.getObject(api.getVariableName()));
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Main.broadcastWarn("Variables of player '" + player.getName() + "' couldn't be loaded, aborting...");
            }

        } else {
            Main.broadcastWarn("The player '" + player.getName() + "' is already loaded!");
        }
    }

    public static void savePlayer(Player player) {
        if (isLoaded(player)) {
            PreparedStatement pst;
            String query = "";

            for (Players api : Main.playerapi.keySet()) {
                if (api.getPlayer() == player) { query = query + api.getVariableName() + " = '" + api.getValue() + "', "; }
            }

            query = "UPDATE `wn_data` SET " + query + "last_update = 'now' WHERE uuid = '" + player.getUniqueId() + "';";
            try {
                pst = SQLiteConnection.conn.prepareStatement(query);
                pst.execute();
                pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Main.broadcastWarn("SQLite attempted save player '" + player.getName() + "' without success.");
            }

        }
    }

}
