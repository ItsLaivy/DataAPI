package net.redewhite.lvdataapi.developers.creators.database;

import net.redewhite.lvdataapi.modules.DatabaseCreationModule;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.types.ConnectionType.SQLITE;
import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class SQLiteDatabase extends DatabaseCreationModule {

    public SQLiteDatabase() {
        super(INSTANCE, "default", SQLITE, null, null, null, null, "");
    }

    public SQLiteDatabase(String name) {
        super(INSTANCE, name, SQLITE, null, null, null, null, "");
    }
    public SQLiteDatabase(String name, String path) {
        super(INSTANCE, name, SQLITE, null, null, null, null, path);
    }

    public SQLiteDatabase(Plugin plugin, String name) {
        super(plugin, name, SQLITE, null, null, null, null, "");
    }
    public SQLiteDatabase(Plugin plugin, String name, String path) {
        super(plugin, name, SQLITE, null, null, null, null, path);
    }

}
