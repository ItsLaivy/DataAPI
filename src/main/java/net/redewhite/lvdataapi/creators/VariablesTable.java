package net.redewhite.lvdataapi.creators;

import net.redewhite.lvdataapi.events.api.tables.TableCreateEvent;
import net.redewhite.lvdataapi.modules.VariableCreationModule;
import net.redewhite.lvdataapi.database.DatabaseConnection;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import static net.redewhite.lvdataapi.DataAPI.*;

public class VariablesTable {

    private final HashMap<VariableCreationModule, String> variables = new HashMap<>();

    private Boolean successfully_created = false;
    private Plugin plugin;
    private String name;

    public VariablesTable(Plugin plugin, String name) {

        // Exceptions to prevent null values
        if (plugin == null) throw new NullPointerException("Table plugin cannot be null!");
        else if (name == null) throw new NullPointerException("Table name cannot be null!");

        String bruteid = plugin.getName() + "_" + name;

        // Verify if the table's name contains illegal characters
        String[] blocked = "-S,S=S[S]S.S/S*S-S+S;S:S(S)".split("S");
        for (String block : blocked) {
            if (name.contains(block)) {
                getMessage("Table illegal characters error", name, block);
                return;
            }
        }

        // Verify if table is already loaded in the plugin
        for (VariablesTable table : getTables().keySet()) {
            if (table.getTableBruteId().equals(bruteid)) {
                if (table.getPlugin() == plugin) {
                    getMessage("Table already created", name);
                    return;
                }
            }
        }

        // Load table into the plugin
        this.plugin = plugin;
        this.name = name;

        TableCreateEvent event = new TableCreateEvent(this);
        instance.getServer().getPluginManager().callEvent(event);

        this.name = event.getName();

        if (event.isCancelled()) {
            return;
        }

        // Try to create the table in the database
        try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement("CREATE TABLE '" + bruteid + "' ('id' INT AUTO_INCREMENT PRIMARY KEY, 'name' TEXT, 'bruteid' TEXT, 'last_update' TEXT)")) {
            pst.execute();
            getMessage("Table successfully created", name, plugin.getName());
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                getMessage("Table successfully loaded", name, plugin.getName());
            } else {
                if (debug) e.printStackTrace();
                return;
            }
        }

        getTables().put(this, getTableBruteId());
        successfully_created = true;
    }

    public Boolean isSuccessfullyCreated() {
        return successfully_created;
    }
    public Plugin getPlugin() {
        return plugin;
    }
    public String getTableBruteId() {
        return plugin.getName() + "_" + name;
    }
    public String getName() {
        return name;
    }

    public HashMap<VariableCreationModule, String> getVariables() {
        return variables;
    }
}
