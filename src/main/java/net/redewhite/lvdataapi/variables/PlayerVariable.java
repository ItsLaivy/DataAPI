package net.redewhite.lvdataapi.variables;

import net.redewhite.lvdataapi.LvDataPlugin.variableType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.LvDataPlugin.playerapi;
import static net.redewhite.lvdataapi.LvDataPlugin.variableType.*;

public class PlayerVariable {

    private final Player player;
    private final String name;
    private final String varname;
    private Object value;
    private final Object variable;
    private final Plugin plugin;
    private final variableType vartype;

    public PlayerVariable(Player player, Plugin plugin, String name, Object value, variableType type, Object variable) {
        this.player = player;
        this.name = name;
        this.value = value;
        this.plugin = plugin;
        this.variable = variable;
        this.vartype = type;

        if (type == ARRAY) {
            this.varname = plugin.getName() + "_ARRAYLIST_" + name;
        } else if (type == TEMPORARY) {
            this.varname = plugin.getName() + "_TEMPORARY_" + name;
        } else {
            this.varname = plugin.getName() + "_" + name;
        }

        for (PlayerVariable api : playerapi.keySet()) {
            if (api.getPlayer() == player) {
                if (api.getVariableName().equalsIgnoreCase(varname)) {
                    return;
                }
            }
        }

        playerapi.put(this, player);

    }

    public Player getPlayer() {
        return player;
    }
    public String getName() {
        return name;
    }
    public Object getValue() {
        return value;
    }
    public Plugin getPlugin() {
        return plugin;
    }
    public String getVariableName() {
        return varname;
    }
    public Object getVariable() {
        return variable;
    }
    public variableType getVariableType() {
        return vartype;
    }
    public void setValue(Object value) {
        this.value = value;
    }
}
