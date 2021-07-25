package net.redewhite.LVDataAPI.database;

import net.redewhite.LVDataAPI.LvDataPlugin;

import java.io.File;
import java.sql.*;

public class SQLiteConnection {

    private static final LvDataPlugin inst = LvDataPlugin.getInstance();
    public static Connection conn;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            File file = new File(inst.getDataFolder() + File.separator + "database.db");

            conn = DriverManager.getConnection("jdbc:sqlite:" + file);
            LvDataPlugin.broadcastInfo("SQLite connection has been successfully established.");
            createDatabase();
        } catch (Exception e) {
            LvDataPlugin.broadcastWarn("SQLite connection failed. Deactivating plugin...");
            inst.getPluginLoader().disablePlugin(inst);
        }
    }

    public static void close() {
        try {
            conn.close();
            LvDataPlugin.broadcastInfo("SQLite database has been closed.");
        } catch (SQLException e) {
            e.printStackTrace();
            LvDataPlugin.broadcastWarn("SQLite closing process could not be established.");
        }
    }

    public static Statement createStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            LvDataPlugin.broadcastWarn("SQLite's statement creation process failed.");
        }
        return null;
    }

    private static void createDatabase() {
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `wn_data` (`id` INTEGER, `nickname` TEXT, `uuid` TEXT, `last_update` TEXT, PRIMARY KEY(`id` AUTOINCREMENT))");
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            LvDataPlugin.broadcastWarn("SQLite database create process failed. Deactivating plugin...");
            inst.getPluginLoader().disablePlugin(inst);
        }
    }


}
