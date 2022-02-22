package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.events.databases.DatabaseLoadEvent;
import net.redewhite.lvdataapi.types.ConnectionType;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;

import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class Database extends Creator {

    private final ConnectionType connectionType;
    private Connection connection;

    private final String user;
    private final Integer port;
    private final String address;

    private final String path;
    private File file;

    private boolean isSuccessfullyCreated;

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
        super(plugin, name, CreatorType.DATABASE_CREATOR);
        
        this.connectionType = connectionType;
        
        if (connectionType == ConnectionType.MYSQL) {
            Validate.notNull(user);
            Validate.notNull(password);
            Validate.notNull(address);
        } else if (connectionType == ConnectionType.SQLITE) {
            Validate.notNull(path);
        }
        
        for (Database database : getDatabases()) {
            if (database.getConnectionType().equals(connectionType)) {
                if (database.getBruteId().equals(getBruteId())) {
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

        try {
            boolean created = false;
            if (connectionType == ConnectionType.MYSQL) {
                String query = "CREATE DATABASE " + getBruteId();

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://localhost/?user=" + user + "&password=" + password);
                    connection.createStatement().executeUpdate(query);
                    created = true;
                } catch (SQLException e) {
                    if (!e.getMessage().contains("; database exists")) {
                        e.printStackTrace();
                        broadcastColoredMessage("&cException trying to create database: " + getBruteId() + ", type: " + connectionType + ". Full query: \"§5" + query + "§c\"");
                        isSuccessfullyCreated = false;
                        return;
                    }
                }
            } else if (connectionType == ConnectionType.SQLITE) {
                try {
                    Class.forName("org.sqlite.JDBC");
                    if (path.equals("")) {
                        file = new File(INSTANCE.getDataFolder(), getBruteId() + ".db");
                    } else {
                        file = new File(INSTANCE.getDataFolder() + File.separator + path, getBruteId() + ".db");
                    }

                    created = !file.exists();
                    connection = DriverManager.getConnection("jdbc:sqlite:" + file);
                } catch (SQLException e) {
                    e.printStackTrace();
                    broadcastColoredMessage("&cException trying to create database: " + getBruteId() + ", type: " + connectionType + ". Full query: \"§5" + "NONE" + "§c\"");
                    isSuccessfullyCreated = false;
                    return;
                }
            }

            boolean l = config.getBoolean("database loading messages");
            boolean c = config.getBoolean("database creating messages");
            broadcastColoredMessage("&a" + connectionType.getName() + " Database &2'" + name + "' (" + plugin.getName() + ")&a successfully " + (created ? "created" : "loaded") + ".", (created ? c : l));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            broadcastColoredMessage("§cPlease, report this error at the plugin's github: §4https://github.com/ItsLaivy/DataAPI/issues");
            isSuccessfullyCreated = false;
            return;
        }

        isSuccessfullyCreated = true;
        getDatabases().add(this);

        Bukkit.getPluginManager().callEvent(new DatabaseLoadEvent(!Bukkit.isPrimaryThread(), this));
    }

    public boolean isSuccessfullyCreated() {
        return isSuccessfullyCreated;
    }

    public void delete() {
        String exception = null;
        if (connectionType.equals(ConnectionType.MYSQL)) {
            String query = ConnectionType.MYSQL.getDeleteDatabaseQuery(getBruteId());
            
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
            broadcastColoredMessage("&cException trying to §ndelete§c database: " + getBruteId() + ", type: " + connectionType + ". Full query: \"§5" + exception + "§c\"");
        } else {
            broadcastColoredMessage("&c" + (connectionType == ConnectionType.MYSQL ? "MySQL" : "SQLite") + " Database &4'" + getName() + "' (" + getPlugin().getName() + ") &c successfully deleted.");
        }
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
