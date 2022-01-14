package net.redewhite.lvdataapi.developers.creators.variables;

import net.redewhite.lvdataapi.modules.TableCreator;
import net.redewhite.lvdataapi.modules.VariableCreator;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.DataAPI.INSTANCE;
import static net.redewhite.lvdataapi.types.VariablesType.PAIR;

public class PairVariable extends VariableCreator {

    public PairVariable(String name, TableCreator table, Object defaultValue) {
        super(INSTANCE, name, table, defaultValue, true, PAIR, true);
    }
    public PairVariable(Plugin plugin, String name, TableCreator table, Object defaultValue) {
        super(plugin, name, table, defaultValue, true, PAIR, true);
    }

    public PairVariable(String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(INSTANCE, name, table, defaultValue, saveToDatabase, PAIR, true);
    }
    public PairVariable(Plugin plugin, String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(plugin, name, table, defaultValue, saveToDatabase, PAIR, true);
    }

}
