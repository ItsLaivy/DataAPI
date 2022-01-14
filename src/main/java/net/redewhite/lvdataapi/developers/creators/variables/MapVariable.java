package net.redewhite.lvdataapi.developers.creators.variables;

import net.redewhite.lvdataapi.modules.TableCreator;
import net.redewhite.lvdataapi.modules.VariableCreator;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.DataAPI.INSTANCE;
import static net.redewhite.lvdataapi.types.VariablesType.MAP;

public class MapVariable extends VariableCreator {

    public MapVariable(String name, TableCreator table, Object defaultValue) {
        super(INSTANCE, name, table, defaultValue, true, MAP, true);
    }
    public MapVariable(Plugin plugin, String name, TableCreator table, Object defaultValue) {
        super(plugin, name, table, defaultValue, true, MAP, true);
    }

    public MapVariable(String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(INSTANCE, name, table, defaultValue, saveToDatabase, MAP, true);
    }
    public MapVariable(Plugin plugin, String name, TableCreator table, Object defaultValue, Boolean saveToDatabase) {
        super(plugin, name, table, defaultValue, saveToDatabase, MAP, true);
    }

}
