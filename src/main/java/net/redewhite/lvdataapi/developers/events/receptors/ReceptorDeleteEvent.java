package net.redewhite.lvdataapi.developers.events.receptors;

import net.redewhite.lvdataapi.modules.ReceptorCreator;

public class ReceptorDeleteEvent extends ReceptorUnloadEvent {
    public ReceptorDeleteEvent(boolean isAsync, ReceptorCreator receptor, boolean saving) {
        super(isAsync, receptor, saving);
    }
}
