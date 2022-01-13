package net.redewhite.lvdataapi.developers.events.receptors;

import net.redewhite.lvdataapi.modules.ReceptorCreator;
import org.bukkit.event.Cancellable;

public class ReceptorUnloadEvent extends ReceptorEvent implements Cancellable {

    private boolean cancelled = false;
    private final boolean saving;

    public ReceptorUnloadEvent(ReceptorCreator receptor, boolean isSaving) {
        super(receptor);
        this.saving = isSaving;
    }

    public boolean isSaving() {
        return saving;
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
