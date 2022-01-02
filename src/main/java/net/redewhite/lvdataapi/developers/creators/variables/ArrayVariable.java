package net.redewhite.lvdataapi.developers.creators.variables;

import net.redewhite.lvdataapi.modules.TableCreator;
import net.redewhite.lvdataapi.modules.VariableCreator;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.types.VariablesType.ARRAY;
import static net.redewhite.lvdataapi.DataAPI.INSTANCE;

@SuppressWarnings("unused")
public class ArrayVariable extends VariableCreator {

    public ArrayVariable(String name, TableCreator table, Object defaultValue) {
        super(INSTANCE, name, table, defaultValue, true, ARRAY);
    }
    public ArrayVariable(Plugin plugin, String name, TableCreator table, Object defaultValue) {
        super(plugin, name, table, defaultValue, true, ARRAY);
    }

    public ArrayVariable(String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(INSTANCE, name, table, defaultValue, saveToDatabase, ARRAY);
    }
    public ArrayVariable(Plugin plugin, String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(plugin, name, table, defaultValue, saveToDatabase, ARRAY);
    }

}
