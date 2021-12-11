package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.types.ConnectionType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;

import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class DatabaseCreationModule {

    private final ConnectionType connectionType;
    private Connection connection;

    private final Plugin plugin;
    private final String name;
    private final String bruteID;

    private final String user;
    private final Integer port;
    private final String address;

    private final String path;

    private boolean isSuccessfullyCreated = false;

    public DatabaseCreationModule(String name, ConnectionType connectionType,

                                  String user,
                                  String password,
                                  Integer port,
                                  String address,

                                  String path

    ) {
        this(INSTANCE, name, connectionType, user, password, port, address, path);
    }

    public DatabaseCreationModule(Plugin plugin,
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

        this.user = user;
        this.port = port;
        this.address = address;

        this.path = path;

        if (name == null) throw new NullPointerException("database name cannot be null");

        if (connectionType == ConnectionType.MYSQL) {
            if (user == null) throw new NullPointerException("MySQL Database's user cannot be null");
            else if (password == null) throw new NullPointerException("MySQL Database's password cannot be null");
            else if (address == null) throw new NullPointerException("MySQL Database's address cannot be null");
        } else if (connectionType == ConnectionType.SQLITE) {
            if (path == null) throw new NullPointerException("SQLite Database's path cannot be null");
        }

        if (getBruteID().length() > 64) {
            throw new IllegalStateException("database name is too big (Name: " + name + ", Plugin: " + plugin.getName() + ")");
        }
        for (DatabaseCreationModule database : getDatabases()) {
            if (database.getBruteID().equals(getBruteID())) {
                connection = database.connection;

                isSuccessfullyCreated = true;
                return;
            }
        }

        // Verify if the name contains illegal characters
        String[] blocked = "-S,S=S[S]S.S/S*S-S+S;S:S(S)".split("S");
        for (String block : blocked) {
            if (name.contains(block)) {
                broadcastColoredMessage("&cThat's database name contains illegal characters (" + block + ")");
                return;
            }
        }

        if (connectionType == ConnectionType.MYSQL) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost/?user=" + user + "&password=" + password);
                connection.createStatement().executeUpdate("CREATE DATABASE " + bruteID);
                broadcastColoredMessage("&aMySQL Database &2'" + name + "'&a successfully created.");
            } catch (Exception e) {
                if (!e.getMessage().contains("; database exists")) {
                    e.printStackTrace();
                    return;
                } else {
                    broadcastColoredMessage("&aMySQL Database &2'" + name + "'&a successfully loaded.");
                }
            }
        } else if (connectionType == ConnectionType.SQLITE) {
            try {
                Class.forName("org.sqlite.JDBC");
                File file;

                if (path.equals("")) {
                    file = new File(INSTANCE.getDataFolder(), getBruteID() + ".db");
                } else {
                    file = new File(INSTANCE.getDataFolder() + File.separator + path, getBruteID() + ".db");
                }
                connection = DriverManager.getConnection("jdbc:sqlite:" + file);
                broadcastColoredMessage("&aSQLite Database &2'" + name + "'&a successfully loaded.");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        isSuccessfullyCreated = true;
        getDatabases().add(this);
    }

    public boolean isSuccessfullyCreated() {
        return isSuccessfullyCreated;
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
        }
    }

}
