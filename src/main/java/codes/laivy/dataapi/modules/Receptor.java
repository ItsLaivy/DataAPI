package codes.laivy.dataapi.modules;

import codes.laivy.dataapi.events.receptors.ReceptorDeleteEvent;
import codes.laivy.dataapi.events.receptors.ReceptorLoadEvent;
import codes.laivy.dataapi.events.receptors.ReceptorSaveEvent;
import codes.laivy.dataapi.events.receptors.ReceptorUnloadEvent;
import codes.laivy.dataapi.modules.receptors.ActiveVariable;
import codes.laivy.dataapi.modules.receptors.InactiveVariable;
import codes.laivy.dataapi.modules.receptors.VariableValue;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static codes.laivy.dataapi.main.DataAPI.plugin;

public class Receptor extends Creator {

    private final Table table;

    private boolean isAlreadyLoaded = false;
    private boolean autoSaveOnServerClose = true;

    private final Receptor thisReceptor;

    private boolean isSuccessfullyCreated;

    public Receptor(@NotNull OfflinePlayer player, @NotNull Table table) {
        this(plugin(), player.getName(), player.getUniqueId().toString(), table);
    }
    public Receptor(@Nullable String name, @NotNull String bruteId, @NotNull Table table) {
        this(plugin(), name, bruteId, table);
    }
    public Receptor(@Nullable Plugin plugin, @Nullable String name, @NotNull String bruteId, @NotNull Table table) {
        super(plugin, name, CreatorType.RECEPTOR_CREATOR, bruteId);
        Validate.notNull(table);

        this.table = table;

        if (!getTable().isSuccessfullyCreated()) {
            throw new IllegalStateException("this table was not created correctly");
        }

        if (plugin().getReceptors().get(getTable()).containsKey(getBruteId())) {
            Receptor p = plugin().getReceptors().get(getTable()).get(getBruteId());

            autoSaveOnServerClose = p.autoSaveOnServerClose;
            this.name = p.name;
            isAlreadyLoaded = true;

            thisReceptor = p;

            isSuccessfullyCreated = true;
            return;
        } else {
            thisReceptor = this;
        }

        this.name = getName();
        isSuccessfullyCreated = true;

        load();
    }

