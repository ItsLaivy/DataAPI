package net.redewhite.lvdataapi.variables;

import net.redewhite.lvdataapi.utils.VariableCreationController;
import net.redewhite.lvdataapi.LvDataAPI;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class Variable {

    public Variable(Plugin plugin, String name, Object default_value, Boolean textvariable) {
        new VariableCreationController(plugin, name, default_value, LvDataAPI.variableType.NORMAL, textvariable);
    }
    public Variable(Plugin plugin, String name, Object default_value) {
        new VariableCreationController(plugin, name, default_value, LvDataAPI.variableType.NORMAL, false);
    }

}
