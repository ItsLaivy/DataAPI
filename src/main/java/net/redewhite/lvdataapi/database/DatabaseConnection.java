package net.redewhite.lvdataapi.database;

import java.io.File;
import java.sql.*;

import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

public class DatabaseConnection {

    public static Connection conn;

    public static Boolean connect(databaseConnection type) {
        if (type == MYSQL) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://" + config.get("mysql-address") + ":" + config.get("mysql-port") + "/" + config.get("mysql-database"), (String) config.get("mysql-user"), (String) config.get("mysql-password"));
                broadcastColoredMessage("§aMySQL connection has been successfully established.");
                database_type = MYSQL;
                if (createDatabase()) return true;
            } catch (Exception ignore) {
            }
        } else if (type == SQLITE) {
            try {
                Class.forName("org.sqlite.JDBC");
                File file = new File(instance.getDataFolder() + File.separator + config.get("Database Name") + ".db");
                conn = DriverManager.getConnection("jdbc:sqlite:" + file);
                broadcastColoredMessage("§aSQLite connection has been successfully established.");
                database_type = SQLITE;
                if (createDatabase()) return true;
            } catch (Exception e) {
                if (debug) e.printStackTrace();
            }
        }
        return false;
    }

    public static void close() {
        try {
            if (conn != null) {
                conn.close();
                broadcastColoredMessage("§aDatabase has been closed.");
            }
        } catch (SQLException e) {
            if (debug) e.printStackTrace();
            broadcastColoredMessage("§cDatabase closing process could not be established.");
        } catch (NullPointerException e) {
            if (debug) e.printStackTrace();
        }
    }

    public static Statement createStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            broadcastColoredMessage("§cDatabase's statement creation process failed.");
        } catch (NullPointerException ignore) {
        }
        return null;
    }

    private static Boolean createDatabase() {
        boolean success1;
        boolean success2;
        boolean success = true;
        try (PreparedStatement pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS '" + tableNamePlayers + "' ('id' INT AUTO_INCREMENT PRIMARY KEY, 'nickname' TEXT, 'uuid' TEXT, 'last_update' TEXT)")) {
            success1 = true;
            pstmt.execute();
        } catch (SQLException | NullPointerException e) {
            if (debug) e.printStackTrace();
            success1 = false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS '" + tableNameText + "' ('id' INT AUTO_INCREMENT PRIMARY KEY, 'name' TEXT, 'last_update' TEXT)")) {
            success2 = true;
            pstmt.execute();
        } catch (SQLException | NullPointerException e) {
            if (debug) e.printStackTrace();
            success2 = false;
        }
        if (!success1) {
            success = false;
            broadcastColoredMessage("§cUsers table create process failed. Deactivating plugin...");
        } if (!success2) {
            success = false;
            broadcastColoredMessage("§cText variables table create process failed. Deactivating plugin...");
        }
        return success;
    }

}