    public void load() {
        plugin().getActiveVariables().get(getTable()).put(getBruteId(), new HashMap<>());
        plugin().getInactiveVariables().get(getTable()).put(getBruteId(), new HashMap<>());

        plugin().getReceptors().get(getTable()).put(getBruteId(), this);

        for (Map.Entry<String, Variable> map : plugin().getVariables().get(getTable()).entrySet()) {
            Variable var = map.getValue();

            if (!var.isSaveToDatabase()) {
                new ActiveVariable(var, this, var.getDefaultValue());
            }
        }

        String query = getTable().getDatabase().getConnectionType().getInsertQuery(getTable().getBruteId(), getName(), getBruteId(), getTable().getDatabase().getBruteId());
        String query2 = getTable().getDatabase().getConnectionType().getSelectQuery("id", getTable().getBruteId(), "WHERE bruteid = '" + getBruteId() + "'", getTable().getDatabase().getBruteId());
        try {
            try (ResultSet result2 = getTable().getDatabase().createStatement().executeQuery(query2)) {
                if (!result2.next()) {
                    try (PreparedStatement pst = getTable().getDatabase().getConnection().prepareStatement(query)) {
                        pst.execute();
                    }
                }

                query = getTable().getDatabase().getConnectionType().getSelectQuery("*", getTable().getBruteId(),  "WHERE bruteid = '" + getBruteId() + "'", getTable().getDatabase().getBruteId());
                try (ResultSet result = getTable().getDatabase().createStatement().executeQuery(query)) {
                    if (result.next()) {
                        ResultSetMetaData rmtd = result.getMetaData();
                        for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                            if (row > 4) {
                                new InactiveVariable(this, rmtd.getColumnName(row), result.getString(row));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin().getReceptors().get(getTable()).remove(getBruteId());

            plugin().getActiveVariables().get(this.getTable()).remove(this.getBruteId());
            plugin().getInactiveVariables().get(this.getTable()).remove(this.getBruteId());

            isSuccessfullyCreated = false;
            return;
        }

        ReceptorLoadEvent event = new ReceptorLoadEvent(!Bukkit.isPrimaryThread(), this);
        Bukkit.getPluginManager().callEvent(event);
    }

    @NotNull
    public Table getTable() {
        return table;
    }

    @NotNull
    public ActiveVariable getVariable(@NotNull Plugin plugin, @NotNull String name) {
        Validate.notNull(plugin);
        Validate.notNull(name);

        if (plugin().getActiveVariables().get(thisReceptor.getTable()).get(thisReceptor.getBruteId()).containsKey(Creator.getBruteIdOf(CreatorType.VARIABLE_CREATOR, plugin, name, null))) {
            return plugin().getActiveVariables().get(thisReceptor.getTable()).get(thisReceptor.getBruteId()).get(Creator.getBruteIdOf(CreatorType.VARIABLE_CREATOR, plugin, name, null));
        }
        throw new NullPointerException("Cannot find that variable in the receptor \"" + thisReceptor.getBruteId() + "\" with that parameters: (Plugin: " + plugin.getName() + ", Name: \"" + name + "\")");
    }
    @NotNull
    public ActiveVariable getVariable(@NotNull String name) {
        return getVariable(plugin(), name);
    }

    public boolean hasLoadedBefore() {
        return getLoads() >= 2;
    }
    public long getLoads() {
        return new VariableValue<Long>(thisReceptor, "timesLoaded").getValue();
    }
    public boolean isAlreadyLoaded() {
        return isAlreadyLoaded;
    }

    public boolean isSuccessfullyCreated() {
        if (!isLoaded()) {
            throw new IllegalStateException("Thats receptor hasn't loaded");
        }

        return isSuccessfullyCreated;
    }

    public void save() {
        if (!isLoaded()) {
            throw new IllegalStateException("Thats receptor hasn't loaded");
        }

        StringBuilder query = new StringBuilder();
        for (Map. Entry<String, ActiveVariable> map : plugin().getActiveVariables().get(thisReceptor.getTable()).get(thisReceptor.getBruteId()).entrySet()) {
            ActiveVariable var = map.getValue();

            if (var.getVariable().isSaveToDatabase()) {
                query.append(var.getVariable().getBruteId()).append(" = '").append(Variable.byteArrayToString(var.getVariable().getVariableHashedValue(var.getValue()))).append("',");
            }
        }

        query.append("last_update = '").append(plugin().getDate()).append("'");
        String fQuery = getTable().getDatabase().getConnectionType().getUpdateQuery(getTable().getBruteId(), query.toString(), getBruteId(), getTable().getDatabase().getBruteId());
        getTable().getDatabase().executeQuery(fQuery);

        ReceptorSaveEvent event = new ReceptorSaveEvent(!Bukkit.isPrimaryThread(), this);
        Bukkit.getPluginManager().callEvent(event);
    }

    public boolean isLoaded() {
        return plugin().getReceptors().get(getTable()).containsKey(thisReceptor.getBruteId());
    }

    public void unload() {
        unload(true);
    }
    public void unload(boolean save) {
        if (!plugin().getReceptors().get(getTable()).containsKey(thisReceptor.getBruteId())) {
            return;
        }

        if (save) {
            save();
        }

        plugin().getReceptors().get(getTable()).remove(thisReceptor.getBruteId());

        plugin().getInactiveVariables().get(thisReceptor.getTable()).remove(thisReceptor.getBruteId());
        plugin().getActiveVariables().get(thisReceptor.getTable()).remove(thisReceptor.getBruteId());

        ReceptorUnloadEvent event = new ReceptorUnloadEvent(!Bukkit.isPrimaryThread(), this, save);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void delete() {
        try (PreparedStatement pst = getTable().getDatabase().getConnection().prepareStatement(getTable().getDatabase().getConnectionType().getDeleteReceptorQuery(getTable().getDatabase().getBruteId(), getTable().getBruteId(), getBruteId()))) {
            pst.execute();
            unload(false);

            ReceptorDeleteEvent event = new ReceptorDeleteEvent(!Bukkit.isPrimaryThread(), this);
            Bukkit.getPluginManager().callEvent(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean autoSaveWhenServerClose() {
        return autoSaveOnServerClose;
    }
    public boolean isNew() {
        return getLoads() == 1;
    }
    public void setAutoSaveWhenServerClose(boolean aswsc) {
        thisReceptor.autoSaveOnServerClose = aswsc;
    }
}
