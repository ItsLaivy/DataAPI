package net.redewhite.lvdataapi.developers.creators.variables;

import net.redewhite.lvdataapi.modules.TableCreator;
import net.redewhite.lvdataapi.modules.VariableCreator;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.DataAPI.INSTANCE;
import static net.redewhite.lvdataapi.types.VariablesType.LIST;

@SuppressWarnings("unused")
public class ListVariable extends VariableCreator {

    public ListVariable(String name, TableCreator table, Object defaultValue) {
        super(INSTANCE, name, table, defaultValue, true, LIST, true);
    }
    public ListVariable(Plugin plugin, String name, TableCreator table, Object defaultValue) {
        super(plugin, name, table, defaultValue, true, LIST, true);
    }

    public ListVariable(String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(INSTANCE, name, table, defaultValue, saveToDatabase, LIST, true);
    }
    public ListVariable(Plugin plugin, String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(plugin, name, table, defaultValue, saveToDatabase, LIST, true);
    }

}
