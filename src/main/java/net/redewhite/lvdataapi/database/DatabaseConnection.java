package net.redewhite.lvdataapi.database;

import net.redewhite.lvdataapi.LvDataPlugin;

import java.io.File;
import java.sql.*;

public class DatabaseConnection {

    private static final LvDataPlugin inst = LvDataPlugin.getInstance();
    public static Connection conn;

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + inst.getConfig().get("mysql-address") + ":" + inst.getConfig().get("mysql-port") + "/" + inst.getConfig().get("mysql-database"), (String) inst.getConfig().get("mysql-user"), (String) inst.getConfig().get("mysql-password"));
            LvDataPlugin.broadcastInfo("MySQL connection has been successfully established.");
            LvDataPlugin.database_type = "MySQL";
        } catch (Exception e) {
            if ((Boolean) inst.getConfig().get("allow-sqlite")) {
                try {
                    Class.forName("org.sqlite.JDBC");
                    File file = new File(inst.getDataFolder() + File.separator + "database.db");

                    conn = DriverManager.getConnection("jdbc:sqlite:" + file);
                    LvDataPlugin.broadcastInfo("SQLite connection has been successfully established.");
                    LvDataPlugin.database_type = "SQLite";
                } catch (Exception ef) {
                    if (LvDataPlugin.debug) ef.printStackTrace();
                    LvDataPlugin.broadcastWarn("SQLite connection failed. Deactivating plugin...");
                    stopPlugin();
                }
            } else {
                LvDataPlugin.broadcastWarn("MySQL connection failed. Deactivating plugin...");
                stopPlugin();
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
        }
    }

    private static void stopPlugin() {
        if (inst.getConfig().getBoolean("close-server")) {
            inst.getServer().shutdown();
            return;
        }
        inst.getPluginLoader().disablePlugin(inst);
    }

    public static Statement createStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            LvDataPlugin.broadcastWarn("Database's statement creation process failed.");
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
        }
    }

}
