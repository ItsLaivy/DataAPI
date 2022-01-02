package net.redewhite.lvdataapi.developers.creators;

import net.redewhite.lvdataapi.modules.Database;
import net.redewhite.lvdataapi.modules.TableCreator;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.DataAPI.INSTANCE;

@SuppressWarnings("unused")
public class Table extends TableCreator {

    public Table(Database database) {
        super(INSTANCE, "default", database);
    }
    public Table(String name, Database database) {
        super(INSTANCE, name, database);
    }
    public Table(Plugin plugin, String name, Database database) {
        super(plugin, name, database);
    }

}
