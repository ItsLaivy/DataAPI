package net.redewhite.lvdataapi.receptors;

import net.redewhite.lvdataapi.events.api.receptors.VariableReceptorCreateEvent;
import net.redewhite.lvdataapi.modules.VariableCreationModule;
import net.redewhite.lvdataapi.loaders.InactiveVariableLoader;
import net.redewhite.lvdataapi.loaders.ActiveVariableLoader;
import net.redewhite.lvdataapi.creators.VariablesTable;
import net.redewhite.lvdataapi.developers.AdvancedAPI;
import net.redewhite.lvdataapi.developers.EasyAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

import static net.redewhite.lvdataapi.DataAPI.*;

@SuppressWarnings("unused")
public class VariableReceptor {

    private final VariablesTable table;
    private final String nameBruteId;
    private final Plugin plugin;
    private final String name;

    public VariableReceptor(Plugin plugin, Player player, VariablesTable table) {
        this(plugin, player.getName(), player.getUniqueId().toString(), table);
    }

    public VariableReceptor(Plugin plugin, String name, String bruteId, VariablesTable table) {
        this.nameBruteId = bruteId;
        this.name = name;
        this.plugin = plugin;
        this.table = table;

        VariableReceptorCreateEvent event = new VariableReceptorCreateEvent(this);
        instance.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            for (VariableReceptor receptor : getVariableReceptors().keySet()) {
                if (receptor.getTable() == this.table) {
                    if (receptor.getNameBruteId().equals(this.nameBruteId)) {
                        getMessage("Receptor variable already defined", name);
                        return;
                    }
                }
            }

            getVariableReceptors().put(this, getReceptorBruteId());
            if (!AdvancedAPI.databaseLoad(this, table)) {
                getMessage("Receptor variable define unknown error", name);
                unload();
            } else {
                getMessage("Receptor variable loaded", name);
            }
        }
    }

    public void unload() {
        AdvancedAPI.databaseSave(this, this.getTable());

        ArrayList<ActiveVariableLoader> array1 = new ArrayList<>();
        ArrayList<InactiveVariableLoader> array2 = new ArrayList<>();
        for (ActiveVariableLoader var : getActiveVariables().keySet()) {
            if (var.getOwnerBruteId().equals(getNameBruteId())) array1.add(var);
        }
        for (InactiveVariableLoader var : getInactiveVariables().keySet()) {
            if (var.getOwnerBruteId().equals(getNameBruteId())) array2.add(var);
        }

        for (ActiveVariableLoader var : array1) getActiveVariables().remove(var);
        for (InactiveVariableLoader var : array2) getInactiveVariables().remove(var);

        getVariableReceptors().remove(this);
    }
    public void delete() {
        getVariableReceptors().remove(this);
        EasyAPI.deleteVariableReceptor(this);

        unload();
    }

    public void createVariable(VariableCreationModule variable) {
        new ActiveVariableLoader(this, variable.getDefaultValue(), variable);
    }

    public VariablesTable getTable() {
        return table;
    }
    public Plugin getPlugin() {
        return plugin;
    }
    public String getName() {
        return name;
    }
    public String getNameBruteId() {
        return nameBruteId;
    }
    public String getReceptorBruteId() {
        return plugin.getName() + "_TEXT_" + name;
    }
}
