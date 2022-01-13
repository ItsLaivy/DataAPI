package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.receptors.InactiveVariable;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import net.redewhite.lvdataapi.types.VariablesType;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.redewhite.lvdataapi.developers.API.getVariableReceptorByBruteID;
import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class VariableCreator {

    private final TableCreator table;

    private final Plugin plugin;
    private final String name;
    private final Object defaultValue;

    private final VariablesType type;

    private final boolean saveToDatabase;
    private boolean isSuccessfullyCreated = false;

    public VariableCreator(Plugin plugin, String name, TableCreator table, Object defaultValue, Boolean saveToDatabase, VariablesType type, boolean messages) {
        this.plugin = plugin;
        this.table = table;
        this.name = name;

        this.type = type;

        this.saveToDatabase = saveToDatabase;

        Object newValue;
        if (defaultValue instanceof ArrayList) {
            ArrayList<String> array = new ArrayList<>();
            for (Object e : ((ArrayList<?>) defaultValue).toArray()) {
                array.add(getVariableHashedValue(e));
            }
            newValue = array.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
            if (newValue.equals("")) newValue = getVariableHashedValue(null);
        } else {
            newValue = defaultValue;
        }
        this.defaultValue = newValue;

        if (name == null) throw new NullPointerException("variable name cannot be null");
        if (table == null) throw new NullPointerException("variable table cannot be null");
        if (plugin == null) plugin = INSTANCE;

        if (!table.isSuccessfullyCreated()) {
            throw new IllegalStateException("this table was not created correctly.");
        }

        Utils.bG("variable", plugin, getBruteID());

        for (VariableCreator variable : getVariables()) {
            if (variable.getBruteID().equals(getBruteID())) {
                if (variable.getTable().equals(table)) {
                    isSuccessfullyCreated = true;
                    return;
                }
            }
        }

        // Verify if the name contains illegal characters
        if (Utils.iS("variable", plugin, name)) {
            return;
        }

        if (this.saveToDatabase) {
            String query = table.getDatabase().getConnectionType().getVariableCreationQuery(table.getBruteID(), getBruteID(), getVariableHashedValue(this.defaultValue), table.getDatabase().getBruteID());
            try (PreparedStatement pst = table.getDatabase().getConnection().prepareStatement(query)) {
                pst.execute();
                broadcastColoredMessage("&a" + type.getName() + "&2 '" + name + "' (" + plugin.getName() + ") &asuccessfully created.", messages);
            } catch (SQLException e) {
                if (e.getMessage().contains("uplicate column name")) {
                    broadcastColoredMessage("&a" + type.getName() + "&2 '" + name + "' (" + plugin.getName() + ") &asuccessfully loaded.", messages);
                } else {
                    e.printStackTrace();
                    broadcastColoredMessage("&cException trying to create " + type.getName().toLowerCase() + "&c: " + getBruteID() + " of table: " + table.getBruteID() + " (Database: " + table.getDatabase().getBruteID() + ", type: " + table.getDatabase().getConnectionType() + "), full query: \"§5" + query + "§c\"");
                }
            }
        } else {
            broadcastColoredMessage(VariablesType.TEMPORARY.getName() + " &a" + type.getName() + "&2 '" + name + "' (" + plugin.getName() + ") &asuccessfully loaded.", messages);
        }

        isSuccessfullyCreated = true;
        table.getVariables().add(this);
        getVariables().add(this);

        List<ReceptorCreator> receptors = new ArrayList<>();
        for (ReceptorCreator rec : getReceptors()) if (rec.getTable() == table) {
            receptors.add(rec);
        }

        for (InactiveVariable var : new ArrayList<>(getInactiveVariables())) {
            if (var.getTable() == table) if (var.getVariableBruteID().equals(getBruteID())) {
                new ActiveVariable(this, getVariableReceptorByBruteID(var.getOwnerBruteID(), table), var.getValue());
                getInactiveVariables().remove(var);
                receptors.remove(getVariableReceptorByBruteID(var.getOwnerBruteID(), var.getTable()));
            }
        }

        for (ReceptorCreator receptor : receptors) {
            if (this.saveToDatabase) {
                String query = receptor.getTable().getDatabase().getConnectionType().getSelectQuery("*", table.getBruteID(), "WHERE bruteid = '" + receptor.getBruteID() + "'", table.getDatabase().getBruteID());
                try (ResultSet result = table.getDatabase().createStatement().executeQuery(query)) {
                    while (result.next()) {
                        new ActiveVariable(this, receptor, getVariableHashedValue(result.getObject(getBruteID())));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                new ActiveVariable(this, receptor, defaultValue);
            }
        }
    }

    public VariablesType getType() {
        return type;
    }

    public boolean isSuccessfullyCreated() {
        return isSuccessfullyCreated;
    }

    public boolean isSaveToDatabase() {
        return saveToDatabase;
    }

    public Object getDefaultValue() {
        return defaultValue;
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
        return plugin.getName() + "_" + name;
    }
}
