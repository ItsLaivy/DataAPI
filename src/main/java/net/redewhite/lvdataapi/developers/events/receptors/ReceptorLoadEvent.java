package net.redewhite.lvdataapi.developers.events.receptors;

import net.redewhite.lvdataapi.modules.ReceptorCreator;

public class ReceptorLoadEvent extends ReceptorEvent {
    public ReceptorLoadEvent(ReceptorCreator receptor) {
        super(receptor);
    }
}
