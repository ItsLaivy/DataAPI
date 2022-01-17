package net.redewhite.lvdataapi.developers.events.tables;

import net.redewhite.lvdataapi.modules.TableCreator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TableLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final TableCreator table;

    public TableLoadEvent(boolean isAsync, TableCreator table) {
        super(isAsync);
        this.table = table;
    }

    public TableCreator getTable() {
        return table;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }
}
