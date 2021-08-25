package net.redewhite.lvdataapi.variables;

import net.redewhite.lvdataapi.utils.VariableCreationController;
import net.redewhite.lvdataapi.LvDataAPI;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class TempVariable {

    public TempVariable(Plugin plugin, String name, Object default_value, Boolean textvariable) {
        new VariableCreationController(plugin, name, default_value, LvDataAPI.variableType.TEMPORARY, textvariable, false);
    }
    public TempVariable(Plugin plugin, String name, Object default_value) {
        new VariableCreationController(plugin, name, default_value, LvDataAPI.variableType.TEMPORARY, false, false);
    }

}
