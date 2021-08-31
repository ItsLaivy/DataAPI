package net.redewhite.lvdataapi;

import net.redewhite.lvdataapi.commands.GeneralCommand;
import net.redewhite.lvdataapi.database.DatabaseConnection;
import net.redewhite.lvdataapi.developers.AdvancedAPI;
import net.redewhite.lvdataapi.developers.EasyAPI;
import net.redewhite.lvdataapi.modules.VariableCreationModule;
import net.redewhite.lvdataapi.loaders.InactiveVariableLoader;
import net.redewhite.lvdataapi.loaders.ActiveVariableLoader;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import net.redewhite.lvdataapi.events.BukkitDefaultEvents;
import net.redewhite.lvdataapi.creators.VariablesTable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.io.File;

import static net.redewhite.lvdataapi.developers.AdvancedAPI.*;
import static net.redewhite.lvdataapi.developers.EasyAPI.getVariableReceptor;

public class DataAPI extends JavaPlugin {

    private static final HashMap<VariablesTable, String> tables = new HashMap<>();

    private static final HashMap<VariableCreationModule, String> variables = new HashMap<>();

    private static final HashMap<InactiveVariableLoader, String> inactiveVariables = new HashMap<>();
    private static final HashMap<ActiveVariableLoader, String> activeVariables = new HashMap<>();

    private static final HashMap<VariableReceptor, String> variableReceptors = new HashMap<>();

    private static BukkitRunnable task = null;

    public static databaseConnection database_type;
    public static YamlConfiguration config;
    public static YamlConfiguration messages;
    public static DataAPI instance;
    public static Boolean debug;

    public static VariablesTable table;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);

        File configFile = new File(getDataFolder() + File.separator + "config.yml");
        File messagesFile = new File(getDataFolder() + File.separator + "messages.yml");

        instance = this;
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        debug = getConfig().getBoolean("debug");

        getCommand("dataapi").setExecutor(new GeneralCommand());

        if (connection()) {
            getServer().getPluginManager().registerEvents(new BukkitDefaultEvents(), this);

            for (VariablesTable tables : getTables().keySet()) {
                for (Player player : this.getServer().getOnlinePlayers()) {
                    new VariableReceptor(tables.getPlugin(), player, tables);
                }
                for (VariableReceptor receptor : variableReceptors.keySet()) {
                    if (receptor.getTable() == tables) {
                        databaseLoad(receptor, tables);
                    }
                }
            }

            startAutoSave(config.getInt("AutoSaver"));
        } else {
            stopPlugin();
        }
    }

    @Override
    public void onDisable() {
        for (VariablesTable table : getTables().keySet()) {
            ArrayList<VariableReceptor> array = new ArrayList<>();
            for (VariableReceptor receptor : getVariableReceptors().keySet()) {
                if (receptor.getTable() == table) array.add(receptor);
            }
            for (VariableReceptor receptor : array) databaseUnload(receptor, table);
        }

        stopAutoSave();
        DatabaseConnection.close();
    }

    public static void stopPlugin() {
        if (config.getBoolean("close-server")) {
            instance.getServer().shutdown();
        } else {
            instance.getPluginLoader().disablePlugin(instance);
        }
    }

    public static void broadcastColoredMessage(String message) {
        if (message != null) {
            getInstance().getServer().getConsoleSender().sendMessage("§8[§6" + instance.getDescription().getName() + "§8]§7" + " " + ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void startAutoSave(Integer seconds) {
        new BukkitRunnable() {
            public void run() {
                task = this;
                final boolean[] saved = {false};
                Bukkit.getScheduler().runTaskAsynchronously(getInstance(), () -> {
                    for (VariablesTable table : getTables().keySet()) {
                        for (VariableReceptor receptor : getVariableReceptors().keySet()) {
                            if (receptor.getTable() == table) {
                                if (isLoaded(receptor.getNameBruteId())) {
                                    saved[0] = true;
                                    AdvancedAPI.databaseSave(getVariableReceptor(table.getPlugin(), receptor.getNameBruteId(), table), table);
                                }
                            }
                        }
                    }
                    if (saved[0]) getMessage("Successfully saved all players");
                });
            }
        }.runTaskTimer(getInstance(), (seconds * 20L), (seconds * 20L));
    }

    public static void stopAutoSave() {
        if (task != null)
            task.cancel();
    }

    public static void getMessage(Player player, String path) {
        if (messages.isSet(path)) {
            if (messages.getString(path).equals("")) {
                return;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString(path)));
            return;
        }
        broadcastColoredMessage("&cWrong &4messages.yml &cfile! Please, delete then to the plugin re-create for you.");
        stopPlugin();
    }
    public static void getMessage(String path) {
        if (messages.isSet(path)) {
            if (messages.getString(path).equals("")) {
                return;
            }
            broadcastColoredMessage(ChatColor.translateAlternateColorCodes('&', messages.getString(path)));
            return;
        }
        broadcastColoredMessage("&cWrong &4messages.yml &cfile! Please, delete then to the plugin re-create for you.");
        stopPlugin();
    }

    public static void getMessage(Player player, String path, String... replaces) {
        if (messages.isSet(path)) {
            if (messages.getString(path).equals("")) {
                return;
            }
            String message = messages.getString(path);
            int row = 1;

            for (String e : replaces) {
                message = message.replace(":value" + row + ":", e);
                row++;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return;
        }
        broadcastColoredMessage("&cWrong &4messages.yml &cfile! Please, delete then to the plugin re-create for you.");
        stopPlugin();
    }
    public static void getMessage(String path, String... replaces) {
        if (messages.isSet(path)) {
            if (messages.getString(path).equals("")) {
                return;
            }
            String message = messages.getString(path);
            int row = 1;

            for (String e : replaces) {
                message = message.replace(":value" + row + ":", e);
                row++;
            }
            broadcastColoredMessage(ChatColor.translateAlternateColorCodes('&', message));
            return;
        }
        broadcastColoredMessage("&cWrong &4messages.yml &cfile! Please, delete then to the plugin re-create for you.");
        stopPlugin();
    }

    public static void reloadAllConfigFile() {
        instance.saveDefaultConfig();
        instance.reloadConfig();
        instance.saveResource("messages.yml", false);
        File configFile = new File(instance.getDataFolder() + File.separator + "config.yml");
        File messagesFile = new File(instance.getDataFolder() + File.separator + "messages.yml");

        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        debug = instance.getConfig().getBoolean("debug");

        stopAutoSave();

        DatabaseConnection.close();
        connection();

        if (config.getInt("AutoSaver") != 0)
            startAutoSave(instance.getConfig().getInt("AutoSaver"));
    }

    public static String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(new Date());
    }
    public static DataAPI getInstance() {
        return instance;
    }
    public static HashMap<VariablesTable, String> getTables() {
        return tables;
    }
    public static HashMap<InactiveVariableLoader, String> getInactiveVariables() {
        return inactiveVariables;
    }
    public static HashMap<ActiveVariableLoader, String> getActiveVariables() {
        return activeVariables;
    }
    public static HashMap<VariableReceptor, String> getVariableReceptors() {
        return variableReceptors;
    }
    public static HashMap<VariableCreationModule, String> getVariables() {
        return variables;
    }
    public enum databaseConnection {
        MYSQL, SQLITE
    }
    public enum variableDataType {
        ARRAY, NORMAL, TEMPORARY
    }

}
