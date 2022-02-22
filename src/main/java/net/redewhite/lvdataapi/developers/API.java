package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.modules.*;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class API {

    public static ReceptorCreator getVariableReceptor(Plugin plugin, String bruteID, TableCreator table) {
        for (ReceptorCreator receptor : getReceptors()) {
            if (receptor.getTable() == table && receptor.getPlugin() == plugin && receptor.getBruteId().equals(bruteID)) {
                return receptor;
            }
        }
        throw new NullPointerException("Couldn't find any variable receptor with this parameters (Plugin: " + plugin.getName() + ", BruteID: " + bruteID + ", Table: " + table.getName() + ")");
    }
    public static ReceptorCreator getVariableReceptor(Plugin plugin, OfflinePlayer player, TableCreator table) {
        return getVariableReceptor(plugin, player.getUniqueId().toString(), table);
    }
    public static ReceptorCreator getVariableReceptor(OfflinePlayer player, TableCreator table) {
        return getVariableReceptor(INSTANCE, player.getUniqueId().toString(), table);
    }
    public static ReceptorCreator getVariableReceptor(String bruteID, TableCreator table) {
        return getVariableReceptor(INSTANCE, bruteID, table);
    }

    public static boolean isVariableReceptorLoaded(Plugin plugin, String bruteID, TableCreator table) {
        try {
            getVariableReceptor(plugin, bruteID, table);
            return true;
        } catch (NullPointerException ignore) {
            return false;
        }
    }
    public static boolean isVariableReceptorLoaded(Plugin plugin, OfflinePlayer player, TableCreator table) {
        return isVariableReceptorLoaded(plugin, player.getUniqueId().toString(), table);
    }
    public static boolean isVariableReceptorLoaded(OfflinePlayer player, TableCreator table) {
        return isVariableReceptorLoaded(INSTANCE, player.getUniqueId().toString(), table);
    }
    public static boolean isVariableReceptorLoaded(String bruteID, TableCreator table) {
        return isVariableReceptorLoaded(INSTANCE, bruteID, table);
    }

    public static boolean isVariableLoaded(Plugin plugin, String name, TableCreator table) {
        for (VariableCreator variable : getVariables()) {
            if (variable.getBruteId().equals(plugin.getName() + "_" + name)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isVariableLoaded(String name, TableCreator table) {
        return isVariableLoaded(INSTANCE, name, table);
    }

    public static ReceptorCreator getVariableReceptorByBruteID(String bruteID, TableCreator table) {
        for (ReceptorCreator receptor : getReceptors()) {
            if (receptor.getTable() == table && receptor.getBruteId().equals(bruteID)) {
                return receptor;
            }
        }
        throw new NullPointerException("Couldn't find any variable receptor with this parameters (BruteID: " + bruteID + ", Table: " + table.getName() + ")");
    }
    public static ReceptorCreator getVariableReceptorByBruteID(OfflinePlayer player, TableCreator table) {
        return getVariableReceptorByBruteID(player.getUniqueId().toString(), table);
    }

    public static ActiveVariable getVariableFromReceptor(Plugin plugin, String name, ReceptorCreator receptor) {
        if (plugin == null) plugin = INSTANCE;

        for (ActiveVariable var : receptor.getVariables()) {
            if (var.getVariable().getPlugin() == plugin && var.getVariable().getName().equals(name)) {
                return var;
            }
        }
        throw new NullPointerException("Couldn't find any variable with this parameters (Plugin: " + plugin +  ", Name: " + name + ", Receptor (BruteID): " + receptor.getBruteId() + ")");
    }

    public static VariableValue getVariableValue(Plugin plugin, String name, ReceptorCreator receptor) {
        if (plugin == null) plugin = INSTANCE;

        ActiveVariable var = getVariableFromReceptor(plugin, name, receptor);
        return new VariableValue(var);
    }
    public static VariableValue getVariableValue(String name, ReceptorCreator receptor) {
        return getVariableValue(null, name, receptor);
    }

    public static VariableValue getVariableValue(String name, String bruteID, TableCreator table) {
        return getVariableValue(null, name, getVariableReceptorByBruteID(bruteID, table));
    }
    public static VariableValue getVariableValue(Plugin plugin, String name, String bruteID, TableCreator table) {
        return getVariableValue(plugin, name, getVariableReceptorByBruteID(bruteID, table));
    }

    public static VariableValue getVariableValue(String name, OfflinePlayer player, TableCreator table) {
        return getVariableValue(null, name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table));
    }
    public static VariableValue getVariableValue(Plugin plugin, String name, OfflinePlayer player, TableCreator table) {
        return getVariableValue(plugin, name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table));
    }

    public static void setVariableValue(String name, ReceptorCreator receptor, Object value) {
        getVariableValue(name, receptor).setValue(value);
    }
    public static void setVariableValue(String name, String bruteID, TableCreator table, Object value) {
        setVariableValue(name, getVariableReceptorByBruteID(bruteID, table), value);
    }
    public static void setVariableValue(String name, OfflinePlayer player, TableCreator table, Object value) {
        setVariableValue(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table), value);
    }

    public static void removeVariableValue(String name, ReceptorCreator receptor, Object value) {
        getVariableValue(name, receptor).removeValue(value);
    }
    public static void removeVariableValue(String name, String bruteID, TableCreator table, Object value) {
        removeVariableValue(name, getVariableReceptorByBruteID(bruteID, table), value);
    }
    public static void removeVariableValue(String name, OfflinePlayer player, TableCreator table, Object value) {
        removeVariableValue(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table), value);
    }

    public static void addVariableValue(String name, ReceptorCreator receptor, Object value) {
        getVariableValue(name, receptor).addValue(value);
    }
    public static void addVariableValue(String name, String bruteID, TableCreator table, Object value) {
        addVariableValue(name, getVariableReceptorByBruteID(bruteID, table), value);
    }
    public static void addVariableValue(String name, OfflinePlayer player, TableCreator table, Object value) {
        addVariableValue(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table), value);
    }

    public static boolean isVariableValueNull(String name, ReceptorCreator receptor) {
        ActiveVariable var = getVariableFromReceptor(receptor.getPlugin(), name, receptor);
        return var.getValue() == null;
    }
    public static boolean isVariableValueNull(String name, String bruteID, TableCreator table) {
        return isVariableValueNull(name, getVariableReceptorByBruteID(bruteID, table));
    }
    public static boolean isVariableValueNull(String name, OfflinePlayer player, TableCreator table) {
        return isVariableValueNull(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table));
    }

    public static List<String> getAllValuesInTable(TableCreator table) {
        List<String> list = new ArrayList<>();

        String query = table.getDatabase().getConnectionType().getSelectQuery("*", table.getBruteId(), "", table.getDatabase().getBruteId());
        try (ResultSet result2 = table.getDatabase().createStatement().executeQuery(query)) {
            while (result2.next()) {
                list.add(result2.getString("bruteid"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }
    public static long getValuesAmountInTable(TableCreator table) {
        return getAllValuesInTable(table).size();
    }

    public static TableCreator getDefaultTable(Database database) {
        return getTable(INSTANCE, database, "default");
    }
    public static TableCreator getDefaultTable(Database database, Plugin plugin) {
        return getTable(plugin, database, "default");
    }

    public static TableCreator getTable(Database database, String name) {
        return getTable(INSTANCE, database, name);
    }
    public static TableCreator getTable(Plugin plugin, Database database, String name) {
        for (TableCreator table : getTables()) {
            if (table.getDatabase().getBruteId().equals(database.getBruteId())) {
                if (table.getName().equals(name)) {
                    if (table.getPlugin() == plugin) {
                        return table;
                    }
                }
            }

        }
        throw new NullPointerException("Cannot find any table with that specs (Name: " + name + ", Plugin: " + plugin.getName() + ", Database: " + database.getName() + ").");
    }

}
