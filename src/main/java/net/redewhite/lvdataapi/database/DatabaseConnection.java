package net.redewhite.lvdataapi.database;

import net.redewhite.lvdataapi.LvDataPlugin;

import java.io.File;
import java.sql.*;

import static net.redewhite.lvdataapi.LvDataPlugin.config;
import static net.redewhite.lvdataapi.LvDataPlugin.instance;

public class DatabaseConnection {

    public static Connection conn;

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + config.get("mysql-address") + ":" + config.get("mysql-port") + "/" + config.get("mysql-database"), (String) config.get("mysql-user"), (String) config.get("mysql-password"));
            LvDataPlugin.broadcastInfo("MySQL connection has been successfully established.");
            LvDataPlugin.database_type = "MySQL";
        } catch (Exception e) {
            if (!config.getBoolean("allow-sqlite")) {
                LvDataPlugin.broadcastWarn("MySQL connection failed. Deactivating plugin...");
                stopPlugin();
            } else {
                try {
                    Class.forName("org.sqlite.JDBC");
                    File file = new File(instance.getDataFolder() + File.separator + "database.db");

                    conn = DriverManager.getConnection("jdbc:sqlite:" + file);
                    LvDataPlugin.broadcastInfo("SQLite connection has been successfully established.");
                    LvDataPlugin.database_type = "SQLite";
                } catch (Exception ef) {
                    if (LvDataPlugin.debug) ef.printStackTrace();
                    LvDataPlugin.broadcastWarn("SQLite connection failed. Deactivating plugin...");
                    stopPlugin();
                }
            }
        }
        createDatabase();
    }

    public static void close() {
        try {
            conn.close();
            LvDataPlugin.broadcastInfo("Database database has been closed.");
        } catch (SQLException e) {
            if (LvDataPlugin.debug) e.printStackTrace();
            LvDataPlugin.broadcastWarn("Database closing process could not be established.");
        } catch (NullPointerException ignore) {
        }
    }

    private static void stopPlugin() {
        if (config.getBoolean("close-server")) {
            instance.getServer().shutdown();
        } else {
            instance.getPluginLoader().disablePlugin(instance);
        }
    }

    public static Statement createStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            LvDataPlugin.broadcastWarn("Database's statement creation process failed.");
        } catch (NullPointerException ignore) {
        }
        return null;
    }

    private static void createDatabase() {
        try (PreparedStatement pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `wn_data` (`id` INT AUTO_INCREMENT PRIMARY KEY, `nickname` TEXT, `uuid` TEXT, `last_update` TEXT)")) {
            pstmt.execute();
        } catch (SQLException e) {
            if (LvDataPlugin.debug) e.printStackTrace();
            LvDataPlugin.broadcastWarn("Database create process failed. Deactivating plugin...");
            stopPlugin();
        } catch (NullPointerException ignore) {
        }
    }

}
