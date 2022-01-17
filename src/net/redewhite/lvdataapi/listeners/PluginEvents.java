package net.redewhite.lvdataapi.listeners;

import net.redewhite.lvdataapi.DataAPI;
import net.redewhite.lvdataapi.developers.API;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorLoadEvent;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorUnloadEvent;
import net.redewhite.lvdataapi.developers.events.tables.TableLoadEvent;
import net.redewhite.lvdataapi.modules.VariableCreator;
import net.redewhite.lvdataapi.types.VariablesType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings("unused")
public class PluginEvents implements Listener {

    @EventHandler
    private void tableLoad(TableLoadEvent e) {
        new VariableCreator(DataAPI.INSTANCE, "hasLoadedBefore", e.getTable(), false, true, VariablesType.NORMAL, false);
        new VariableCreator(DataAPI.INSTANCE, "timesLoaded", e.getTable(), 0, true, VariablesType.NORMAL, false);
    }

    @EventHandler
    private void receptorLoad(ReceptorLoadEvent e) {
        API.getVariableValue("timesLoaded", e.getReceptor()).addValue(1);
    }

    @EventHandler
    private void receptorUnload(ReceptorUnloadEvent e) {
        API.getVariableValue("hasLoadedBefore", e.getReceptor()).setValue(true);
    }

}
