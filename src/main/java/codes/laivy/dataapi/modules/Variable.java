package codes.laivy.dataapi.modules;

import codes.laivy.dataapi.events.variables.VariableCreateEvent;
import codes.laivy.dataapi.events.variables.VariableEvent;
import codes.laivy.dataapi.events.variables.VariableLoadEvent;
import codes.laivy.dataapi.modules.receptors.ActiveVariable;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import static codes.laivy.dataapi.main.DataAPI.plugin;

public class Variable extends Creator {

    private final Table table;

    private final Serializable defaultValue;

    private final boolean saveToDatabase;
    private boolean isSuccessfullyCreated = false;

    public Variable(@Nullable String name, @NotNull Table table, @Nullable Serializable defaultValue) {
        this(plugin(), name, table, defaultValue, true, true);
    }
    public Variable(@Nullable String name, @NotNull Table table, @Nullable Serializable defaultValue, boolean saveToDatabase) {
        this(plugin(), name, table, defaultValue, saveToDatabase, true);
    }

    public Variable(@Nullable Plugin plugin, @Nullable String name, @NotNull Table table, @Nullable Serializable defaultValue, boolean saveToDatabase) {
        this(plugin, name, table, defaultValue, saveToDatabase, true);
    }
    public Variable(@Nullable Plugin plugin, @Nullable String name, @NotNull Table table, @Nullable Serializable defaultValue, boolean saveToDatabase, boolean messages) {
        super(plugin, name, CreatorType.VARIABLE_CREATOR);
        Validate.notNull(table, "Table cannot be null");

        this.table = table;
        this.saveToDatabase = saveToDatabase;
        this.defaultValue = defaultValue;

        if (!getTable().isSuccessfullyCreated()) {
            throw new IllegalStateException("this table was not created correctly.");
        }

        if (plugin().getVariables().get(getTable()).containsKey(getBruteId())) {
            throw new IllegalStateException("That variable already exists");
        }

        if (!plugin().getConfig().getBoolean("variable messages")) {
            messages = false;
        }

        VariableEvent event;
        if (this.saveToDatabase) {
            String query = getTable().getDatabase().getConnectionType().getVariableCreationQuery(getTable().getBruteId(), getBruteId(), byteArrayToString(getVariableHashedValue(defaultValue)), getTable().getDatabase().getBruteId());
            try (PreparedStatement pst = getTable().getDatabase().getConnection().prepareStatement(query)) {
                pst.execute();
                plugin().broadcastColoredMessage("&aVariable&2 '" + getName() + "' (" + getPlugin().getName() + ") &asuccessfully created.", messages);
                event = new VariableCreateEvent(!Bukkit.isPrimaryThread(), this);
            } catch (SQLException e) {
                if (e.getMessage().contains("uplicate column name")) {
                    plugin().broadcastColoredMessage("&aVariable&2 '" + getName() + "' (" + getPlugin().getName() + ") &asuccessfully loaded.", messages);
                    event = new VariableLoadEvent(!Bukkit.isPrimaryThread(), this);
                } else {
                    e.printStackTrace();
                    plugin().broadcastColoredMessage("&cException trying to create variable&c: " + getBruteId() + " of table: " + getTable().getBruteId() + " (Database: " + getTable().getDatabase().getBruteId() + ", type: " + getTable().getDatabase().getConnectionType() + "), full query: \"§5" + query + "§c\"");
                    return;
                }
            }
        } else {
            plugin().broadcastColoredMessage("&bTemporary &aVariable&2 '" + getName() + "' (" + getPlugin().getName() + ") &asuccessfully loaded.", messages);
            event = new VariableLoadEvent(!Bukkit.isPrimaryThread(), this);
        }

        isSuccessfullyCreated = true;
        plugin().getVariables().get(getTable()).put(getBruteId(), this);

        Bukkit.getPluginManager().callEvent(event);

        for (Map.Entry<String, Receptor> map : plugin().getReceptors().get(getTable()).entrySet()) {
            Receptor receptor = map.getValue();
            new ActiveVariable(this, receptor, defaultValue);
        }
    }

    public boolean isSuccessfullyCreated() {
        return isSuccessfullyCreated;
    }

    public boolean isSaveToDatabase() {
        return saveToDatabase;
    }

    @Nullable
    public Serializable getDefaultValue() {
        return defaultValue;
    }

    @NotNull
    public Table getTable() {
        return table;
    }
    
    public byte[] getVariableHashedValue(@Nullable Serializable value) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(b);
            stream.writeObject(value);
            return b.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    @NotNull
    public Serializable getVariableUnHashedValue(byte[] value) {
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(value);
            ObjectInputStream stream = new ObjectInputStream(b);
            return (Serializable) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    @NotNull
    public static String byteArrayToString(byte[] byteArray) {
        return Arrays.toString(byteArray).replace("[", "").replace("]", "").replace(", ", "/");
    }
    public static byte[] stringToByteArray(@NotNull String byteArray) {
        Validate.notNull(byteArray);

        String[] split = byteArray.split("/");
        byte[] b = new byte[split.length];
             
        int row = 0;
        for (String e : split) {
            b[row] = Byte.parseByte(e);
            row++;
        }
        
        return b;
    }

}
