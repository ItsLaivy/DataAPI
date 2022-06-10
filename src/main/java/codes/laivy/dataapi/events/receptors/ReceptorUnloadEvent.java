package codes.laivy.dataapi.events.receptors;

import codes.laivy.dataapi.modules.Receptor;
import org.bukkit.event.Cancellable;

public class ReceptorUnloadEvent extends ReceptorEvent implements Cancellable {

    private boolean cancelled = false;
    private final boolean saving;

    public ReceptorUnloadEvent(boolean isAsync, Receptor receptor, boolean saving) {
        super(isAsync, receptor);
        this.saving = saving;
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
