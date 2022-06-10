package codes.laivy.dataapi.modules;

import codes.laivy.dataapi.events.databases.DatabaseLoadEvent;
import codes.laivy.dataapi.type.ConnectionType;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static codes.laivy.dataapi.main.DataAPI.plugin;

public class Database extends Creator {

    private final ConnectionType connectionType;

    private Connection connection;

    private final String user;
    private final Integer port;
    private final String address;

    private final String path;
    private File file;

    private boolean isSuccessfullyCreated = false;

    Database(
            @Nullable Plugin plugin,
            @Nullable String name,
            @NotNull ConnectionType connectionType,

            String user,
            String password,
            Integer port,
            String address,

            @Nullable String path,
            boolean messages
    ) {
        super(plugin, name, CreatorType.DATABASE_CREATOR);

        this.connectionType = connectionType;

        if (getConnectionType() == ConnectionType.MYSQL) {
            Validate.notNull(user);
            Validate.notNull(password);
            Validate.notNull(address);
            Validate.notNull(port);
        }

        if (plugin().getDatabases().get(getConnectionType()).containsKey(getBruteId())) {
            throw new IllegalStateException("That database already exists");
        } else {
            for (Map.Entry<String, Database> map : plugin().getDatabases().get(getConnectionType()).entrySet()) {
                if (map.getValue().getName().equals(getName())) {
                    throw new IllegalStateException("A database with that name already exists");
                }
            }

            this.user = user;
            this.port = port;
            this.address = address;
            this.path = path;

            if (!plugin().getConfig().getBoolean("database messages")) {
                messages = false;
            }

            try {
                boolean created = false;
                if (getConnectionType() == ConnectionType.MYSQL) {
                    String query = "CREATE DATABASE " + getBruteId();

                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        connection = DriverManager.getConnection("jdbc:mysql://" + address + "/?user=" + user + "&password=" + password + "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10");
                        getConnection().createStatement().executeUpdate(query);
                        created = true;
                    } catch (SQLException e) {
                        if (!e.getMessage().contains("; database exists")) {
                            e.printStackTrace();
                            plugin().broadcastColoredMessage("&cException trying to create database: " + getBruteId() + ", type: " + getConnectionType() + ". Full query: \"§5" + query + "§c\"");
                            return;
                        }
                    }
                } else if (getConnectionType() == ConnectionType.SQLITE) {
                    try {
                        Class.forName("org.sqlite.JDBC");
                        if (path == null) {
                            file = new File(plugin().getDataFolder(), getBruteId() + ".db");
                        } else {
                            file = new File(plugin().getDataFolder() + File.separator + path, getBruteId() + ".db");
                        }

                        created = !file.exists();
                        connection = DriverManager.getConnection("jdbc:sqlite:" + file);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        plugin().broadcastColoredMessage("&cException trying to create database: " + getBruteId() + ", type: " + getConnectionType() + ". Full query: \"§5" + "NONE" + "§c\"");
                        return;
                    }
                }

                plugin().broadcastColoredMessage("&a" + getConnectionType().getName() + " Database &2'" + getName() + "' (" + getPlugin().getName() + ")&a successfully " + (created ? "created" : "loaded") + ".", messages);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                plugin().broadcastColoredMessage("§cPlease, report this error at the plugin's github: §4https://github.com/ItsLaivy/DataAPI/issues");
                return;
            }

            isSuccessfullyCreated = true;
            plugin().getDatabases().get(getConnectionType()).put(getBruteId(), this);
            plugin().getTables().put(this, new HashMap<>());

            Bukkit.getPluginManager().callEvent(new DatabaseLoadEvent(!Bukkit.isPrimaryThread(), this));
        }
    }

    @NotNull
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    @NotNull
    public Connection getConnection() {
        return connection;
    }

    public boolean isSuccessfullyCreated() {
        return isSuccessfullyCreated;
    }

    @SuppressWarnings("unused")
    public static class MySQLDatabase extends Database {
        public MySQLDatabase(@NotNull String user, @NotNull String password, @NotNull Integer port, @NotNull String address) {
            super(plugin(), null, ConnectionType.MYSQL, user, password, port, address, null, true);
        }
        public MySQLDatabase(@Nullable String name, @NotNull String user, @NotNull String password, @NotNull Integer port, @NotNull String address) {
            super(plugin(), name, ConnectionType.MYSQL, user, password, port, address, null, true);
        }
        public MySQLDatabase(@Nullable String name, @NotNull String user, @NotNull String password, @NotNull Integer port, @NotNull String address, boolean messages) {
            super(plugin(), name, ConnectionType.MYSQL, user, password, port, address, null, messages);
        }

        public MySQLDatabase(@Nullable Plugin plugin, @Nullable String name, @NotNull String user, @NotNull String password, @NotNull Integer port, @NotNull String address) {
            super(plugin, name, ConnectionType.MYSQL, user, password, port, address, null, true);
        }
        public MySQLDatabase(@Nullable Plugin plugin, @Nullable String name, @NotNull String user, @NotNull String password, @NotNull Integer port, @NotNull String address, boolean messages) {
            super(plugin, name, ConnectionType.MYSQL, user, password, port, address, null, messages);
        }

        @NotNull
        public String getUser() {
            return super.user;
        }
        @NotNull
        public Integer getPort() {
            return super.port;
        }
        @NotNull
        public String getAddress() {
            return super.address;
        }
    }

    @SuppressWarnings("unused")
    public static class SQLiteDatabase extends Database {
        public SQLiteDatabase() {
            super(plugin(), null, ConnectionType.SQLITE, null, null, null, null, "", true);
        }
        public SQLiteDatabase(@Nullable String name) {
            super(plugin(), name, ConnectionType.SQLITE, null, null, null, null, "", true);
        }

        public SQLiteDatabase(@Nullable String name, @Nullable String path) {
            super(plugin(), name, ConnectionType.SQLITE, null, null, null, null, path, true);
        }
        public SQLiteDatabase(@Nullable String name, @Nullable String path, boolean messages) {
            super(plugin(), name, ConnectionType.SQLITE, null, null, null, null, path, messages);
        }

        public SQLiteDatabase(@Nullable Plugin plugin, @Nullable String name, @Nullable String path) {
            super(plugin, name, ConnectionType.SQLITE, null, null, null, null, path, true);
        }
        public SQLiteDatabase(@Nullable Plugin plugin, @Nullable String name, @Nullable String path, boolean messages) {
            super(plugin, name, ConnectionType.SQLITE, null, null, null, null, path, messages);
        }

        @Nullable
        public String getPath() {
            return super.path;
        }
        @NotNull
        public File getFile() {
            return super.file;
        }
    }

    @Nullable
    public Statement createStatement() {
        try {
            return getConnection().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignore) {
        }
        return null;
    }
    public void executeQuery(@NotNull String query) {
        Validate.notNull(query);

        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            plugin().broadcastColoredMessage("&cFull query: \"§5" + query + "§c\"");
        }
    }

}
