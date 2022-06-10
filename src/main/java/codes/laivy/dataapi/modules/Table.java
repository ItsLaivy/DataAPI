package codes.laivy.dataapi.modules;

import codes.laivy.dataapi.events.tables.TableLoadEvent;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import static codes.laivy.dataapi.main.DataAPI.plugin;

public class Table extends Creator {

    private final Database database;
    private boolean isSuccessfullyCreated = false;

    public Table(@NotNull Database database) {
        this(plugin(), "default", database);
    }
    public Table(@Nullable String name, @NotNull Database database) {
        this(plugin(), name, database);
    }
    public Table(@Nullable Plugin plugin, @Nullable String name, @NotNull Database database) {
        this(plugin, name, database, true);
    }
    public Table(@Nullable Plugin plugin, @Nullable String name, @NotNull Database database, boolean messages) {
        super(plugin, name, CreatorType.TABLE_CREATOR);

        Validate.notNull(database);
        this.database = database;

        if (!getDatabase().isSuccessfullyCreated()) {
            throw new IllegalStateException("this database was not created correctly");
        }

        if (plugin().getTables().get(getDatabase()).containsKey(getBruteId())) {
            throw new IllegalStateException("a table with that name already exists in plugin " + getPlugin() + " and database " + getDatabase().getBruteId() + ".");
        }

        if (!plugin().getConfig().getBoolean("table messages")) {
            messages = false;
        }

        // Try to create the table in the database
        String query = getDatabase().getConnectionType().getTableCreationQuery(getBruteId(), getDatabase());
        try (PreparedStatement pst = getDatabase().getConnection().prepareStatement(query)) {
            pst.execute();
            plugin().broadcastColoredMessage("&aTable &2'" + getName() + "' (" + getPlugin().getName() + ") &asucessfully created.", messages);
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                plugin().broadcastColoredMessage("&aTable &2'" + getName() + "' (" + getPlugin().getName() + ") &asucessfully loaded.", messages);
            } else {
                e.printStackTrace();
                plugin().broadcastColoredMessage("&cException trying to create table: " + getBruteId() + " of database: " + getDatabase().getBruteId() + " (" + getDatabase().getConnectionType() + "), full query: \"§5" + query + "§c\"");
                return;
            }
        }

        isSuccessfullyCreated = true;
        plugin().getTables().get(getDatabase()).put(getBruteId(), this);

        plugin().getVariables().put(this, new HashMap<>());
        plugin().getReceptors().put(this, new HashMap<>());

        plugin().getActiveVariables().put(this, new HashMap<>());
        plugin().getInactiveVariables().put(this, new HashMap<>());

        Bukkit.getPluginManager().callEvent(new TableLoadEvent(!Bukkit.isPrimaryThread(), this));
    }

    public Database getDatabase() {
        return database;
    }

    public boolean isSuccessfullyCreated() {
        return isSuccessfullyCreated;
    }

}
