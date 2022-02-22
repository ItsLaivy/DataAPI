package net.redewhite.lvdataapi.modules;

import com.sun.istack.internal.Nullable;
import net.redewhite.lvdataapi.developers.API;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorDeleteEvent;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorLoadEvent;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorSaveEvent;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorUnloadEvent;
import net.redewhite.lvdataapi.receptors.InactiveVariable;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import net.redewhite.lvdataapi.DataAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.List;

import static net.redewhite.lvdataapi.DataAPI.*;
import static net.redewhite.lvdataapi.developers.API.getVariableFromReceptor;

@SuppressWarnings("unused")
public class ReceptorCreator extends Creator {

    private final TableCreator table;

    private boolean isAlreadyLoaded = false;
    private boolean autoSaveOnServerClose = true;

    private ReceptorCreator thisReceptor = this;

    private List<ActiveVariable> variables = new ArrayList<>();

    public ReceptorCreator(Plugin plugin, @Nullable String name, String bruteId, TableCreator table) {
        super(plugin, name, CreatorType.RECEPTOR_CREATOR, bruteId);
        
        Validate.notNull(table);
        this.table = table;

        Validate.notNull(table);
        if (plugin == null) plugin = INSTANCE;
        
        if (API.isVariableReceptorLoaded(plugin, getBruteId(), table)) {
            ReceptorCreator p = API.getVariableReceptor(plugin, getBruteId(), table);

            variables = p.variables;
            autoSaveOnServerClose = p.autoSaveOnServerClose;
            this.name = p.name;
            thisReceptor = p;

            isAlreadyLoaded = true;
            return;
        }

        this.name = name;

        getReceptors().add(this);

        for (VariableCreator var : DataAPI.getVariables()) {
            if (!var.isSaveToDatabase()) {
                new ActiveVariable(var, this, getVariableHashedValue(var.getDefaultValue()));
            }
        }

        String query = table.getDatabase().getConnectionType().getInsertQuery(table.getBruteId(), name, getBruteId(), table.getDatabase().getBruteId());
        String query2 = table.getDatabase().getConnectionType().getSelectQuery("id", table.getBruteId(), "WHERE bruteid = '" + getBruteId() + "'", table.getDatabase().getBruteId());
        try {
            try (ResultSet result2 = table.getDatabase().createStatement().executeQuery(query2)) {
                if (!result2.next()) {
                    try (PreparedStatement pst = table.getDatabase().getConnection().prepareStatement(query)) {
                        pst.execute();
                    }
                }

                query = table.getDatabase().getConnectionType().getSelectQuery("*", table.getBruteId(),  "WHERE bruteid = '" + getBruteId() + "'", table.getDatabase().getBruteId());
                try (ResultSet result = table.getDatabase().createStatement().executeQuery(query)) {
                    if (result.next()) {
                        ResultSetMetaData rmtd = result.getMetaData();
                        for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                            if (row > 4) {
                                new InactiveVariable(getBruteId(), rmtd.getColumnName(row), table, result.getObject(row));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            getReceptors().remove(this);
        }

        Bukkit.getPluginManager().callEvent(new ReceptorLoadEvent(!Bukkit.isPrimaryThread(), this));
    }

    public TableCreator getTable() {
        return table;
    }

    public List<ActiveVariable> getVariables() {
        return variables;
    }

    public ActiveVariable getVariable(Plugin plugin, String name) {
        return getVariableFromReceptor(plugin, name, thisReceptor);
    }
    public ActiveVariable getVariable(String name) {
        return getVariableFromReceptor(INSTANCE, name, this);
    }
    public VariableValue getVariableValue(Plugin plugin, String name) {
        return getVariable(plugin, name).getValueModule();
    }
    public VariableValue getVariableValue(String name) {
        return getVariable(INSTANCE, name).getValueModule();
    }

    public boolean hasLoadedBefore() {
        return getTimesLoaded() >= 2;
    }
    public long getTimesLoaded() {
        return getVariableValue("timesLoaded").asLong();
    }
    public void deleteIfHasntLoadedBefore() {
        if (!hasLoadedBefore()) delete();
    }
    public boolean isAlreadyLoaded() {
        return isAlreadyLoaded;
    }

    public void save() {
        StringBuilder query = new StringBuilder();

        for (ActiveVariable var : variables) {
            if (var.getVariable().isSaveToDatabase()) {
                query.append(var.getVariable().getBruteId()).append(" = '").append(getVariableHashedValue(var.getValue())).append("',");
            }
        }

        query.append("last_update = '").append(getDate()).append("'");
        String fQuery = table.getDatabase().getConnectionType().getUpdateQuery(table.getBruteId(), query.toString(), getBruteId(), table.getDatabase().getBruteId());
        table.getDatabase().executeQuery(fQuery);

        try {
            Bukkit.getScheduler().runTask(INSTANCE, () -> {
                ReceptorSaveEvent event = new ReceptorSaveEvent(!Bukkit.isPrimaryThread(), thisReceptor);
                Bukkit.getPluginManager().callEvent(event);
            });
        } catch (IllegalPluginAccessException ignore) {}
    }

    public void unload() {
        unload(true);
    }
    public void unload(boolean save) {
        if (!getReceptors().contains(thisReceptor)) {
            return;
        }

        ReceptorUnloadEvent event = new ReceptorUnloadEvent(!Bukkit.isPrimaryThread(), thisReceptor, save);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (save) {
            save();
        }

        getReceptors().remove(thisReceptor);

        getInactiveVariables().removeIf(in -> in.getOwnerBruteID().equals(getBruteId()));
        getActiveVariables().removeIf(ac -> ac.getReceptor() == thisReceptor);
    }

    public void delete() {
        delete(true);
    }
    public void delete(boolean save) {
        ReceptorDeleteEvent event = new ReceptorDeleteEvent(!Bukkit.isPrimaryThread(), thisReceptor, save);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        try (PreparedStatement pst = table.getDatabase().getConnection().prepareStatement("DELETE FROM '" + table.getBruteId() +  "' WHERE bruteid = '" + getBruteId() + "';")) {
            pst.execute();
            unload(save);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean autoSaveWhenServerClose() {
        return autoSaveOnServerClose;
    }
    public boolean isNew() {
        return getTimesLoaded() == 1;
    }
    public void setAutoSaveWhenServerClose(boolean aswsc) {
        thisReceptor.autoSaveOnServerClose = aswsc;
    }
}
