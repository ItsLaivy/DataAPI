package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.types.ConnectionType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;

import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class Database {

    private final ConnectionType connectionType;
    private Connection connection;

    private final Plugin plugin;
    private final String name;
    private final String bruteID;

    private final String user;
    private final Integer port;
    private final String address;

    private final String path;
    private File file;

    private boolean isSuccessfullyCreated = false;

    public Database(String name, ConnectionType connectionType,

                    String user,
                    String password,
                    Integer port,
                    String address,

                    String path

    ) {
        this(INSTANCE, name, connectionType, user, password, port, address, path);
    }

    public Database(Plugin plugin,
                    String name,
                    ConnectionType connectionType,

                    String user,
                    String password,
                    Integer port,
                    String address,

                    String path
    ) {
        this.plugin = plugin;
        this.name = name;
        this.bruteID = plugin.getName() + "_" + name;

        this.connectionType = connectionType;

        if (name == null) throw new NullPointerException("database name cannot be null");

        if (connectionType == ConnectionType.MYSQL) {
            if (user == null) throw new NullPointerException("MySQL Database's user cannot be null");
            else if (password == null) throw new NullPointerException("MySQL Database's password cannot be null");
            else if (address == null) throw new NullPointerException("MySQL Database's address cannot be null");
        } else if (connectionType == ConnectionType.SQLITE) {
            if (path == null) throw new NullPointerException("SQLite Database's path cannot be null");
        }

        Utils.bG("database", plugin, getBruteID());

        for (Database database : getDatabases()) {
            if (database.getConnectionType().equals(connectionType)) {
                if (database.getBruteID().equals(getBruteID())) {
                    connection = database.connection;
                    this.user = database.user;
                    this.port = database.port;
                    this.address = database.address;
                    this.path = database.path;

                    isSuccessfullyCreated = true;
                    return;
                }
            }
        }

        this.user = user;
        this.port = port;
        this.address = address;
        this.path = path;

        // Verify if the name contains illegal characters
        if (Utils.iS("database", plugin, name)) {
            return;
        }

        try {

            boolean created = false;
            if (connectionType == ConnectionType.MYSQL) {
                String query = "CREATE DATABASE " + bruteID;

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://localhost/?user=" + user + "&password=" + password);
                    connection.createStatement().executeUpdate(query);
                    created = true;
                } catch (SQLException e) {
                    if (!e.getMessage().contains("; database exists")) {
                        e.printStackTrace();
                        broadcastColoredMessage("&cException trying to create database: " + getBruteID() + ", type: " + connectionType + ". Full query: \"§5" + query + "§c\"");
                        return;
                    }
                }
            } else if (connectionType == ConnectionType.SQLITE) {
                try {
                    Class.forName("org.sqlite.JDBC");
                    if (path.equals("")) {
                        file = new File(INSTANCE.getDataFolder(), getBruteID() + ".db");
                    } else {
                        file = new File(INSTANCE.getDataFolder() + File.separator + path, getBruteID() + ".db");
                    }

                    created = !file.exists();
                    connection = DriverManager.getConnection("jdbc:sqlite:" + file);
                } catch (SQLException e) {
                    e.printStackTrace();
                    broadcastColoredMessage("&cException trying to create database: " + getBruteID() + ", type: " + connectionType + ". Full query: \"§5" + "NONE" + "§c\"");
                    return;
                }
            }

            boolean l = config.getBoolean("database loading messages");
            boolean c = config.getBoolean("database creating messages");
            broadcastColoredMessage("&a" + connectionType.getName() + " Database &2'" + name + "' (" + plugin.getName() + ")&a successfully " + (created ? "created" : "loaded") + ".", (created ? c : l));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            broadcastColoredMessage("§cPlease, report this error at the plugin's github: §4https://github.com/LaivyTLife/DataAPI/issues");
            return;
        }

        isSuccessfullyCreated = true;
        getDatabases().add(this);
    }

    public boolean isSuccessfullyCreated() {
        return isSuccessfullyCreated;
    }

    public void delete() {
        String exception = null;
        if (connectionType.equals(ConnectionType.MYSQL)) {
            String query = ConnectionType.MYSQL.getDeleteDatabaseQuery(bruteID);
            
            try {
                createStatement().executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
                exception = query;
            }
        } else if (connectionType.equals(ConnectionType.SQLITE)) {
            if (!file.delete()) {
                exception = "NONE";
            }
        }

        if (exception != null) {
            broadcastColoredMessage("&cException trying to §ndelete§c database: " + getBruteID() + ", type: " + connectionType + ". Full query: \"§5" + exception + "§c\"");
        } else {
            broadcastColoredMessage("&c" + (connectionType == ConnectionType.MYSQL ? "MySQL" : "SQLite") + " Database &4'" + name + "' (" + plugin.getName() + ") &c successfully deleted.");
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }
    public String getName() {
        return name;
    }
    public String getBruteID() {
        return bruteID;
    }

    public String getUser() {
        return user;
    }
    public Integer getPort() {
        return port;
    }
    public String getAddress() {
        return address;
    }

    public String getPath() {
        return path;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement createStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignore) {
        }
        return null;
    }
    public void executeQuery(String query) {
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            broadcastColoredMessage("&cFull query: \"§5" + query + "§c\"");
        }
    }

}
