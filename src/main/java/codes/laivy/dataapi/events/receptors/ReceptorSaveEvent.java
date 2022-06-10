package codes.laivy.dataapi.events.receptors;

import codes.laivy.dataapi.modules.Receptor;

public class ReceptorSaveEvent extends ReceptorEvent {
    public ReceptorSaveEvent(boolean isAsync, Receptor receptor) {
        super(isAsync, receptor);
    }
}
