package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.events.api.receptors.VariableReceptorDeleteEvent;
import net.redewhite.lvdataapi.events.api.variables.VariableValueChangeEvent;
import net.redewhite.lvdataapi.loaders.ActiveVariableLoader;
import net.redewhite.lvdataapi.database.DatabaseConnection;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import net.redewhite.lvdataapi.creators.VariablesTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Arrays;
import java.util.List;

import static net.redewhite.lvdataapi.DataAPI.variableDataType.*;
import static net.redewhite.lvdataapi.developers.AdvancedAPI.*;
import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class EasyAPI {

    /**
     *
     * @since 2.0
     * @param player  The player that will try to register
     * @param table the table created with VariablesTable.class
     * @return returns true if the register will be successfully <br>
     *         returns false if operation fails (error) <br>
     *         returns null if user is already registered
     */
    public static Boolean playerTypeDatabaseRegister(Player player, VariablesTable table) {
        return databaseRegister(player.getUniqueId().toString(), player.getName(), table);
    }

    /**
     *
     * @since 2.0
     * @param name  The name of receptor (Example: player's name)
     * @param bruteid  The id or uuid of receptor (Example: player's uuid or receptor's brute id)
     * @param table the table created with VariablesTable.class
     * @return returns true if the register will be successfully <br>
     *         returns false if operation fails (error) <br>
     *         returns null if user is already registered
     */
    public static Boolean textTypeDatabaseRegister(String name, String bruteid, VariablesTable table) {
        return databaseRegister(bruteid, name, table);
    }

    /**
     *
     * Get the number of variables in player's database
     *
     * @since 2.0
     * @param player The player that will get values
     * @param table the table created with VariablesTable.class
     * @return returns null if player's database doesn't have any variable in the database or if get process fail (error)
     *
     */
    public static Integer getNumberOfDatabaseVariables(Player player, VariablesTable table) {
        return getDatabaseVariablesAmountOf(player.getUniqueId().toString(), table);
    }

    /**
     *
     * Get the number of variables in receptor's database
     *
     * @since 2.0
     * @param bruteid  The id or uuid of receptor (Example: player's uuid or receptor's brute id)
     * @param table the table created with VariablesTable.class
     * @return returns null if receptor's database doesn't have any variable in the database or if get process fail (error)
     *
     */
    public static Integer getNumberOfDatabaseVariables(String bruteid, VariablesTable table) {
        return getDatabaseVariablesAmountOf(bruteid, table);
    }

    /**
     *
     * Unload variable values of a certain receptor
     *
     * @since 2.0
     * @param player The player that will be unloaded
     * @param table the table created with VariablesTable.class
     * @return returns true if unload process run successfully <br>
     *         returns false if unload process fail
     *
     */
    public static Boolean receptorTypeDatabaseUnload(Plugin plugin, Player player, VariablesTable table) {
        return databaseUnload(getVariableReceptor(plugin, player, table), table);
    }

    /**
     *
     * Load variable values of a certain receptor at the database
     *
     * @since 2.0
     * @param player The player that will be loaded
     * @param table the table created with VariablesTable.class
     * @return returns true if load process run successfully <br>
     *         returns false if load process fail
     *
     */
    public static Boolean receptorTypeDatabaseLoad(Plugin plugin, Player player, VariablesTable table) {
        return databaseLoad(getVariableReceptor(plugin, player, table), table);
    }

    /**
     *
     * Verify if player is loaded (with variables)
     *
     * @since 2.0
     * @param player The player that will be verified if is load
     * @return returns true receptor/player has variables loaded <br>
     *         returns false if no variable is found
     *
     */
    public static Boolean isLoaded(Player player) {
        return AdvancedAPI.isLoaded(player.getUniqueId().toString());
    }

    /*
    ---------------------
    Receptor API section
    ---------------------
    */

    public static VariableReceptor getVariableReceptor(Plugin plugin, Player player, VariablesTable table) {
        return getVariableReceptor(plugin, player.getUniqueId().toString(), table);
    }

    /**
     *
     * Gets a variable receptor
     *
     * @since 2.0
     * @param bruteid  The id or uuid of receptor (Example: player's uuid or receptor's brute id)
     * @param plugin  The plugin of the receptor
     * @param table the table created with VariablesTable.class
     * @return returns a variable receptor
     *
     */
    public static VariableReceptor getVariableReceptor(Plugin plugin, String bruteid, VariablesTable table) {
        for (VariableReceptor receptor : getVariableReceptors().keySet()) {
            if (receptor.getTable() == table) {
                if (receptor.getNameBruteId().equals(bruteid)) {
                    if (receptor.getPlugin() == plugin) {
                        return receptor;
                    }
                }
            }
        }
        throw new NullPointerException("Couldn't find any variable receptor with this parameters");
    }

    public static Boolean deleteVariableReceptor(Plugin plugin, String bruteid, VariablesTable table) {
        return deleteVariableReceptor(getVariableReceptor(plugin, bruteid, table));
    }

    /**
     *
     * Delete a variable receptor
     *
     * @since 2.0
     * @param receptor The receptor that will be deleted
     * @return returns true if receptor has been successfully deleted <br>returns false if an error occurred.
     *
     */
    public static Boolean deleteVariableReceptor(VariableReceptor receptor) {

        VariableReceptorDeleteEvent event = new VariableReceptorDeleteEvent(receptor);
        instance.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled())  {
            try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement("DELETE FROM '" + receptor.getTable().getTableBruteId() +  "' WHERE bruteid = '" + receptor.getNameBruteId() + "';")) {
                pst.execute();
                receptor.unload();
                return true;
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
                getMessage("Receptor variable delete unknown error", receptor.getName());
            }
        }
        return false;
    }

    /*
    ---------------------
    Variables API section
    ---------------------
    */

    /**
     *
     * Gets a pure variable information
     *
     * @since 2.0
     * @param name The name of the variable
     * @param receptor The receptor of the variable
     * @return returns the variable if exists
     *
     */
    public static ActiveVariableLoader getVariable(String name, VariableReceptor receptor) {
        for (ActiveVariableLoader var : getActiveVariables().keySet()) {
            if (var.getVariable().getTable() == receptor.getTable()) {
                if (var.getVariable().getPlugin() == receptor.getPlugin()) {
                    if (var.getVariable().getName().equals(name)) {
                        if (var.getOwnerBruteId().equals(receptor.getNameBruteId())) {
                            return var;
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Couldn't find any variable with this parameters");
    }

    public static ActiveVariableLoader getVariable(Plugin plugin, String name, String bruteid, VariablesTable table) {
        return getVariable(name, getVariableReceptor(plugin, bruteid, table));
    }
    public static ActiveVariableLoader getVariable(Plugin plugin, String name, Player player, VariablesTable table) {
        return getVariable(name, getVariableReceptor(plugin, player.getUniqueId().toString(), table));
    }

    /**
     *
     * Gets a receptor's variable values
     *
     * @since 2.0
     * @param name The name of the variable
     * @param receptor The receptor of the variable
     * @return returns the value of the variable
     *
     */
    public static Object getVariableValue(String name, VariableReceptor receptor) {
        ActiveVariableLoader var = getVariable(name, receptor);
        return var.getValue();
    }

    public static Object getVariableValue(Plugin plugin, String name, String bruteid, VariablesTable table) {
        return getVariableValue(name, getVariableReceptor(plugin, bruteid, table));
    }
    public static Object getVariableValue(Plugin plugin, String name, Player player, VariablesTable table) {
        return getVariableValue(name, getVariableReceptor(plugin, player.getUniqueId().toString(), table));
    }

    /**
     *
     * Sets a receptor's variable value
     *
     * @since 2.0
     * @param name The name of the variable
     * @param receptor The receptor of the variable
     * @param value the new value of the variable
     * @return returns the value of the variable
     *
     */
    public static Boolean setVariableValue(String name, VariableReceptor receptor, Object value) {
        ActiveVariableLoader var = getVariable(name, receptor);
        VariableValueChangeEvent event = new VariableValueChangeEvent(var, receptor, value, var.getValue());
        instance.getServer().getPluginManager().callEvent(event);

        value = event.getNewValue();

        if (!event.isCancelled()) {
            if (value == null) {
                var.setValue(null);
                return true;
            }

            if (var.getVariable().getDataType() == NORMAL || var.getVariable().getDataType() == TEMPORARY) {
                var.setValue(getVariableHashedValue(value));
                return true;
            } else if (var.getVariable().getDataType() == ARRAY) {
                ArrayList<Object> array = new ArrayList<>();
                if (value instanceof ArrayList<?>) {
                    for (Object e : new ArrayList<>(Arrays.asList(((ArrayList<?>) value).toArray()))) {
                        array.add(getVariableHashedValue(e));
                    }

                    if (array.size() == 0) var.setValue(null);
                    else var.setValue(array.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", ""));

                    return true;
                } else {
                    throw new NullPointerException("Thats value isn't a arraylist!");
                }
            }
        }
        return false;
    }

    public static Boolean setVariableValue(Plugin plugin, String name, String bruteid, VariablesTable table, Object value) {
        return setVariableValue(name, getVariableReceptor(plugin, bruteid, table), value);
    }
    public static Boolean setVariableValue(Plugin plugin, String name, Player player, VariablesTable table, Object value) {
        return setVariableValue(name, getVariableReceptor(plugin, player.getUniqueId().toString(), table), value);
    }

    /**
     *
     * Remove a value from a array or a normal variable integer value.
     *
     * @since 2.1
     * @param name The name of the variable
     * @param receptor The receptor of the variable
     * @param value the value that will remove the variable
     * @return returns the value of the variable
     *
     */
    public static Boolean removeVariableValue(String name, VariableReceptor receptor, Object value) {
        boolean returnValue = false;

        ActiveVariableLoader activeVar = getVariable(name, receptor);
        if (activeVar.getVariable().getDataType() == NORMAL || activeVar.getVariable().getDataType() == TEMPORARY) {
            try {
                try {
                    long val1 = Long.parseLong(getVariableValue(name, receptor).toString());
                    long val2 = Long.parseLong(value.toString());

                    activeVar.setValue(val1 - val2);
                    returnValue = true;
                } catch (NumberFormatException ignore) {
                    throw new IllegalArgumentException("this value isn't a number.");
                }
            } catch (NumberFormatException ignore) {
                throw new IllegalArgumentException("this variable isn't a number variable!");
            }
        } else if (activeVar.getVariable().getDataType() == ARRAY) {
            ArrayList<Object> array = new ArrayList<>();
            List<String> list = getVariableArray(name, receptor);

            if (value instanceof ArrayList<?>) {
                for (Object e : new ArrayList<>(Arrays.asList(((ArrayList<?>) value).toArray()))) {
                    array.add(getVariableHashedValue(e));
                }

                for (Object e : array) if (!list.contains(e.toString())) {
                    list.remove(e.toString());
                }

                if (list.size() == 0) activeVar.setValue(null);
                else activeVar.setValue(list.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", ""));
            } else {
                list.remove(getVariableHashedValue(value));
                activeVar.setValue(list.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", ""));
            }
            returnValue = true;
        }

        return returnValue;
    }
    public static Boolean removeVariableValue(Plugin plugin, String name, String bruteid, VariablesTable table, Object value) {
        return removeVariableValue(name, getVariableReceptor(plugin, bruteid, table), value);
    }
    public static Boolean removeVariableValue(Plugin plugin, String name, Player player, VariablesTable table, Object value) {
        return removeVariableValue(name, getVariableReceptor(plugin, player.getUniqueId().toString(), table), value);
    }

    /**
     *
     * Add a value to a array or a normal variable integer value.
     *
     * @since 2.1
     * @param name The name of the variable
     * @param receptor The receptor of the variable
     * @param value the value that will add to the variable
     * @return returns the value of the variable
     *
     */
    public static Boolean addVariableValue(String name, VariableReceptor receptor, Object value) {
        boolean returnValue = false;

        ActiveVariableLoader activeVar = getVariable(name, receptor);
        if (activeVar.getVariable().getDataType() == NORMAL || activeVar.getVariable().getDataType() == TEMPORARY) {
            try {
                try {
                    long val1 = Long.parseLong(getVariableValue(name, receptor).toString());
                    long val2 = Long.parseLong(value.toString());

                    activeVar.setValue(val1 + val2);
                    returnValue = true;
                } catch (NumberFormatException ignore) {
                    throw new IllegalArgumentException("this value isn't a number.");
                }
            } catch (NumberFormatException ignore) {
                throw new IllegalArgumentException("this variable isn't a number variable!");
            }
        } else if (activeVar.getVariable().getDataType() == ARRAY) {
            List<String> list = getVariableArray(name, receptor);

            if (value instanceof ArrayList<?>) {
                for (Object e : new ArrayList<>(Arrays.asList(((ArrayList<?>) value).toArray()))) {
                    if (!list.contains(getVariableHashedValue(e))) list.add(getVariableHashedValue(e));
                }

                if (list.size() == 0) activeVar.setValue(null);
                else activeVar.setValue(list.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", ""));

            } else {
                if (!list.contains(getVariableHashedValue(value))) list.add(getVariableHashedValue(value));
                activeVar.setValue(list.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", ""));
            }
            returnValue = true;
        }

        return returnValue;
    }
    public static Boolean addVariableValue(Plugin plugin, String name, String bruteid, VariablesTable table, Object value) {
        return addVariableValue(name, getVariableReceptor(plugin, bruteid, table), value);
    }
    public static Boolean addVariableValue(Plugin plugin, String name, Player player, VariablesTable table, Object value) {
        return addVariableValue(name, getVariableReceptor(plugin, player.getUniqueId().toString(), table), value);
    }

    /**
     *
     * Gets a foreach variable value (only for ArrayVariables)
     *
     * @since 2.0
     * @param name The name of the array variable
     * @param receptor The receptor of the variable
     * @return returns the value (that can be iterate) of an array variable
     *
     */
    public static ArrayList<String> getVariableArray(String name, VariableReceptor receptor) {
        ActiveVariableLoader var = getVariable(name, receptor);

        ArrayList<String> array = new ArrayList<>();
        if (var.getVariable().getDataType() == ARRAY) {
            if (var.getValue() != null) {
                String[] split = var.getValue().toString().split("<SPLIT!>");
                for (String e : split) {
                    if (getVariableUnhashedValue(e) != null) {
                        array.add(Objects.requireNonNull(getVariableUnhashedValue(e)));
                    } else {
                        return new ArrayList<>();
                    }
                }
            }
        }
        return array;
    }

    public static ArrayList<String> getVariableArray(Plugin plugin, String name, Player player, VariablesTable table) {
        return getVariableArray(name, getVariableReceptor(plugin, player.getUniqueId().toString(), table));
    }
    public static ArrayList<String> getVariableArray(Plugin plugin, String name, String bruteId, VariablesTable table) {
        return getVariableArray(name, getVariableReceptor(plugin, bruteId, table));
    }

    /**
     *
     * Check if variable value is null
     *
     * @since 2.0
     * @param variable The variable
     * @return returns true if variable value is null<br>returns false if variable value isn't null
     *
     */
    public static Boolean isVariableValueNull(ActiveVariableLoader variable) {
        if (variable != null) {
            return variable.getValue() == null;
        }
        throw new NullPointerException("Couldn't find this active variable.");
    }
    public static Boolean isVariableValueNull(Plugin plugin, String name, String bruteId, VariablesTable table) {
        return isVariableValueNull(getVariable(name, getVariableReceptor(plugin, bruteId, table)));
    }
    public static Boolean isVariableValueNull(String name, VariableReceptor receptor) {
        return isVariableValueNull(getVariable(name, receptor));
    }
    public static Boolean isVariableValueNull(Plugin plugin, String name, Player player, VariablesTable table) {
        return isVariableValueNull(getVariable(name, getVariableReceptor(plugin, player.getUniqueId().toString(), table)));
    }
}
