package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.events.tables.TableLoadEvent;
import org.bukkit.Bukkit;
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

    public TableCreator(Database database) {
        this(INSTANCE, "default", database);
    }
    public TableCreator(String name, Database database) {
        this(INSTANCE, name, database);
    }
    public TableCreator(Plugin plugin, String name, Database database) {
        this.database = database;
        this.plugin = plugin;
        this.name = name;

        if (name == null) throw new NullPointerException("table name cannot be null");
        if (database == null) throw new NullPointerException("table database cannot be null");
        if (plugin == null) plugin = INSTANCE;

        Utils.bG("table", plugin, getBruteID());

        for (TableCreator table : getTables()) {
            if (table.getBruteID().equals(getBruteID())) {
                if (table.getDatabase().equals(database)) {
                    throw new IllegalStateException("a table with that name already exists in plugin " + table.getPlugin() + ". Use API.getTable() to get a table.");
                }
            }
        }

        if (!database.isSuccessfullyCreated()) {
            throw new IllegalStateException("this database was not created correctly");
        }

        // Verify if the name contains illegal characters
        if (Utils.iS("table", plugin, name)) {
            return;
        }

        // Try to create the table in the database
        String query = database.getConnectionType().getTableCreationQuery(getBruteID(), getDatabase().getBruteID());
        try (PreparedStatement pst = database.getConnection().prepareStatement(query)) {
            pst.execute();
            broadcastColoredMessage("&aTable &2'" + name + "' (" + plugin.getName() + ") &asucessfully created.");
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
               broadcastColoredMessage("&aTable &2'" + name + "' (" + plugin.getName() + ") &asucessfully loaded.");
            } else {
                e.printStackTrace();
                broadcastColoredMessage("&cException trying to create table: " + getBruteID() + " of database: " + getDatabase().getBruteID() + " (" + database.getConnectionType() + "), full query: \"§5" + query + "§c\"");
                return;
            }
        }

        isSuccessfullyCreated = true;
        getTables().add(this);

        TableLoadEvent event = new TableLoadEvent(!Bukkit.isPrimaryThread(), this);
        Bukkit.getPluginManager().callEvent(event);
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
