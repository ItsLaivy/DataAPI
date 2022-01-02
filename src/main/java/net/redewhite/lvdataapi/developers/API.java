package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.modules.*;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import net.redewhite.lvdataapi.types.ConnectionType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;

import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class API {

    public static ReceptorCreator getVariableReceptor(Plugin plugin, String bruteID, TableCreator table) {
        for (ReceptorCreator receptor : getReceptors()) {
            if (receptor.getTable() == table && receptor.getPlugin() == plugin && receptor.getBruteID().equals(bruteID)) {
                return receptor;
            }
        }
        throw new NullPointerException("Couldn't find any variable receptor with this parameters (Plugin: " + plugin.getName() + ", BruteID: " + bruteID + ", Table: " + table.getName() + ")");
    }
    public static ReceptorCreator getVariableReceptor(Plugin plugin, Player player, TableCreator table) {
        return getVariableReceptor(plugin, player.getUniqueId().toString(), table);
    }
    public static ReceptorCreator getVariableReceptor(Player player, TableCreator table) {
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
    public static boolean isVariableReceptorLoaded(Plugin plugin, Player player, TableCreator table) {
        return isVariableReceptorLoaded(plugin, player.getUniqueId().toString(), table);
    }
    public static boolean isVariableReceptorLoaded(Player player, TableCreator table) {
        return isVariableReceptorLoaded(INSTANCE, player.getUniqueId().toString(), table);
    }
    public static boolean isVariableReceptorLoaded(String bruteID, TableCreator table) {
        return isVariableReceptorLoaded(INSTANCE, bruteID, table);
    }

    public static boolean isVariableLoaded(Plugin plugin, String name, TableCreator table) {
        for (VariableCreator variable : getVariables()) {
            if (variable.getBruteID().equals(plugin.getName() + "_" + name)) {
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
            if (receptor.getTable() == table && receptor.getBruteID().equals(bruteID)) {
                return receptor;
            }
        }
        throw new NullPointerException("Couldn't find any variable receptor with this parameters (BruteID: " + bruteID + ", Table: " + table.getName() + ")");
    }
    public static ReceptorCreator getVariableReceptorByBruteID(Player player, TableCreator table) {
        return getVariableReceptorByBruteID(player.getUniqueId().toString(), table);
    }

    public static ActiveVariable getVariableFromReceptor(Plugin plugin, String name, ReceptorCreator receptor) {
        if (plugin == null) plugin = INSTANCE;

        for (ActiveVariable var : receptor.getVariables()) {
            if (var.getVariable().getPlugin() == plugin && var.getVariable().getName().equals(name)) {
                return var;
            }
        }
        throw new NullPointerException("Couldn't find any variable with this parameters (Plugin: " + plugin +  ", Name: " + name + ", Receptor (BruteID): " + receptor.getBruteID() + ")");
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

    public static VariableValue getVariableValue(String name, Player player, TableCreator table) {
        return getVariableValue(null, name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table));
    }
    public static VariableValue getVariableValue(Plugin plugin, String name, Player player, TableCreator table) {
        return getVariableValue(plugin, name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table));
    }

    public static void setVariableValue(String name, ReceptorCreator receptor, Object value) {
        getVariableValue(name, receptor).setValue(value);
    }
    public static void setVariableValue(String name, String bruteID, TableCreator table, Object value) {
        setVariableValue(name, getVariableReceptorByBruteID(bruteID, table), value);
    }
    public static void setVariableValue(String name, Player player, TableCreator table, Object value) {
        setVariableValue(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table), value);
    }

    public static void removeVariableValue(String name, ReceptorCreator receptor, Object value) {
        getVariableValue(name, receptor).removeValue(value);
    }
    public static void removeVariableValue(String name, String bruteID, TableCreator table, Object value) {
        removeVariableValue(name, getVariableReceptorByBruteID(bruteID, table), value);
    }
    public static void removeVariableValue(String name, Player player, TableCreator table, Object value) {
        removeVariableValue(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table), value);
    }

    public static void addVariableValue(String name, ReceptorCreator receptor, Object value) {
        getVariableValue(name, receptor).addValue(value);
    }
    public static void addVariableValue(String name, String bruteID, TableCreator table, Object value) {
        addVariableValue(name, getVariableReceptorByBruteID(bruteID, table), value);
    }
    public static void addVariableValue(String name, Player player, TableCreator table, Object value) {
        addVariableValue(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table), value);
    }

    public static boolean isVariableValueNull(String name, ReceptorCreator receptor) {
        ActiveVariable var = getVariableFromReceptor(receptor.getPlugin(), name, receptor);
        return var.getValue() == null;
    }
    public static boolean isVariableValueNull(String name, String bruteID, TableCreator table) {
        return isVariableValueNull(name, getVariableReceptorByBruteID(bruteID, table));
    }
    public static boolean isVariableValueNull(String name, Player player, TableCreator table) {
        return isVariableValueNull(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table));
    }

    public static int getValuesAmountInTable(TableCreator table) {
        String query = table.getDatabase().getConnectionType().getSelectQuery("*", table.getBruteID(), "", table.getDatabase().getBruteID());
        int returnValue = 0;

        try (ResultSet result2 = table.getDatabase().createStatement().executeQuery(query)) {
            while (result2.next()) returnValue++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return returnValue;
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
            if (table.getDatabase().getBruteID().equals(database.getBruteID())) {
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
