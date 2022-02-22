package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.events.variables.VariableCreateEvent;
import net.redewhite.lvdataapi.developers.events.variables.VariableLoadEvent;
import net.redewhite.lvdataapi.receptors.InactiveVariable;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import net.redewhite.lvdataapi.types.VariablesType;
import net.redewhite.lvdataapi.types.variables.Pair;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.redewhite.lvdataapi.developers.API.getVariableReceptorByBruteID;
import static net.redewhite.lvdataapi.DataAPI.*;
import static net.redewhite.lvdataapi.types.VariablesType.*;

@SuppressWarnings("unused")
public class VariableCreator extends Creator {

    private final TableCreator table;

    private final Object defaultValue;

    private final VariablesType type;

    private final boolean saveToDatabase;
    private boolean isSuccessfullyCreated;

    public VariableCreator(Plugin plugin, String name, TableCreator table, Object defaultValue, Boolean saveToDatabase, VariablesType type, boolean messages) {
        super(plugin, name, CreatorType.VARIABLE_CREATOR);
        Validate.notNull(table);

        this.table = table;
        this.type = type;
        this.saveToDatabase = saveToDatabase;

        Object newValue;
        if (defaultValue instanceof List) {
            List<String> array = new ArrayList<>();

            for (Object e : ((ArrayList<?>) defaultValue).toArray()) {
                array.add(getVariableHashedValue(e));
            }
            newValue = replaceListVariable(array);
            if (newValue.equals("")) newValue = getVariableHashedValue(null);
        } else if (defaultValue instanceof Map) {
            Map<String, String> map = new HashMap<>();

            for (Map.Entry<?, ?> e : ((Map<?, ?>) defaultValue).entrySet()) {
                map.put(getVariableHashedValue(e.getKey()), getVariableHashedValue(e.getValue()));
            }
            newValue = replaceMapVariable(map);
            if (newValue.equals("")) newValue = getVariableHashedValue(null);
        } else if (defaultValue instanceof Pair) {
            newValue = replacePairVariable((Pair<?, ?>) defaultValue);
            if (newValue.equals("")) newValue = getVariableHashedValue(null);
        } else {
            newValue = defaultValue;
        }

        this.defaultValue = newValue;

        if (!table.isSuccessfullyCreated()) {
            throw new IllegalStateException("this table was not created correctly.");
        }

        isSuccessfullyCreated = true;
        for (VariableCreator variable : getVariables()) {
            if (variable.getBruteId().equals(getBruteId())) {
                if (variable.getTable().equals(table)) {
                    return;
                }
            }
        }

        Event event = new VariableLoadEvent(!Bukkit.isPrimaryThread(), this);
        if (this.saveToDatabase) {
            String query = table.getDatabase().getConnectionType().getVariableCreationQuery(table.getBruteId(), getBruteId(), getVariableHashedValue(this.defaultValue), table.getDatabase().getBruteId());
            try (PreparedStatement pst = table.getDatabase().getConnection().prepareStatement(query)) {
                pst.execute();
                broadcastColoredMessage("&a" + type.getName() + "&2 '" + name + "' (" + plugin.getName() + ") &asuccessfully created.", messages);
                event = new VariableCreateEvent(!Bukkit.isPrimaryThread(), this);
            } catch (SQLException e) {
                if (e.getMessage().contains("uplicate column name")) {
                    broadcastColoredMessage("&a" + type.getName() + "&2 '" + name + "' (" + plugin.getName() + ") &asuccessfully loaded.", messages);
                } else {
                    e.printStackTrace();
                    broadcastColoredMessage("&cException trying to create " + type.getName().toLowerCase() + "&c: " + getBruteId() + " of table: " + table.getBruteId() + " (Database: " + table.getDatabase().getBruteId() + ", type: " + table.getDatabase().getConnectionType() + "), full query: \"§5" + query + "§c\"");
                    isSuccessfullyCreated = false;
                    return;
                }
            }
        } else {
            broadcastColoredMessage(TEMPORARY.getName() + " &a" + type.getName() + "&2 '" + name + "' (" + plugin.getName() + ") &asuccessfully loaded.", messages);
        }

        Bukkit.getPluginManager().callEvent(event);

        table.getVariables().add(this);
        getVariables().add(this);

        List<ReceptorCreator> receptors = new ArrayList<>();
        for (ReceptorCreator rec : getReceptors()) if (rec.getTable() == table) {
            receptors.add(rec);
        }

        for (InactiveVariable var : new ArrayList<>(getInactiveVariables())) {
            if (var.getTable() == table) if (var.getVariableBruteID().equals(getBruteId())) {
                new ActiveVariable(this, getVariableReceptorByBruteID(var.getOwnerBruteID(), table), var.getValue());
                getInactiveVariables().remove(var);
                receptors.remove(getVariableReceptorByBruteID(var.getOwnerBruteID(), var.getTable()));
            }
        }

        for (ReceptorCreator receptor : receptors) {
            if (this.saveToDatabase) {
                String query = receptor.getTable().getDatabase().getConnectionType().getSelectQuery("*", table.getBruteId(), "WHERE bruteid = '" + receptor.getBruteId() + "'", table.getDatabase().getBruteId());
                try (ResultSet result = table.getDatabase().createStatement().executeQuery(query)) {
                    while (result.next()) {
                        new ActiveVariable(this, receptor, getVariableHashedValue(result.getObject(getBruteId())));
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
    public String getBruteId() {
        return plugin.getName() + "_" + name;
    }
}
