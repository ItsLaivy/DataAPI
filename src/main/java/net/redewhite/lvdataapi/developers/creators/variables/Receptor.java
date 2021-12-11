package net.redewhite.lvdataapi.developers.creators.variables;

import net.redewhite.lvdataapi.modules.TableCreationModule;
import net.redewhite.lvdataapi.modules.VariableReceptorModule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.DataAPI.INSTANCE;

@SuppressWarnings("unused")
public class Receptor extends VariableReceptorModule {

    public Receptor(Player player, TableCreationModule table) {
        super(INSTANCE, player.getName(), player.getUniqueId().toString(), table);
    }
    public Receptor(Plugin plugin, Player player, TableCreationModule table) {
        super(plugin, player.getName(), player.getUniqueId().toString(), table);
    }

    public Receptor(String name, String bruteID, TableCreationModule table) {
        super(INSTANCE, name, bruteID, table);
    }
    public Receptor(Plugin plugin, String name, String bruteID, TableCreationModule table) {
        super(plugin, name, bruteID, table);
    }

}
