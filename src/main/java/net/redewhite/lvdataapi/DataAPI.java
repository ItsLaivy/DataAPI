package net.redewhite.lvdataapi;

import net.redewhite.lvdataapi.listeners.PluginEvents;
import net.redewhite.lvdataapi.receptors.InactiveVariable;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import net.redewhite.lvdataapi.modules.*;
import org.bukkit.ChatColor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class DataAPI extends JavaPlugin {

    public static DataAPI INSTANCE;
    
    public static YamlConfiguration config;

    private static BukkitRunnable saveTask;

    // Modules List
    private static final List<Database> databases = new ArrayList<>();
    private static final List<VariableCreator> variables = new ArrayList<>();
    private static final List<TableCreator> tables = new ArrayList<>();
    private static final List<ReceptorCreator> receptors = new ArrayList<>();

    private static final List<ActiveVariable> activeVariables = new ArrayList<>();
    private static final List<InactiveVariable> inactiveVariables = new ArrayList<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        
        if (hasWrongConfiguration(
                "variables loading messages",
                "variables creating messages",
                "table loading messages",
                "table creating messages",
                "database loading messages",
                "database creating messages",

                "check-updates",
                "AutoSaver"
        )) {
            if (new File(getDataFolder(), "config.yml").delete()) {
                saveDefaultConfig();
                broadcastColoredMessage("§cYour configuration file is broken, reseted.");
            }
        }

        Bukkit.getPluginManager().registerEvents(new PluginEvents(), this);

        saveDefaultConfig();
        new Thread(new Updater()).start();

        long interval = config.getLong("AutoSaver") * 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                saveTask = this;
                boolean save = false;
                for (ReceptorCreator receptor : getReceptors()) {
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
        for (ReceptorCreator receptor : getReceptors()) {
            if (receptor.autoSaveWhenServerClose()) {
                receptor.save();
            }
        }
    }

    public static void broadcastColoredMessage(String message, boolean reallySend) {
        if (reallySend) broadcastColoredMessage(message);
    }
    public static void broadcastColoredMessage(String message) {
        INSTANCE.getServer().getConsoleSender().sendMessage("§8[§6" + INSTANCE.getDescription().getName() + "§8]§7" + " " + ChatColor.translateAlternateColorCodes('&', message));
    }

    public static List<Database> getDatabases() {
        return databases;
    }
    public static List<VariableCreator> getVariables() {
        return variables;
    }
    public static List<TableCreator> getTables() {
        return tables;
    }
    public static List<ReceptorCreator> getReceptors() {
        return receptors;
    }
    public static List<ActiveVariable> getActiveVariables() {
        return activeVariables;
    }
    public static List<InactiveVariable> getInactiveVariables() {
        return inactiveVariables;
    }

    public static BukkitRunnable getSaveTask() {
        return saveTask;
    }

    public static String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(new Date());
    }

    // VARIABLE HASHES

    public static String getVariableHashedValue(Object value) {
        try {
            if (value != null) {
                return value.toString()
                        .replace(",", "<!COMMA>")

                        .replace("[", "<!RIGHTBRACKET>")
                        .replace("]", "<!LEFTBRACKET>")
                        .replace("{", "<!RIGHTKEY>")
                        .replace("}", "<!LEFTKEY>")
                        .replace("(", "<!RIGHTPARENTHESIS>")
                        .replace(")", "<!LEFTPARENTHESIS>")

                        .replace("=", "<!EQUAL>")

                        .replace("'", "<!SIMPLECOMMA>")
                        .replace("\"", "<!QUOTE>");
            }
        } catch (NullPointerException ignore) {
        }
        return "<NULL!>";
    }
    public static String getVariableUnhashedValue(Object value) {
        try {
            boolean bool = value.toString().equals("<NULL!>");
            if (!bool) {
                return value.toString()
                        .replace("<!COMMA>", ",")

                        .replace("<!RIGHTBRACKET>", "[")
                        .replace("<!LEFTBRACKET>", "]")
                        .replace("<!RIGHTKEY>", "{")
                        .replace("<!LEFTKEY>", "}")
                        .replace("<!RIGHTPARENTHESIS>", "(")
                        .replace("<!LEFTPARENTHESIS>", ")")

                        .replace("<!EQUAL>", "=")

                        .replace("<!SIMPLECOMMA>", "'")
                        .replace("<!QUOTE>", "\"");
            }
        } catch (NullPointerException ignore) {
        }
        return null;
    }

    private boolean hasWrongConfiguration(String... strings) {
        for (String str : strings) {
            if (!config.contains(str)) {
                return true;
            }
        }
        return false;
    }
}