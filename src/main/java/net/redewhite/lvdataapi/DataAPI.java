package net.redewhite.lvdataapi;

import net.redewhite.lvdataapi.receptors.InactiveVariableLoader;
import net.redewhite.lvdataapi.receptors.ActiveVariableLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import net.redewhite.lvdataapi.modules.*;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;

@SuppressWarnings("unused")
public class DataAPI extends JavaPlugin {

    public static final String version = "3.0";

    public static DataAPI INSTANCE;
    public static YamlConfiguration config;

    private static BukkitRunnable saveTask;

    // Modules List
    private static final List<DatabaseCreationModule> databases = new ArrayList<>();
    private static final List<VariableCreationModule> variables = new ArrayList<>();
    private static final List<TableCreationModule> tables = new ArrayList<>();
    private static final List<VariableReceptorModule> receptors = new ArrayList<>();

    private static final List<ActiveVariableLoader> activeVariables = new ArrayList<>();
    private static final List<InactiveVariableLoader> inactiveVariables = new ArrayList<>();

    @Override
    public void onEnable() {
        INSTANCE = this;

        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        if (config.getBoolean("check-updates")) Updater.checkUpdates();

        long interval = config.getLong("AutoSaver") * 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                saveTask = this;
                boolean save = false;
                for (VariableReceptorModule receptor : getReceptors()) {
                    receptor.save();
                    save = true;
                }
                if (save) {
                    broadcastColoredMessage("&aSuccessfully saved all receptors.");
                }
            }
        }.runTaskTimerAsynchronously(this, (interval / 2), interval);
    }

    @Override
    public void onDisable() {
        for (VariableReceptorModule receptor : getReceptors()) {
            receptor.save();
        }
    }

    public static void broadcastColoredMessage(String message) {
        INSTANCE.getServer().getConsoleSender().sendMessage("§8[§6" + INSTANCE.getDescription().getName() + "§8]§7" + " " + ChatColor.translateAlternateColorCodes('&', message));
    }

    public static List<DatabaseCreationModule> getDatabases() {
        return databases;
    }
    public static List<VariableCreationModule> getVariables() {
        return variables;
    }
    public static List<TableCreationModule> getTables() {
        return tables;
    }
    public static List<VariableReceptorModule> getReceptors() {
        return receptors;
    }
    public static List<ActiveVariableLoader> getActiveVariables() {
        return activeVariables;
    }
    public static List<InactiveVariableLoader> getInactiveVariables() {
        return inactiveVariables;
    }

    public static String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(new Date());
    }
}