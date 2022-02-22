package net.redewhite.lvdataapi.developers.events.databases;

import net.redewhite.lvdataapi.modules.Database;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DatabaseLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Database database;

    public DatabaseLoadEvent(boolean isAsync, Database database) {
        super(isAsync);
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }

}
