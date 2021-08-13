package net.redewhite.lvdataapi.events;

import net.redewhite.lvdataapi.LvDataPlugin.variableType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

@Deprecated
public class VariableCreateEvent extends Event implements Cancellable {

    private Boolean cancelled;
    private Boolean success;
    private final Plugin plugin;
    private final String name;
    private final variableType type;
    private final Object defaultvalue;
    private static final HandlerList handlers = new HandlerList();

    public VariableCreateEvent(Plugin plugin, String name, variableType type, Object defaultvalue) {
        this.plugin = plugin;
        this.name = name;
        this.type = type;
        this.defaultvalue = defaultvalue;
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

    public Object getDefaultValue() {
        return defaultvalue;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public variableType getType() {
        return type;
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
