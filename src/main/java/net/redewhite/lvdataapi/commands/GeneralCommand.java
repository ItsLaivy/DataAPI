package net.redewhite.lvdataapi.commands;

import net.redewhite.lvdataapi.variables.receptors.TextVariableReceptor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import static net.redewhite.lvdataapi.developers.DatabaseAPI.databaseSavePlayer;
import static net.redewhite.lvdataapi.developers.DatabaseAPI.databaseSaveText;
import static net.redewhite.lvdataapi.LvDataAPI.*;

public class GeneralCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equals("dataapi")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("save")) {
                    if (args.length > 1) {
                        try {
                            Player player = Bukkit.getPlayer(args[1]);
                            sender.sendMessage("§aSaving §f" + player.getName() + "§a's data, please wait...");
                            databaseSavePlayer(player);
                            sender.sendMessage("§aSuccessfully saved §f" + player.getName() + "§a's data.");
                        } catch (NullPointerException e) {
                            for (TextVariableReceptor variable : getTextVariablesNames().keySet()) {
                                if (variable.getVariableName().equals(args[1])) {
                                    sender.sendMessage("§aSaving §atext variable §f" + variable.getName() + "§a's data, please wait...");
                                    databaseSaveText(variable);
                                    sender.sendMessage("§aSuccessfully saved §atext variable §f" + variable.getName() + "§a's data.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§cThat player or text variable isn't online or doesn't exists.");
                        }
                    } else {
                        sender.sendMessage("§aSaving all players and text variables data...");
                        for (Player player : getInstance().getServer().getOnlinePlayers()) {
                            databaseSavePlayer(player);
                        }
                        for (TextVariableReceptor variable : getTextVariablesNames().keySet()) {
                            databaseSaveText(variable);
                        }
                        sender.sendMessage("§aSuccessfully saved all players and text variables data.");
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage("§aReloading plugin's configuration file...");
                    getInstance().reloadAllConfig();
                    sender.sendMessage("§aSuccessfully reloaded all plugin's configuration file.");
                }
            } else {
                sender.sendMessage("");
                sender.sendMessage("§f/" + cmd.getName() + " save §7- Save all variables data");
                sender.sendMessage("§f/" + cmd.getName() + " save (player) §7- Save player's data");
                sender.sendMessage("§f/" + cmd.getName() + " save (textVarBruteName) §7- Save textVar's data");
                sender.sendMessage("§f/" + cmd.getName() + " reload §7- Reload configuration.yml");
                sender.sendMessage("");
            }
        }
        return true;
    }
}
