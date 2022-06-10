package codes.laivy.dataapi.events.tables;

import codes.laivy.dataapi.modules.Table;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TableLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Table table;

    public TableLoadEvent(boolean isAsync, Table table) {
        super(isAsync);
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }
}
