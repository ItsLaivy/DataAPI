package net.redewhite.lvdataapi.utils;

import net.redewhite.lvdataapi.variables.loaders.PlayerVariableLoader;
import net.redewhite.lvdataapi.variables.loaders.TextVariableLoader;
import net.redewhite.lvdataapi.database.DatabaseConnection;
import net.redewhite.lvdataapi.variables.receptors.TextVariableReceptor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.redewhite.lvdataapi.developers.DatabaseAPI.getVariableHashedValue;
import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

public class SQLDatabaseAPI {

    public static Boolean insertDefaultTextValues(TextVariableReceptor textVariable) {
        try {
            PreparedStatement pstmt = null;
            if (database_type == MYSQL) pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO '" + tableNameText + "' (id, name, last_update) VALUES (DEFAULT, '" + textVariable.getVariableName() + "', '" + now + "');");
            else if (database_type == SQLITE) pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO '" + tableNameText + "' (name, last_update) VALUES ('" + textVariable.getVariableName() + "', '" + now + "');");
            assert pstmt != null;

            pstmt.execute();
            broadcastColoredMessage("§aSuccessfully registered text variable '§2" + textVariable.getName() + "§a'.");
            return true;
        } catch (SQLException e) {
            if (debug) e.printStackTrace();
            broadcastColoredMessage("§cInternal error when trying to register text variable '§4" + textVariable.getName() + "§c'. Aborting...");
            return false;
        }
    }
    public static Boolean insertDefaultPlayerValues(Player player) {
        try {
            PreparedStatement pstmt = null;
            if (database_type == MYSQL) pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO '" + tableNamePlayers + "' (id, uuid, nickname, last_update) VALUES (DEFAULT, '" + player.getUniqueId() + "', '" + player.getName() + "', '" + now + "');");
            else if (database_type == SQLITE) pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO '" + tableNamePlayers + "' (uuid, nickname, last_update) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', '" + now + "');");
            assert pstmt != null;

            pstmt.execute();
            broadcastColoredMessage("§aSuccessfully registered player '§2" + player.getName() + "§a'.");
            return true;
        } catch (SQLException e) {
            if (debug) e.printStackTrace();
            broadcastColoredMessage("§cInternal error when trying to register player '§4" + player.getName() + "§c'. Aborting...");
            return false;
        }
    }

    public static Integer createColumn(VariableCreationController variable) {
        String table;
        if (variable.getVariableTextType()) {
            table = tableNameText;
        } else {
            table = tableNamePlayers;
        }

        try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement("ALTER TABLE '" + table + "' ADD COLUMN " + variable.getVariableName() + " " + variable.getSQLDefaultSymbol() + " DEFAULT '" + variable.getValue() + "';")) {
            pst.execute();
        } catch (SQLException e) {
            if (!e.getMessage().contains("uplicate column name")) {
                if (debug) e.printStackTrace();
                return 0;
            } else return 1;
        } catch (NullPointerException e) {
            return 0;
        }
        return 2;
    }

    public static Boolean executeQuery(String query) {
        try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement(query)) {
            pst.execute();
            return true;
        } catch (SQLException e) {
            if (debug) e.printStackTrace();
            return false;
        }
    }

    public static String getTextSaverQuery(TextVariableReceptor textVariable, ArrayList<TextVariableLoader> variables) {
        StringBuilder query = new StringBuilder();
        for (TextVariableLoader var : variables) {
            query.append(var.getVariable().getVariableName()).append(" = '").append(getVariableHashedValue(var.getValue())).append("', ");
        }
        return "UPDATE '" + tableNameText + "' SET " + query + "last_update = '" + now + "' WHERE name = '" + textVariable.getVariableName() + "';";
    }
    public static String getPlayerSaverQuery(Player player, ArrayList<PlayerVariableLoader> variables) {
        StringBuilder query = new StringBuilder();
        for (PlayerVariableLoader var : variables) {
            query.append(var.getVariable().getVariableName()).append(" = '").append(getVariableHashedValue(var.getValue())).append("', ");
        }
        return "UPDATE '" + tableNamePlayers + "' SET " + query + "last_update = '" + now + "' WHERE uuid = '" + player.getUniqueId() + "';";
    }

}
