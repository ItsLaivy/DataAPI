package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.API;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorDeleteEvent;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorLoadEvent;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorSaveEvent;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorUnloadEvent;
import net.redewhite.lvdataapi.receptors.InactiveVariable;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import net.redewhite.lvdataapi.DataAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.List;

import static net.redewhite.lvdataapi.DataAPI.*;
import static net.redewhite.lvdataapi.developers.API.getVariableValue;

@SuppressWarnings("unused")
public class ReceptorCreator {

    private final TableCreator table;
    private final Plugin plugin;
    private final String name;
    private final String bruteID;

    private final List<ActiveVariable> variables = new ArrayList<>();

    public ReceptorCreator(Plugin plugin, String name, String bruteID, TableCreator table) {
        this.plugin = plugin;
        this.table = table;
        this.name = name;
        this.bruteID = bruteID;

        if (name == null) throw new NullPointerException("variable name cannot be null");
        if (table == null) throw new NullPointerException("variable table cannot be null");
        if (plugin == null) plugin = INSTANCE;

        if (getBruteID().length() > 64) {
            throw new IllegalStateException("receptor name is too big (Name: " + name + ", Plugin: " + plugin.getName() + ")");
        }
        if (API.isVariableReceptorLoaded(plugin, bruteID, table)) {
            throw new IllegalStateException("a receptor with that name already exists at this plugin instance (Name: " + name + ", Plugin: " + plugin.getName() + ")");
        }

        getReceptors().add(this);

        for (VariableCreator var : DataAPI.getVariables()) {
            if (!var.isSaveToDatabase()) {
                new ActiveVariable(var, this, getVariableHashedValue(var.getDefaultValue()));
            }
        }

        String query = table.getDatabase().getConnectionType().getInsertQuery(table.getBruteID(), name, getBruteID(), table.getDatabase().getBruteID());
        String query2 = table.getDatabase().getConnectionType().getSelectQuery("id", table.getBruteID(), "WHERE bruteid = '" + bruteID + "'", table.getDatabase().getBruteID());
        try {
            try (ResultSet result2 = table.getDatabase().createStatement().executeQuery(query2)) {
                if (!result2.next()) {
                    try (PreparedStatement pst = table.getDatabase().getConnection().prepareStatement(query)) {
                        pst.execute();
                    }
                }

                query = table.getDatabase().getConnectionType().getSelectQuery("*", table.getBruteID(),  "WHERE bruteid = '" + bruteID + "'", table.getDatabase().getBruteID());
                try (ResultSet result = table.getDatabase().createStatement().executeQuery(query)) {
                    if (result.next()) {
                        ResultSetMetaData rmtd = result.getMetaData();
                        for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                            if (row > 4) {
                                new InactiveVariable(bruteID, rmtd.getColumnName(row), table, result.getObject(row));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            getReceptors().remove(this);
        }

        ReceptorLoadEvent event = new ReceptorLoadEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

    public TableCreator getTable() {
        return table;
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

    public List<ActiveVariable> getVariables() {
        return variables;
    }

    public boolean hasLoadedBefore() {
        return getVariableValue("hasLoadedBefore", this).asBoolean();
    }
    public long timesLoaded() {
        return getVariableValue("timesLoaded", this).asLong();
    }

    public void save() {
        StringBuilder query = new StringBuilder();

        for (ActiveVariable var : variables) {
            if (var.getVariable().isSaveToDatabase()) {
                query.append(var.getVariable().getBruteID()).append(" = '").append(getVariableHashedValue(var.getValue())).append("',");
            }
        }

        query.append("last_update = '").append(getDate()).append("'");
        String fQuery = table.getDatabase().getConnectionType().getUpdateQuery(table.getBruteID(), query.toString(), getBruteID(), table.getDatabase().getBruteID());
        table.getDatabase().executeQuery(fQuery);

        ReceptorSaveEvent event = new ReceptorSaveEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }
    public void unload() {
        ReceptorUnloadEvent event = new ReceptorUnloadEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        save();
        getReceptors().remove(this);

        getInactiveVariables().removeIf(in -> in.getOwnerBruteID().equals(bruteID));
        getActiveVariables().removeIf(ac -> ac.getReceptor() == this);
    }

    public void delete() {
        ReceptorDeleteEvent event = new ReceptorDeleteEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        try (PreparedStatement pst = table.getDatabase().getConnection().prepareStatement("DELETE FROM '" + table.getBruteID() +  "' WHERE bruteid = '" + bruteID + "';")) {
            pst.execute();
            unload();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
