package net.redewhite.lvdataapi.commands;

import net.redewhite.lvdataapi.DataAPI;
import net.redewhite.lvdataapi.Updater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GeneralCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("dataapi")) {
            sender.sendMessage("§7DataAPI's version: §f" + DataAPI.INSTANCE.getDescription().getVersion());

            if (Updater.hasNewVersion()) {
                sender.sendMessage("§7A new version has released §f" + Updater.getNewVersion() + "§7. Download here: §ahttps://github.com/LaivyTLife/DataAPI/releases/" + Updater.getNewVersion() + "/");
            }
        }

        return true;
    }
}
