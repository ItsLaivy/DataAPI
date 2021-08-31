package net.redewhite.lvdataapi.database;

import net.redewhite.lvdataapi.DataAPI;

import java.io.File;
import java.sql.*;

import static net.redewhite.lvdataapi.DataAPI.databaseConnection.*;
import static net.redewhite.lvdataapi.DataAPI.*;

public class DatabaseConnection {

    public static Connection conn;

    public static Boolean connect(DataAPI.databaseConnection type) {
        if (type == MYSQL) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://" + config.get("mysql-address") + ":" + config.get("mysql-port") + "/" + config.get("mysql-database"), (String) config.get("mysql-user"), (String) config.get("mysql-password"));
                database_type = MYSQL;
                getMessage("Connection established", "MySQL");
                return true;
            } catch (Exception ignore) {
            }
        } else if (type == SQLITE) {
            try {
                Class.forName("org.sqlite.JDBC");
                File file = new File(instance.getDataFolder() + File.separator + config.get("Database Name") + ".db");
                conn = DriverManager.getConnection("jdbc:sqlite:" + file);
                database_type = SQLITE;
                getMessage("Connection established", "SQLite");
                return true;
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
                getMessage("Connection closed");
            }
        } catch (SQLException e) {
            if (debug) e.printStackTrace();
            getMessage("Connection closing process failed");
        } catch (NullPointerException e) {
            if (debug) e.printStackTrace();
        }
    }

    public static Statement createStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            getMessage("Connection statement creating process failed");
        } catch (NullPointerException ignore) {
        }
        return null;
    }

}
