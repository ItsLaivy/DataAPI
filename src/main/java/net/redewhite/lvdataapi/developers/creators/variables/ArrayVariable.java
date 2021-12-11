package net.redewhite.lvdataapi.developers.creators.variables;

import net.redewhite.lvdataapi.modules.TableCreationModule;
import net.redewhite.lvdataapi.modules.VariableCreationModule;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.types.VariablesType.ARRAY;
import static net.redewhite.lvdataapi.DataAPI.INSTANCE;

@SuppressWarnings("unused")
public class ArrayVariable extends VariableCreationModule {

    public ArrayVariable(String name, TableCreationModule table, Object defaultValue) {
        super(INSTANCE, name, table, defaultValue, true, ARRAY);
    }
    public ArrayVariable(Plugin plugin, String name, TableCreationModule table, Object defaultValue) {
        super(plugin, name, table, defaultValue, true, ARRAY);
    }

    public ArrayVariable(String name, TableCreationModule table, Object defaultValue, Boolean saveToDatabase) {
        super(INSTANCE, name, table, defaultValue, saveToDatabase, ARRAY);
    }
    public ArrayVariable(Plugin plugin, String name, TableCreationModule table, Object defaultValue, Boolean saveToDatabase) {
        super(plugin, name, table, defaultValue, saveToDatabase, ARRAY);
    }

}
