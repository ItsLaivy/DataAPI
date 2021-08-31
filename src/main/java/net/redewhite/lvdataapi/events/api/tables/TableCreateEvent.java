package net.redewhite.lvdataapi.events.api.tables;

import net.redewhite.lvdataapi.creators.VariablesTable;
import net.redewhite.lvdataapi.events.api.TableEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class TableCreateEvent extends TableEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private String name;

    public TableCreateEvent(VariablesTable table) {
        super(table);
        this.name = table.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCancelled() {
        return cancel;
    }
    public void setCancelled(boolean cancelled) {
        cancel = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
