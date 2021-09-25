package net.redewhite.lvdataapi.creators;

import net.redewhite.lvdataapi.modules.VariableCreationModule;
import net.redewhite.lvdataapi.DataAPI;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class ArrayVariable {

    public ArrayVariable(Plugin plugin, String name, VariablesTable table, Object default_value) {
        new VariableCreationModule(plugin, name, default_value, DataAPI.variableDataType.ARRAY, table);
    }

}
