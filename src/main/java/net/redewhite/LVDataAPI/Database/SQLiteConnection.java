package net.redewhite.LVDataAPI.Database;

import net.redewhite.LVDataAPI.Main;

import java.io.File;
import java.sql.*;

public class SQLiteConnection {

    private static final Main inst = Main.getInstance();
    public static Connection conn;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            File file = new File(inst.getDataFolder() + File.separator + "database.db");

            conn = DriverManager.getConnection("jdbc:sqlite:" + file);
            Main.broadcastInfo("SQLite connection has been successfully established.");
            createDatabase();
        } catch (Exception e) {
            Main.broadcastWarn("SQLite connection failed. Deactivating plugin...");
            inst.getPluginLoader().disablePlugin(inst);
        }
    }

    public static void close() {
        try {
            conn.close();
            Main.broadcastInfo("SQLite database has been closed.");
        } catch (SQLException e) {
            e.printStackTrace();
            Main.broadcastWarn("SQLite closing process could not be established.");
        }
    }

    public static Statement createStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            Main.broadcastWarn("SQLite's statement creation process failed.");
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
            Main.broadcastWarn("SQLite database create process failed. Deactivating plugin...");
            inst.getPluginLoader().disablePlugin(inst);
        }
    }


}
