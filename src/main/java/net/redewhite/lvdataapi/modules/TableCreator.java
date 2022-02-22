package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.events.tables.TableLoadEvent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class TableCreator extends Creator {

    private final Database database;

    private final List<VariableCreator> variables = new ArrayList<>();

    private boolean isSuccessfullyCreated;

    public TableCreator(Database database) {
        this(INSTANCE, "default", database);
    }
    public TableCreator(String name, Database database) {
        this(INSTANCE, name, database);
    }
    public TableCreator(Plugin plugin, String name, Database database) {
        super(plugin, name, CreatorType.TABLE_CREATOR);

        Validate.notNull(database);
        this.database = database;

        for (TableCreator table : getTables()) {
            if (table.getBruteId().equals(getBruteId())) {
                if (table.getDatabase().equals(database)) {
                    throw new IllegalStateException("a table with that name already exists in plugin " + table.getPlugin() + ". Use API.getTable() to get a table.");
                }
            }
        }

        if (!database.isSuccessfullyCreated()) {
            throw new IllegalStateException("this database was not created correctly");
        }

        // Try to create the table in the database
        isSuccessfullyCreated = true;
        String query = database.getConnectionType().getTableCreationQuery(getBruteId(), getDatabase().getBruteId());
        try (PreparedStatement pst = database.getConnection().prepareStatement(query)) {
            pst.execute();
            broadcastColoredMessage("&aTable &2'" + name + "' (" + plugin.getName() + ") &asucessfully created.");
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
               broadcastColoredMessage("&aTable &2'" + name + "' (" + plugin.getName() + ") &asucessfully loaded.");
            } else {
                e.printStackTrace();
                broadcastColoredMessage("&cException trying to create table: " + getBruteId() + " of database: " + getDatabase().getBruteId() + " (" + database.getConnectionType() + "), full query: \"§5" + query + "§c\"");
                isSuccessfullyCreated = false;
                return;
            }
        }

        getTables().add(this);
        Bukkit.getPluginManager().callEvent(new TableLoadEvent(!Bukkit.isPrimaryThread(), this));
    }

    public boolean isSuccessfullyCreated() {
        return isSuccessfullyCreated;
    }

    public Database getDatabase() {
        return database;
    }

    public List<VariableCreator> getVariables() {
        return variables;
    }
}
