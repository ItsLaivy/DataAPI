package net.redewhite.lvdataapi.developers.creators;

import net.redewhite.lvdataapi.modules.DatabaseCreationModule;
import net.redewhite.lvdataapi.modules.TableCreationModule;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.DataAPI.INSTANCE;

@SuppressWarnings("unused")
public class Table extends TableCreationModule {

    public Table(DatabaseCreationModule database) {
        super(INSTANCE, "default", database);
    }
    public Table(String name, DatabaseCreationModule database) {
        super(INSTANCE, name, database);
    }
    public Table(Plugin plugin, String name, DatabaseCreationModule database) {
        super(plugin, name, database);
    }

}
