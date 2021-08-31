package net.redewhite.lvdataapi;

import net.redewhite.lvdataapi.database.DatabaseConnection;
import net.redewhite.lvdataapi.modules.VariableCreationModule;
import net.redewhite.lvdataapi.loaders.InactiveVariableLoader;
import net.redewhite.lvdataapi.loaders.ActiveVariableLoader;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import net.redewhite.lvdataapi.events.BukkitDefaultEvents;
import net.redewhite.lvdataapi.creators.VariablesTable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.io.File;

import static net.redewhite.lvdataapi.developers.AdvancedAPI.*;

public class DataAPI extends JavaPlugin {

    private static final HashMap<VariablesTable, String> tables = new HashMap<>();

    private static final HashMap<VariableCreationModule, String> variables = new HashMap<>();

    private static final HashMap<InactiveVariableLoader, String> inactiveVariables = new HashMap<>();
    private static final HashMap<ActiveVariableLoader, String> activeVariables = new HashMap<>();

    private static final HashMap<VariableReceptor, String> variableReceptors = new HashMap<>();

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
