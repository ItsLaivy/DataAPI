package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.modules.*;
import net.redewhite.lvdataapi.receptors.ActiveVariableLoader;
import net.redewhite.lvdataapi.types.ConnectionType;
import net.redewhite.lvdataapi.types.VariablesType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.redewhite.lvdataapi.modules.VariableReturnModule.getVariableHashedValue;
import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class API {

    public static VariableReceptorModule getVariableReceptor(Plugin plugin, String bruteID, TableCreationModule table) {
        for (VariableReceptorModule receptor : getReceptors()) {
            if (receptor.getTable() == table && receptor.getPlugin() == plugin && receptor.getBruteID().equals(bruteID)) {
                return receptor;
            }
        }
        throw new NullPointerException("Couldn't find any variable receptor with this parameters (Plugin: " + plugin.getName() + ", BruteID: " + bruteID + ", Table: " + table.getName() + ")");
    }
    public static VariableReceptorModule getVariableReceptor(Plugin plugin, Player player, TableCreationModule table) {
        return getVariableReceptor(plugin, player.getUniqueId().toString(), table);
    }
    public static VariableReceptorModule getVariableReceptor(Player player, TableCreationModule table) {
        return getVariableReceptor(INSTANCE, player.getUniqueId().toString(), table);
    }
    public static VariableReceptorModule getVariableReceptor(String bruteID, TableCreationModule table) {
        return getVariableReceptor(INSTANCE, bruteID, table);
    }

    public static boolean isVariableReceptorLoaded(Plugin plugin, String bruteID, TableCreationModule table) {
        try {
            getVariableReceptor(plugin, bruteID, table);
            return true;
        } catch (NullPointerException ignore) {
            return false;
        }
    }
    public static boolean isVariableReceptorLoaded(Plugin plugin, Player player, TableCreationModule table) {
        return isVariableReceptorLoaded(plugin, player.getUniqueId().toString(), table);
    }
    public static boolean isVariableReceptorLoaded(Player player, TableCreationModule table) {
        return isVariableReceptorLoaded(INSTANCE, player.getUniqueId().toString(), table);
    }
    public static boolean isVariableReceptorLoaded(String bruteID, TableCreationModule table) {
        return isVariableReceptorLoaded(INSTANCE, bruteID, table);
    }

    public static boolean isVariableLoaded(Plugin plugin, String name, TableCreationModule table) {
        for (VariableCreationModule variable : getVariables()) {
            if (variable.getBruteID().equals(plugin.getName() + "_" + name)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isVariableLoaded(String name, TableCreationModule table) {
        return isVariableLoaded(INSTANCE, name, table);
    }

    public static VariableReceptorModule getVariableReceptorByBruteID(String bruteID, TableCreationModule table) {
        for (VariableReceptorModule receptor : getReceptors()) {
            if (receptor.getTable() == table && receptor.getBruteID().equals(bruteID)) {
                return receptor;
            }
        }
        throw new NullPointerException("Couldn't find any variable receptor with this parameters (BruteID: " + bruteID + ", Table: " + table.getName() + ")");
    }
    public static VariableReceptorModule getVariableReceptorByBruteID(Player player, TableCreationModule table) {
        return getVariableReceptorByBruteID(player.getUniqueId().toString(), table);
    }

    public static ActiveVariableLoader getVariableFromReceptor(Plugin plugin, String name, VariableReceptorModule receptor) {
        if (plugin == null) plugin = INSTANCE;

        for (ActiveVariableLoader var : receptor.getVariables()) {
            if (var.getVariable().getPlugin() == plugin && var.getVariable().getName().equals(name)) {
                return var;
            }
        }
        throw new NullPointerException("Couldn't find any variable with this parameters (Plugin: " + plugin +  ", Name: " + name + ", Receptor (BruteID): " + receptor.getBruteID() + ")");
    }

    public static VariableReturnModule getVariableValue(Plugin plugin, String name, VariableReceptorModule receptor) {
        if (plugin == null) plugin = INSTANCE;

        ActiveVariableLoader var = getVariableFromReceptor(plugin, name, receptor);
        return new VariableReturnModule(var.getValue());
    }
    public static VariableReturnModule getVariableValue(String name, VariableReceptorModule receptor) {
        return getVariableValue(null, name, receptor);
    }

    public static VariableReturnModule getVariableValue(String name, String bruteID, TableCreationModule table) {
        return getVariableValue(null, name, getVariableReceptorByBruteID(bruteID, table));
    }
    public static VariableReturnModule getVariableValue(Plugin plugin, String name, String bruteID, TableCreationModule table) {
        return getVariableValue(plugin, name, getVariableReceptorByBruteID(bruteID, table));
    }

    public static VariableReturnModule getVariableValue(String name, Player player, TableCreationModule table) {
        return getVariableValue(null, name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table));
    }
    public static VariableReturnModule getVariableValue(Plugin plugin, String name, Player player, TableCreationModule table) {
        return getVariableValue(plugin, name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table));
    }

    public static boolean setVariableValue(String name, VariableReceptorModule receptor, Object value) {
        ActiveVariableLoader var = getVariableFromReceptor(receptor.getPlugin(), name, receptor);
        String newValue = null;

        if (value != null) {
            if (value instanceof ArrayList<?>) {
                ArrayList<Object> array = new ArrayList<>();
                for (Object e : new ArrayList<>(Arrays.asList(((ArrayList<?>) value).toArray()))) {
                    array.add(getVariableHashedValue(e));
                }

                if (array.size() != 0) {
                    newValue = array.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
                }
            } else {
                newValue = getVariableHashedValue(value);
            }
        }

        if (getVariableHashedValue(var.getValue()).equals(newValue)) return false;
        var.setValue(newValue);
        return true;
    }
    public static boolean setVariableValue(String name, String bruteID, TableCreationModule table, Object value) {
        return setVariableValue(name, getVariableReceptorByBruteID(bruteID, table), value);
    }
    public static boolean setVariableValue(String name, Player player, TableCreationModule table, Object value) {
        return setVariableValue(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table), value);
    }

    public static void removeVariableValue(String name, VariableReceptorModule receptor, Object value) {
        ActiveVariableLoader var = getVariableFromReceptor(receptor.getPlugin(), name, receptor);
        if (value == null) return;

        if (var.getValue() == null) {
            var.setValue(value);
            return;
        }

        if (var.getVariable().getType() == VariablesType.ARRAY) {
            List<Object> newList = new ArrayList<>();
            List<Object> addToNewList = new ArrayList<>();
            if (value instanceof ArrayList<?>) {
                addToNewList.addAll(Arrays.asList(((ArrayList<?>) value).toArray()));
            } else {
                addToNewList.add(value);
            }

            for (Object a : addToNewList) {
                boolean success = false;
                for (Object e : var.getValueModule().asList()) {
                    if (!success) {
                        if (e.equals(a)) {
                            success = true;
                            continue;
                        }
                    }
                    newList.add(e);
                }
            }

            String e = newList.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
            if (newList.size() == 0) var.setValue(null);
            else var.setValue(e);
        } else {
            try {
                Double.parseDouble(value.toString());
            } catch (NumberFormatException ignore) {
                try {
                    Double.parseDouble(var.getValue().toString());
                } catch (NumberFormatException ignore2) {
                    throw new IllegalStateException("the variable value isn't a number");
                }
                throw new IllegalStateException("the value isn't a number");
            } try {
                Double.parseDouble(var.getValue().toString());
            } catch (NumberFormatException ignore2) {
                throw new IllegalStateException("the variable value isn't a number");
            }

            double num1 = Double.parseDouble(var.getValue().toString());
            double num2 = Double.parseDouble(value.toString());

            var.setValue(num1 - num2);
        }
    }
    public static void removeVariableValue(String name, String bruteID, TableCreationModule table, Object value) {
        removeVariableValue(name, getVariableReceptorByBruteID(bruteID, table), value);
    }
    public static void removeVariableValue(String name, Player player, TableCreationModule table, Object value) {
        removeVariableValue(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table), value);
    }

    public static void addVariableValue(String name, VariableReceptorModule receptor, Object value) {
        ActiveVariableLoader var = getVariableFromReceptor(receptor.getPlugin(), name, receptor);
        if (value == null) return;

        if (var.getValue() == null) {
            var.setValue(value);
            return;
        }

        if (var.getVariable().getType() == VariablesType.ARRAY) {
            List<Object> newList = var.getValueModule().asList();
            if (value instanceof ArrayList<?>) {
                newList.addAll(Arrays.asList(((ArrayList<?>) value).toArray()));
            } else {
                newList.add(value);
            }

            String e = newList.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
            var.setValue(e);
        } else {
            try {
                Double.parseDouble(value.toString());
            } catch (NumberFormatException ignore) {
                try {
                    Double.parseDouble(var.getValue().toString());
                } catch (NumberFormatException ignore2) {
                    throw new IllegalStateException("the variable value isn't a number");
                }
                throw new IllegalStateException("the value isn't a number");
            } try {
                Double.parseDouble(var.getValue().toString());
            } catch (NumberFormatException ignore2) {
                throw new IllegalStateException("the variable value isn't a number");
            }

            double num1 = Double.parseDouble(var.getValue().toString());
            double num2 = Double.parseDouble(value.toString());

            var.setValue(num1 + num2);
        }
    }
    public static void addVariableValue(String name, String bruteID, TableCreationModule table, Object value) {
        addVariableValue(name, getVariableReceptorByBruteID(bruteID, table), value);
    }
    public static void addVariableValue(String name, Player player, TableCreationModule table, Object value) {
        addVariableValue(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table), value);
    }

    public static boolean isVariableValueNull(String name, VariableReceptorModule receptor) {
        ActiveVariableLoader var = getVariableFromReceptor(receptor.getPlugin(), name, receptor);
        return var.getValue() == null;
    }
    public static boolean isVariableValueNull(String name, String bruteID, TableCreationModule table) {
        return isVariableValueNull(name, getVariableReceptorByBruteID(bruteID, table));
    }
    public static boolean isVariableValueNull(String name, Player player, TableCreationModule table) {
        return isVariableValueNull(name, getVariableReceptorByBruteID(player.getUniqueId().toString(), table));
    }

    public static int getValuesAmountInTable(TableCreationModule table) {
        String query = table.getDatabase().getConnectionType().getSelectQuery();
        query = ConnectionType.replace(query, "*", table.getBruteID(), "", table.getDatabase().getBruteID());
        int returnValue = 0;

        try (ResultSet result2 = table.getDatabase().createStatement().executeQuery(query)) {
            while (result2.next()) returnValue++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return returnValue;
    }

    public static TableCreationModule getDefaultTable(DatabaseCreationModule database) {
        return getTable(INSTANCE, database, "default");
    }
    public static TableCreationModule getDefaultTable(DatabaseCreationModule database, Plugin plugin) {
        return getTable(plugin, database, "default");
    }

    public static TableCreationModule getTable(DatabaseCreationModule database, String name) {
        return getTable(INSTANCE, database, name);
    }
    public static TableCreationModule getTable(Plugin plugin, DatabaseCreationModule database, String name) {
        for (TableCreationModule table : getTables()) {
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
