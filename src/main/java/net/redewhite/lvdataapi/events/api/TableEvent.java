package net.redewhite.lvdataapi.events.api;

import net.redewhite.lvdataapi.creators.VariablesTable;
import org.bukkit.event.Event;

public abstract class TableEvent extends Event {

    private final VariablesTable table;

    public TableEvent(VariablesTable table) {
        this.table = table;
    }

    public VariablesTable getTable() {
        return table;
    }

}
