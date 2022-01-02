package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.types.ConnectionType;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class TableCreator {

    private final Database database;

    private final Plugin plugin;
    private final String name;

    private final List<VariableCreator> variables = new ArrayList<>();

    private boolean isSuccessfullyCreated = false;

    public TableCreator(Plugin plugin, String name, Database database) {
        this.database = database;
        this.plugin = plugin;
        this.name = name;

        if (name == null) throw new NullPointerException("table name cannot be null");
        if (database == null) throw new NullPointerException("table database cannot be null");
        if (plugin == null) plugin = INSTANCE;

        if (getBruteID().length() > 64) {
            throw new IllegalStateException("table name is too big (Name: " + name + ", Plugin: " + plugin.getName() + ")");
        }

        for (TableCreator table : getTables()) {
            if (table.getBruteID().equals(getBruteID())) {
                throw new IllegalStateException("a table with that name already exists in plugin " + table.getPlugin() + ". Use API.getTable() to get a table.");
            }
        }

        if (!database.isSuccessfullyCreated()) {
            throw new IllegalStateException("this database was not created correctly");
        }

        // Verify if the name contains illegal characters
        String[] blocked = "-S,S=S[S]S.S/S*S-S+S;S:S(S)".split("S");
        for (String block : blocked) {
            if (name.contains(block)) {
                broadcastColoredMessage("&cThat's table name contains illegal characters (" + block + ")");
                return;
            }
        }

        // Try to create the table in the database
        try (PreparedStatement pst = database.getConnection().prepareStatement(ConnectionType.replace(database.getConnectionType().getCreationTableQuery(), getBruteID(), getDatabase().getBruteID()))) {
            pst.execute();
            broadcastColoredMessage("&aTable &2'" + name + "' &asucessfully created.");
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
               broadcastColoredMessage("&aTable &2'" + name + "' &asucessfully loaded.");
            } else {
                e.printStackTrace();
                return;
            }
        }

        isSuccessfullyCreated = true;
        getTables().add(this);
    }

    public boolean isSuccessfullyCreated() {
        return isSuccessfullyCreated;
    }

    public Database getDatabase() {
        return database;
    }

    public Plugin getPlugin() {
        return plugin;
    }
    public String getName() {
        return name;
    }
    public String getBruteID() {
        return plugin.getName() + "_" + name;
    }

    public List<VariableCreator> getVariables() {
        return variables;
    }
}
