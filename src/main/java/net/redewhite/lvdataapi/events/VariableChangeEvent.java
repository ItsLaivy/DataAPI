package net.redewhite.lvdataapi.events;

import net.redewhite.lvdataapi.LvDataPlugin.variableChangeErrorTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class VariableChangeEvent extends Event implements Cancellable {

    private Boolean cancelled;
    private Boolean success;
    private final Plugin plugin;
    private final String name;
    private final Object variable;
    private final Object changevalue;
    private final Object beforevalue;
    private final Player player;
    private variableChangeErrorTypes error = null;
    private static final HandlerList handlers = new HandlerList();

    public VariableChangeEvent(Plugin plugin, Player player, String name, Object variable, Object changevalue, Object beforevalue) {
        this.plugin = plugin;
        this.name = name;
        this.variable = variable;
        this.changevalue = changevalue;
        this.beforevalue = beforevalue;
        this.player = player;
        this.cancelled = false;
        this.success = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Object getBeforeValue() {
        return beforevalue;
    }
    public Object getChangeValue() {
        return changevalue;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public variableChangeErrorTypes getError() {
        return error;
    }

    public void setError(variableChangeErrorTypes error) {
        this.error = error;
        this.success = false;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public Object getVariable() {
        return variable;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
