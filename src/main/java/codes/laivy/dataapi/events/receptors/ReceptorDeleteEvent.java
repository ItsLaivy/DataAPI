package codes.laivy.dataapi.events.receptors;

import codes.laivy.dataapi.modules.Receptor;

public class ReceptorDeleteEvent extends ReceptorUnloadEvent {
    public ReceptorDeleteEvent(boolean isAsync, Receptor receptor) {
        super(isAsync, receptor, false);
    }
}
