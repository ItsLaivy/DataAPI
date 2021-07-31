package net.redewhite.lvdataapi;

import net.redewhite.lvdataapi.database.DatabaseConnection;
import net.redewhite.lvdataapi.database.PlayerVariable;
import net.redewhite.lvdataapi.database.Variable;
import net.redewhite.lvdataapi.events.BukkitDefaultEvents;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static net.redewhite.lvdataapi.LvDataPluginAPI.*;

public class LvDataPlugin extends JavaPlugin {

    public static HashMap<Variable, String> dataapi = new HashMap<>();
    public static HashMap<PlayerVariable, Player> playerapi = new HashMap<>();

    private static BukkitRunnable task = null;

    public static YamlConfiguration config;

    private static Boolean saved = false;
    public static Boolean debug = false;

    public static String database_type;

    public static String now = new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(new Date());

    public static LvDataPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        debug = getConfig().getBoolean("debug");

        File configFile = new File(getInstance().getDataFolder() + File.separator + "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        try {
            DatabaseConnection.connect();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }

        if (DatabaseConnection.conn != null) {

            Bukkit.getPluginManager().registerEvents(new BukkitDefaultEvents(), this);

            new Variable(this, "examplevar", "example-value");

            if (!(getConfig().getInt("AutoSaver") == 0)) {
                startAutoSave(getConfig().getInt("AutoSaver"));
            }

            for (Player player : getServer().getOnlinePlayers()) {
                loadPlayer(player);
            }
        }

    }

    @Override
    public void onDisable() {
        stopAutoSave();

        if (DatabaseConnection.conn != null) {
            for (Player player : getServer().getOnlinePlayers()) {
                unloadPlayer(player);
            }
        }

        DatabaseConnection.close();
    }

    public static LvDataPlugin getInstance() {
        return instance;
    }

    public static void broadcastInfo(String message) {
        Bukkit.getLogger().info("[" + getInstance().getDescription().getName() + "]" + " " + message);
    }
    public static void broadcastWarn(String message) {
        Bukkit.getLogger().warning("[" + getInstance().getDescription().getName() + "]" + " " + message);
    }

    public static void startAutoSave(Integer seconds) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task = this;
                saved = false;
                Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
                    for (Player player : LvDataPlugin.getInstance().getServer().getOnlinePlayers()) {
                        if (isLoaded(player)) {
                            saved = true;
                            savePlayer(player);
                        }
                    }
                    if (saved) LvDataPlugin.broadcastInfo("Successfully saved all players data.");
                });
            }
        }.runTaskTimer(LvDataPlugin.getInstance(), 0, seconds * 20);
    }

    public static void stopAutoSave() {
        if (!(task == null)) {
            task.cancel();
        }
    }

}
