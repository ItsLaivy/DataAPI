package net.redewhite.LVDataAPI.Database;

import net.redewhite.LVDataAPI.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Players {

    private final Player player;
    private final String name;
    private final String varname;
    private Object value;
    private final Plugin plugin;

    public Players(Player player, Plugin plugin, String name, Object value) {
        this.player = player;
        this.name = name;
        this.value = value;
        this.plugin = plugin;
        this.varname = plugin.getName() + "_" + getName();

        Main.playerapi.put(this, player);
    }

    public Player getPlayer() {
        return player;
    }
    public String getName() {
        return name;
    }
    public Object getValue() { return value; }
    public Plugin getPlugin() { return plugin; }
    public String getVariableName() { return varname; }
    public void setValue(Object value) { this.value = value; }
}
