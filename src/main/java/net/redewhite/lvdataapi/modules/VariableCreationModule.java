package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.events.api.variables.VariableModuleCreateEvent;
import net.redewhite.lvdataapi.loaders.ActiveVariableLoader;
import net.redewhite.lvdataapi.database.DatabaseConnection;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import net.redewhite.lvdataapi.DataAPI.variableDataType;
import net.redewhite.lvdataapi.creators.VariablesTable;
import net.redewhite.lvdataapi.developers.AdvancedAPI;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.redewhite.lvdataapi.DataAPI.variableDataType.*;
import static net.redewhite.lvdataapi.DataAPI.*;

public class VariableCreationModule {

    private Boolean successfully_created = true;
    private variableDataType data_type;
    private Object default_value;
    private VariablesTable table;
    private Plugin plugin;
    private String name;

    public VariableCreationModule(Plugin plugin, String name, Object default_value, variableDataType data_type, VariablesTable table) {
        if (plugin == null) throw new NullPointerException("Variable plugin cannot be null!");
        else if (name == null) throw new NullPointerException("Variable name cannot be null!");
        else if (data_type == null) throw new NullPointerException("Variable data type type cannot be null!");
        else if (table == null) throw new NullPointerException("Variable table type cannot be null!");

        String prefix = "variable";
        String prefix_caps = "Variable";

        String message_caps = null;
        String message_error_caps = null;
        if (data_type == ARRAY) {
            message_caps = "&eArray" + " &a" + prefix;
            message_error_caps = "&eArray" + " &c" + prefix;
        } else if (data_type == TEMPORARY) {
            message_caps = "&bTemporary" + " &a" + prefix;
            message_error_caps = "&bTemporary" + " &c" + prefix;
        } else if (data_type == NORMAL) {
            message_caps = "&a" + prefix_caps;
            message_error_caps = "&c" + prefix_caps;
        }

        String[] blocked = "-S,S=S[S]S.S/S*S-S+S;S:".split("S");
        for (String block : blocked) {
            if (name.contains(block)) {
                getMessage("Normal variable illegal characters error", message_error_caps, name, block);
                return;
            }
        }

        if (!table.isSuccessfullyCreated()) {
            getMessage("Normal variable invalid table error", message_error_caps, name);
            return;
        }

        if (default_value instanceof ArrayList) {
            ArrayList<String> array = new ArrayList<>();
            for (Object e : ((ArrayList<?>) default_value).toArray()) {
                array.add(AdvancedAPI.getVariableHashedValue(e));
            }
            this.default_value = array.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
            if (this.default_value.toString().equals("")) this.default_value = AdvancedAPI.getVariableHashedValue(null);
        } else {
            this.default_value = AdvancedAPI.getVariableHashedValue(default_value);
        }

        this.data_type = data_type;
        this.plugin = plugin;
        this.table = table;
        this.name = name;

        VariableModuleCreateEvent event = new VariableModuleCreateEvent(this);
        instance.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (data_type == ARRAY || data_type == NORMAL) {

                int parse = 0;
                try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement("ALTER TABLE '" + table.getTableBruteId() + "' ADD COLUMN " + getVariableBruteId() + " TEXT DEFAULT '" + this.default_value + "';")) {
                    pst.execute();
                    parse = 1;
                } catch (SQLException e) {
                    if (e.getMessage().contains("uplicate column name")) {
                        parse = 2;
                    } else {
                        if (debug) e.printStackTrace();
                    }
                }

                if (parse == 0) {
                    getMessage("Normal variable unknown error", message_error_caps, name);
                } else if (parse == 1) {
                    getMessage("Normal variable created", message_caps, name, plugin.getName());
                    for (VariableReceptor receptor : getVariableReceptors().keySet()) {
                        receptor.createVariable(this);
                    }
                } else {
                    getMessage("Normal variable loaded", message_caps, name, plugin.getName());
                    for (VariableReceptor receptor : getVariableReceptors().keySet()) {
                        new ActiveVariableLoader(receptor, AdvancedAPI.getInactiveVariable(getVariableBruteId(), receptor), this);
                    }
                }

            }

            this.successfully_created = true;
            table.getVariables().put(this, getVariableBruteId());
            getVariables().put(this, getVariableBruteId());
        }
    }

    @SuppressWarnings("unused")
    public Boolean isSuccessfullyCreated() {
        return successfully_created;
    }
    public variableDataType getDataType() {
        return data_type;
    }
    public Object getDefaultValue() {
        return default_value;
    }
    public VariablesTable getTable() {
        return table;
    }
    public Plugin getPlugin() {
        return plugin;
    }
    public String getName() {
        return name;
    }
    public String getVariableBruteId() {
        return plugin.getName() + "_" + data_type.name() + "_" + name;
    }
}
