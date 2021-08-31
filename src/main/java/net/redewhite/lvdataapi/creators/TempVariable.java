package net.redewhite.lvdataapi.creators;

import net.redewhite.lvdataapi.DataAPI;
import net.redewhite.lvdataapi.modules.VariableCreationModule;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class TempVariable {

    public TempVariable(Plugin plugin, String name, VariablesTable table, Object default_value) {
        new VariableCreationModule(plugin, name, default_value, DataAPI.variableDataType.TEMPORARY, table);
    }

}