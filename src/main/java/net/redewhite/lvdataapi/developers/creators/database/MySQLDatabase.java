package net.redewhite.lvdataapi.developers.creators.database;

import net.redewhite.lvdataapi.modules.DatabaseCreationModule;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.types.ConnectionType.MYSQL;
import static net.redewhite.lvdataapi.DataAPI.INSTANCE;

@SuppressWarnings("unused")
public class MySQLDatabase extends DatabaseCreationModule {

    public MySQLDatabase(String address, Integer port, String user, String password) {
        super(INSTANCE, "default", MYSQL, user, password, port, address, null);
    }

    public MySQLDatabase(String name, String address, Integer port, String user, String password) {
        super(INSTANCE, name, MYSQL, user, password, port, address, null);
    }
    public MySQLDatabase(Plugin plugin, String name, String address, Integer port, String user, String password) {
        super(plugin, name, MYSQL, user, password, port, address, null);
    }

}
