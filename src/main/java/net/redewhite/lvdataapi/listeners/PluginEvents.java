package net.redewhite.lvdataapi.listeners;

import net.redewhite.lvdataapi.DataAPI;
import net.redewhite.lvdataapi.developers.API;
import net.redewhite.lvdataapi.developers.events.databases.DatabaseLoadEvent;
import net.redewhite.lvdataapi.developers.events.receptors.ReceptorLoadEvent;
import net.redewhite.lvdataapi.developers.events.tables.TableLoadEvent;
import net.redewhite.lvdataapi.developers.events.variables.VariableLoadEvent;
import net.redewhite.lvdataapi.modules.VariableCreator;
import net.redewhite.lvdataapi.types.VariablesType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings("unused")
public class PluginEvents implements Listener {

    private static int databasesRecord = 0;
    private static int tablesRecord = 0;
    private static int variablesRecord = 0;
    private static int receptorsRecord = 0;

    @EventHandler
    private void tableLoad(TableLoadEvent e) {
        new VariableCreator(DataAPI.INSTANCE, "timesLoaded", e.getTable(), 0, true, VariablesType.NORMAL, false);
        tablesRecord++;
    }
    @EventHandler
    private void databaseLoad(DatabaseLoadEvent e) {
        databasesRecord++;
    }
    @EventHandler
    private void variableLoad(VariableLoadEvent e) {
        variablesRecord++;
    }


    @EventHandler
    private void receptorLoad(ReceptorLoadEvent e) {
        API.getVariableValue("timesLoaded", e.getReceptor()).addValue(1);
        receptorsRecord++;
    }

    public static int getDatabasesRecord() {
        return databasesRecord;
    }

    public static int getTablesRecord() {
        return tablesRecord;
    }

    public static int getVariablesRecord() {
        return variablesRecord;
    }

    public static int getReceptorsRecord() {
        return receptorsRecord;
    }

}
