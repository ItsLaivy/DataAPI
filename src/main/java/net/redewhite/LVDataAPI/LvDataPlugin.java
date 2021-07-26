package net.redewhite.lvdataapi;

import net.redewhite.lvdataapi.database.PlayerVariable;
import net.redewhite.lvdataapi.database.SQLiteConnection;
import net.redewhite.lvdataapi.database.Variable;
import net.redewhite.lvdataapi.events.BukkitDefaultEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static net.redewhite.lvdataapi.LvDataPluginAPI.*;

public class LvDataPlugin extends JavaPlugin {

    public static HashMap<Variable, String> dataapi = new HashMap<>();
    public static HashMap<PlayerVariable, Player> playerapi = new HashMap<>();

    private static BukkitRunnable task = null;

    private static Boolean saved;
    public static String gitlink;

    public static String now = new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(new Date());

    public static LvDataPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        SQLiteConnection.connect();
        Bukkit.getPluginManager().registerEvents(new BukkitDefaultEvents(), this);

        new Variable(this, "examplevar", "example-value");
        gitlink = "https://github.com/LaivyTLife/DataAPI";

        if (!(getConfig().getInt("AutoSaver") == 0)) {
            startAutoSave(getConfig().getInt("AutoSaver"));
        }

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

        SQLiteConnection.close();
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
