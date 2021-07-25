package net.redewhite.LVDataAPI;

import net.redewhite.LVDataAPI.Database.Players;
import net.redewhite.LVDataAPI.Database.SQLiteConnection;
import net.redewhite.LVDataAPI.Database.Variable;
import net.redewhite.LVDataAPI.Events.BukkitDefaultEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

import static net.redewhite.LVDataAPI.API.*;

public class Main extends JavaPlugin {

    public static HashMap<Variable, String> dataapi = new HashMap<>();
    public static HashMap<Players, Player> playerapi = new HashMap<>();

    private static BukkitRunnable task;

    private static Boolean saved;
    public static String gitlink;

    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new BukkitDefaultEvents(), this);
        SQLiteConnection.connect();

        gitlink = "https://github.com/LaivyTLife/DataAPI";

        startAutoSave(getConfig().getInt("AutoSaver"));
        for (Player player : getServer().getOnlinePlayers()) { loadPlayer(player); }

    }

    @Override
    public void onDisable() {
        SQLiteConnection.close();
        stopAutoSave();
        for (Player player : getServer().getOnlinePlayers()) { unloadPlayer(player); }

    }

    public static Main getInstance() {
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
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
                        if (isLoaded(player)) {
                            saved = true;
                            savePlayer(player);
                        }
                    }
                    if (saved) Main.broadcastInfo("Successfully saved all players data.");
                });
            }
        }.runTaskTimer(Main.getInstance(), 0, seconds * 20);
    }

    public static void stopAutoSave() {
        task.cancel();
    }

}
