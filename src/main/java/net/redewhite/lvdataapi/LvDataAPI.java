package net.redewhite.lvdataapi;

import net.redewhite.lvdataapi.variables.loaders.InactivePlayerVariable;
import net.redewhite.lvdataapi.utils.VariableCreationController;
import net.redewhite.lvdataapi.variables.loaders.PlayerVariable;
import net.redewhite.lvdataapi.listeners.BukkitDefaultEvents;
import org.bukkit.configuration.file.YamlConfiguration;
import net.redewhite.lvdataapi.developers.DatabaseAPI;
import net.redewhite.lvdataapi.variables.Variable;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;
import java.io.File;

import static net.redewhite.lvdataapi.database.DatabaseConnection.*;
import static net.redewhite.lvdataapi.developers.API.*;

@SuppressWarnings("unused")
public class LvDataAPI extends JavaPlugin {

    public static final String now = new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(new Date());

    private static final HashMap<InactivePlayerVariable, String> inactivevariables = new HashMap<>();
    private static final HashMap<VariableCreationController, String> variables = new HashMap<>();

    private static final HashMap<PlayerVariable, Player> players = new HashMap<>();

    private static BukkitRunnable task;

    public static databaseConnection database_type;
    public static YamlConfiguration config;
    public static String tableNamePlayers;
    public static String tableNameText;
    public static LvDataAPI instance;
    public static Boolean debug;

    static databaseConnection database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        File configFile = new File(getDataFolder() + File.separator + "config.yml");

        instance = this;
        config = YamlConfiguration.loadConfiguration(configFile);
        debug = getConfig().getBoolean("debug");
        tableNamePlayers = getConfig().getString("Players table name");
        tableNameText = getConfig().getString("Texts table name");

        DatabaseAPI.connection();
        Bukkit.getPluginManager().registerEvents(new BukkitDefaultEvents(), this);

        if (!(config.getInt("AutoSaver") == 0)) startAutoSave(getConfig().getInt("AutoSaver"));

        new Variable(this, "defaultvar", "defaultvalue");

        for (Player player : getServer().getOnlinePlayers()) {
            loadPlayer(player);
        }

    }

    @Override
    public void onDisable() {
        stopAutoSave();
        for (Player player : getServer().getOnlinePlayers()) {
            unloadPlayer(player);
        }
        close();
    }

    public static void broadcastColoredMessage(String message) {
        getInstance().getServer().getConsoleSender().sendMessage("§8[§6" + getInstance().getDescription().getName() + "§8]§7" + " " + message);
    }

    public static void startAutoSave(Integer seconds) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task = this;
                Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
                    boolean saved = false;
                    for (Player player : instance.getServer().getOnlinePlayers()) {
                        if (isLoaded(player)) {
                            saved = true;
                            savePlayer(player, config.getBoolean("Async variables saving"));
                        }
                    }
                    if (saved) broadcastColoredMessage("§aSuccessfully saved all players data.");
                });
            }
        }.runTaskTimer(instance, 0, seconds * 20);
    }

    public static void stopAutoSave() {
        if (!(task == null)) {
            task.cancel();
        }
    }

    public static LvDataAPI getInstance() {
        return instance;
    }

    public enum databaseConnection {
        MYSQL, YAML, SQLITE
    }
    public enum variableType {
        ARRAY, NORMAL, TEMPORARY
    }

    public static HashMap<InactivePlayerVariable, String> getInactiveVariables() {
        return inactivevariables;
    }
    public static HashMap<VariableCreationController, String> getVariables() {
        return variables;
    }
    public static HashMap<PlayerVariable, Player> getPlayers() {
        return players;
    }

    public static databaseConnection getConnectedDatabase() {
        return database;
    }
    public static String getTableNamePlayers() {
        return tableNamePlayers;
    }
    public static String getTableNameText() {
        return tableNameText;
    }
}
