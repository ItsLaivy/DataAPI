package codes.laivy.dataapi.listeners;

import codes.laivy.dataapi.events.databases.DatabaseLoadEvent;
import codes.laivy.dataapi.events.receptors.ReceptorLoadEvent;
import codes.laivy.dataapi.events.receptors.ReceptorUnloadEvent;
import codes.laivy.dataapi.events.tables.TableLoadEvent;
import codes.laivy.dataapi.events.variables.VariableLoadEvent;
import codes.laivy.dataapi.main.Metrics;
import codes.laivy.dataapi.modules.Variable;
import codes.laivy.dataapi.modules.receptors.VariableValue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static codes.laivy.dataapi.main.DataAPI.plugin;

@SuppressWarnings("unused")
public class BukkitEvents implements Listener {

    private static int databasesRecord = 0;
    private static int tablesRecord = 0;
    private static int variablesRecord = 0;
    private static int receptorsRecord = 0;

    @EventHandler
    private void tableLoad(TableLoadEvent e) {
        new Variable(plugin(), "timesLoaded", e.getTable(), 0L, true, false);
        tablesRecord++;
        plugin().getMetrics().addCustomChart(new Metrics.SingleLineChart("tables", BukkitEvents::getTablesRecord));
    }
    @EventHandler
    private void databaseLoad(DatabaseLoadEvent e) {
        databasesRecord++;
        plugin().getMetrics().addCustomChart(new Metrics.SingleLineChart("databases", BukkitEvents::getDatabasesRecord));
    }

    @EventHandler
    private void variableLoad(VariableLoadEvent e) {
        variablesRecord++;
        plugin().getMetrics().addCustomChart(new Metrics.SingleLineChart("variables", BukkitEvents::getVariablesRecord));
    }


    @EventHandler
    private void receptorLoad(ReceptorLoadEvent e) {
        VariableValue<Long> variableValue = new VariableValue<>(e.getReceptor(), "timesLoaded");
        variableValue.setValue(variableValue.getValue() + 1);

        receptorsRecord++;
        plugin().getMetrics().addCustomChart(new Metrics.SingleLineChart("receptors", BukkitEvents::getReceptorsRecord));
    }
    @EventHandler
    private void receptorUnload(ReceptorUnloadEvent e) {
        receptorsRecord--;
        plugin().getMetrics().addCustomChart(new Metrics.SingleLineChart("receptors", BukkitEvents::getReceptorsRecord));
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
