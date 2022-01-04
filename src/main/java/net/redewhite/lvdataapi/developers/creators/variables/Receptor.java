package net.redewhite.lvdataapi.developers.creators.variables;

import net.redewhite.lvdataapi.modules.TableCreator;
import net.redewhite.lvdataapi.modules.ReceptorCreator;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.DataAPI.INSTANCE;

@SuppressWarnings("unused")
public class Receptor extends ReceptorCreator {

    public Receptor(OfflinePlayer player, TableCreator table) {
        super(INSTANCE, player.getName(), player.getUniqueId().toString(), table);
    }
    public Receptor(Plugin plugin, OfflinePlayer player, TableCreator table) {
        super(plugin, player.getName(), player.getUniqueId().toString(), table);
    }

    public Receptor(String name, String bruteID, TableCreator table) {
        super(INSTANCE, name, bruteID, table);
    }
    public Receptor(Plugin plugin, String name, String bruteID, TableCreator table) {
        super(plugin, name, bruteID, table);
    }

}
