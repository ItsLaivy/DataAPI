package net.redewhite.lvdataapi.developers.events.receptors;

import net.redewhite.lvdataapi.modules.ReceptorCreator;

public class ReceptorDeleteEvent extends ReceptorUnloadEvent {

    public ReceptorDeleteEvent(ReceptorCreator receptor, boolean isSaving) {
        super(receptor, isSaving);
    }

}
