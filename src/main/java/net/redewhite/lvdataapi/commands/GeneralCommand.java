package net.redewhite.lvdataapi.commands;

import net.redewhite.lvdataapi.receptors.VariableReceptor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static net.redewhite.lvdataapi.DataAPI.*;
import static net.redewhite.lvdataapi.developers.AdvancedAPI.databaseSave;

public class GeneralCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equals("dataapi")) {

            if (!sender.hasPermission("lvdataapi.command.use")) {
                getMessage((Player) sender, "Command no permission");
                return true;
            }

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("save")) {
                    if (args.length > 1) {
                        ArrayList<VariableReceptor> receptors = new ArrayList<>();
                        for (VariableReceptor receptor : getVariableReceptors().keySet()) {
                            if (receptor.getName().equals(args[1])) {
                                receptors.add(receptor);
                            }
                        }
                        if (receptors.isEmpty()) {
                            getMessage((Player) sender, "None receptor found", args[1]);
                            return true;
                        }
                        for (VariableReceptor receptor : receptors) {
                            getMessage((Player) sender, "Saving receptor", receptor.getName());
                            databaseSave(receptor, receptor.getTable());
                            getMessage((Player) sender, "Saving receptor successfully", receptor.getName());
                        }
                    } else {
                        getMessage((Player) sender, "Saving all receptors");
                        for (VariableReceptor receptor : getVariableReceptors().keySet()) {
                            databaseSave(receptor, receptor.getTable());
                        }
                        getMessage((Player) sender, "Saving all receptors successfully");
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage("§aReloading plugin's configuration file...");
                    reloadAllConfigFile();
                    sender.sendMessage("§aSuccessfully reloaded all plugin's configuration and messages file.");
                }
            } else {
                sender.sendMessage("");
                sender.sendMessage("§f/" + cmd.getName() + " save §7- Save all variables data");
                sender.sendMessage("§f/" + cmd.getName() + " save (player) §7- Save player's data");
                sender.sendMessage("§f/" + cmd.getName() + " save (name) §7- Save receptor's data");
                sender.sendMessage("§f/" + cmd.getName() + " reload §7- Reload configuration.yml");
                sender.sendMessage("");
            }
        }
        return true;
    }
}
