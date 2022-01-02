package net.redewhite.lvdataapi.developers.events.receptors;

import net.redewhite.lvdataapi.modules.ReceptorCreator;
import org.bukkit.event.Cancellable;

public class ReceptorUnloadEvent extends ReceptorEvent implements Cancellable {

    private boolean cancelled = false;

    public ReceptorUnloadEvent(ReceptorCreator receptor) {
        super(receptor);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
