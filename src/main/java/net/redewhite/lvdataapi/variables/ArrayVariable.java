package net.redewhite.lvdataapi.variables;

import net.redewhite.lvdataapi.utils.VariableCreationController;
import net.redewhite.lvdataapi.LvDataAPI;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ArrayVariable {

    public ArrayVariable(Plugin plugin, String name, ArrayList<?> default_value, Boolean textvariable) {
        new VariableCreationController(plugin, name, default_value, LvDataAPI.variableType.ARRAY, textvariable);
    }
    public ArrayVariable(Plugin plugin, String name, ArrayList<?> default_value) {
        new VariableCreationController(plugin, name, default_value, LvDataAPI.variableType.ARRAY, false);
    }

}
