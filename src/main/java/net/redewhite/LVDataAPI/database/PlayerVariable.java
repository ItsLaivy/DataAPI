package net.redewhite.lvdataapi.database;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.LvDataPlugin.playerapi;

public class PlayerVariable {

    private final Player player;
    private final String name;
    private final String varname;
    private Object value;
    private final Plugin plugin;

    public PlayerVariable(Player player, Plugin plugin, String name, Object value, String type) {
        this.player = player;
        this.name = name;
        this.value = value;
        this.plugin = plugin;

        if (type.equalsIgnoreCase("VARIABLE")) {
            this.varname = plugin.getName() + "_" + getName();
        } else {
            this.varname = plugin.getName() + "_ARRAYLIST_" + getName();
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
    public void setValue(Object value) {
        this.value = value;
    }
}
