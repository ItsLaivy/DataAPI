package codes.laivy.dataapi.events.receptors;

import codes.laivy.dataapi.modules.Receptor;

public class ReceptorLoadEvent extends ReceptorEvent {
    public ReceptorLoadEvent(boolean isAsync, Receptor receptor) {
        super(isAsync, receptor);
    }
}
