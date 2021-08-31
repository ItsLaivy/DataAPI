package net.redewhite.lvdataapi.events.api.receptors;

import net.redewhite.lvdataapi.creators.VariablesTable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class VariableReceptorRegisterEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;

    private VariablesTable table;
    private String bruteId;
    private String name;

    public VariableReceptorRegisterEvent(String bruteId, String name, VariablesTable table) {
        this.bruteId = bruteId;
        this.table = table;
        this.name = name;
    }

    public VariablesTable getTable() {
        return table;
    }
    public String getBruteId() {
        return bruteId;
    }
    public String getName() {
        return name;
    }

    public void setTable(VariablesTable table) {
        this.table = table;
    }
    public void setBruteId(String bruteId) {
        this.bruteId = bruteId;
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
