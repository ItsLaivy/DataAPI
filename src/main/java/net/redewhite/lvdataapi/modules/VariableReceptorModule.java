package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.API;
import net.redewhite.lvdataapi.receptors.InactiveVariableLoader;
import net.redewhite.lvdataapi.receptors.ActiveVariableLoader;
import net.redewhite.lvdataapi.types.ConnectionType;
import net.redewhite.lvdataapi.DataAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

import static net.redewhite.lvdataapi.DataAPI.*;
import static net.redewhite.lvdataapi.modules.VariableReturnModule.getVariableHashedValue;

@SuppressWarnings("unused")
public class VariableReceptorModule {

    private final TableCreationModule table;
    private final Plugin plugin;
    private final String name;
    private final String bruteID;

    private final List<ActiveVariableLoader> variables = new ArrayList<>();

    public VariableReceptorModule(Plugin plugin, String name, String bruteID, TableCreationModule table) {
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

        for (VariableCreationModule var : DataAPI.getVariables()) {
            if (!var.isSaveToDatabase()) {
                new ActiveVariableLoader(var, this, getVariableHashedValue(var.getDefaultValue()));
            }
        }

        String query = table.getDatabase().getConnectionType().getInsertQuery();
        query = ConnectionType.replace(query, table.getBruteID(), name, getBruteID(), getDate(), table.getDatabase().getBruteID());

        String query2 = table.getDatabase().getConnectionType().getSelectQuery();
        query2 = ConnectionType.replace(query2, "id", table.getBruteID(), "WHERE bruteid = '" + bruteID + "'", table.getDatabase().getBruteID());

        try {
            try (ResultSet result2 = table.getDatabase().createStatement().executeQuery(query2)) {
                if (!result2.next()) {
                    try (PreparedStatement pst = table.getDatabase().getConnection().prepareStatement(query)) {
                        pst.execute();
                    }
                }

                query = ConnectionType.replace(table.getDatabase().getConnectionType().getSelectQuery(), "*", table.getBruteID(), "WHERE bruteid = '" + bruteID + "'", table.getDatabase().getBruteID());
                try (ResultSet result = table.getDatabase().createStatement().executeQuery(query)) {
                    if (result.next()) {
                        ResultSetMetaData rmtd = result.getMetaData();
                        for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                            if (row > 4) {
                                new InactiveVariableLoader(bruteID, rmtd.getColumnName(row), table, result.getObject(row));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            getReceptors().remove(this);
        }
    }

    public TableCreationModule getTable() {
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

    public List<ActiveVariableLoader> getVariables() {
        return variables;
    }

    public void save() {
        StringBuilder query = new StringBuilder();

        for (ActiveVariableLoader var : variables) {
            if (var.getVariable().isSaveToDatabase()) {
                query.append(var.getVariable().getBruteID()).append(" = '").append(getVariableHashedValue(var.getValue())).append("',");
            }
        }

        query.append("last_update = '").append(getDate()).append("'");
        String fQuery = table.getDatabase().getConnectionType().getUpdateQuery();
        fQuery = ConnectionType.replace(fQuery, table.getBruteID(), query.toString(), getBruteID(), table.getDatabase().getBruteID());
        table.getDatabase().executeQuery(fQuery);
    }
    public void unload() {
        save();
        getReceptors().remove(this);

        getInactiveVariables().removeIf(in -> in.getOwnerBruteID().equals(bruteID));
        getActiveVariables().removeIf(ac -> ac.getReceptor() == this);
    }

    public void delete() {
        try (PreparedStatement pst = table.getDatabase().getConnection().prepareStatement("DELETE FROM '" + table.getBruteID() +  "' WHERE bruteid = '" + bruteID + "';")) {
            pst.execute();
            unload();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
