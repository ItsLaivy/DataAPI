package net.redewhite.lvdataapi.variables;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.LvDataPlugin.*;
import static net.redewhite.lvdataapi.LvDataPlugin.variableType.TEMPORARY;

public class TempVariable {

    private final String name;
    private final String varname;
    private final Object value;
    private final Plugin plugin;

    public TempVariable(Plugin plugin, String name, Object value) {

        this.plugin = plugin;
        this.name = name;
        this.value = value;
        this.varname = plugin.getName() + "_TEMPORARY_" + name;

        for (TempVariable var : tempvariables.keySet()) {
            if (var.getVariableName().equalsIgnoreCase(varname)) {
                return;
            }
        }

        if (name.contains("-")) {
            broadcastColoredMessage("§cVariable '§4" + name + "§c' couldn't be created because it has illegal characters ('§4-§c')");
            return;
        }

        tempvariables.put(this, varname);
        broadcastColoredMessage("§aSuccessfully parsed temporary variable '§2" + name + "§a' of the plugin '§2" + plugin.getName() + "§a'.");

        for (Player player : instance.getServer().getOnlinePlayers()) {
            new PlayerVariable(player, plugin, name, value, TEMPORARY);
        }

    }

    public String getName() {
        return name;
    }
    public String getVariableName() {
        return varname;
    }
    public Plugin getPlugin() {
        return plugin;
    }
    public variableType getVariableType() {
        return TEMPORARY;
    }
    public Object getValue() {
        return value;
    }
}
