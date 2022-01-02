package net.redewhite.lvdataapi.developers.creators.variables;

import net.redewhite.lvdataapi.modules.TableCreator;
import net.redewhite.lvdataapi.modules.VariableCreator;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.types.VariablesType.NORMAL;
import static net.redewhite.lvdataapi.DataAPI.INSTANCE;

@SuppressWarnings("unused")
public class Variable extends VariableCreator {

    public Variable(String name, TableCreator table, Object defaultValue) {
        super(INSTANCE, name, table, defaultValue, true, NORMAL, true);
    }
    public Variable(Plugin plugin, String name, TableCreator table, Object defaultValue) {
        super(plugin, name, table, defaultValue, true, NORMAL, true);
    }

    public Variable(String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(INSTANCE, name, table, defaultValue, saveToDatabase, NORMAL, true);
    }
    public Variable(Plugin plugin, String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(plugin, name, table, defaultValue, saveToDatabase, NORMAL, true);
    }

}
