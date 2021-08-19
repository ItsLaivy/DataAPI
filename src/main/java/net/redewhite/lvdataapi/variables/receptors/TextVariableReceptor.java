package net.redewhite.lvdataapi.variables.receptors;

import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.developers.TextVariablesAPI.loadTextType;
import static net.redewhite.lvdataapi.LvDataAPI.*;

public class TextVariableReceptor {

    private final String varname;
    private final Plugin plugin;
    private final String name;

    public TextVariableReceptor(Plugin plugin, String name) {
        if (plugin == null) throw new NullPointerException("Variable plugin cannot be null!");
        else if (name == null) throw new NullPointerException("Variable name cannot be null!");

        this.varname = plugin.getName() + "_TEXT_" + name;
        this.name = name;
        this.plugin = plugin;

        if (name.contains("-")) {
            broadcastColoredMessage("§4Text §cvariable '§4" + name + "§c' couldn't be created because it has illegal characters ('§4-§c')");
            return;
        }

        getTextVariablesNames().put(this, name);
        loadTextType(this);
    }

    public String getVariableName() {
        return varname;
    }
    public Plugin getPlugin() {
        return plugin;
    }
    public String getName() {
        return name;
    }
}