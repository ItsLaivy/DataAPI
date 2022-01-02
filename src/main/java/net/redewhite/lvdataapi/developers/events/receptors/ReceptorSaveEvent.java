package net.redewhite.lvdataapi.developers.events.receptors;

import net.redewhite.lvdataapi.modules.ReceptorCreator;

public class ReceptorSaveEvent extends ReceptorEvent {
    public ReceptorSaveEvent(ReceptorCreator receptor) {
        super(receptor);
    }
}
