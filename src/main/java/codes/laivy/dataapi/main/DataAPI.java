package codes.laivy.dataapi.main;

import codes.laivy.dataapi.commands.GeneralCommands;
import codes.laivy.dataapi.listeners.BukkitEvents;
import codes.laivy.dataapi.modules.Database;
import codes.laivy.dataapi.modules.Receptor;
import codes.laivy.dataapi.modules.Table;
import codes.laivy.dataapi.modules.Variable;
import codes.laivy.dataapi.modules.receptors.ActiveVariable;
import codes.laivy.dataapi.modules.receptors.InactiveVariable;
import codes.laivy.dataapi.type.ConnectionType;
import com.sun.istack.internal.NotNull;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class DataAPI extends JavaPlugin {

    private YamlConfiguration config;
    private final Metrics metrics = new Metrics(this, 14425);

    public static DataAPI plugin() {
        return getPlugin(DataAPI.class);
    }

    private final Map<ConnectionType, Map<String, Database>> databases = new HashMap<>();
    private final Map<Database, Map<String, Table>> tables = new HashMap<>();
    private final Map<Table, Map<String, Variable>> variables = new HashMap<>();
    private final Map<Table, Map<String, Receptor>> receptors = new HashMap<>();

    private final Map<Table, Map<String, Map<String, InactiveVariable>>> inactiveVariables = new HashMap<>();
    private final Map<Table, Map<String, Map<String, ActiveVariable>>> activeVariables = new HashMap<>();

    public String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(new Date());
    }

    public Map<ConnectionType, Map<String, Database>> getDatabases() {
        return databases;
    }
    public Map<Database, Map<String, Table>> getTables() {
        return tables;
    }
    public Map<Table, Map<String, Variable>> getVariables() {
        return variables;
    }
    public Map<Table, Map<String, Receptor>> getReceptors() {
        return receptors;
    }

    public Map<Table, Map<String, Map<String, InactiveVariable>>> getInactiveVariables() {
        return inactiveVariables;
    }
    public Map<Table, Map<String, Map<String, ActiveVariable>>> getActiveVariables() {
        return activeVariables;
    }

    public void broadcastColoredMessage(@NotNull String message, boolean reallySend) {
        if (reallySend) broadcastColoredMessage(message);
    }
    public void broadcastColoredMessage(@NotNull String message) {
        Validate.notNull(message);
        getServer().getConsoleSender().sendMessage("§8[§6" + getDescription().getName() + "§8]§7" + " " + ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand("dataapi").setExecutor(new GeneralCommands());

        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        if (hasWrongConfiguration(
                "variable messages",
                "table messages",
                "database messages",

                "check-updates",

                "auto saver time",
                "auto saver message"
        )) {
            if (new File(getDataFolder(), "config.yml").delete()) {
                saveDefaultConfig();
                config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
                broadcastColoredMessage("§cYour configuration file is broken, reseted.");
            }
        }

        new Thread(new Updater()).start();

        Bukkit.getPluginManager().registerEvents(new BukkitEvents(), this);

        long interval = config.getLong("auto saver time") * 20;
        Bukkit.getScheduler().runTaskTimer(plugin(), () -> {
            boolean save = false;
            for (Map.Entry<Table, Map<String, Receptor>> map : getReceptors().entrySet()) {
                for (Map.Entry<String, Receptor> mapTwo : getReceptors().get(map.getKey()).entrySet()) {
                    mapTwo.getValue().save();
                    save = true;
                }
            }
            if (save) {
                if (config.getBoolean("auto saver message")) {
                    broadcastColoredMessage("&aSuccessfully saved all receptors.");
                }
            }
        }, (interval / 2), interval);
    }

    @Override
    public void onDisable() {
        for (Map.Entry<Table, Map<String, Receptor>> map : getReceptors().entrySet()) {
            for (Map.Entry<String, Receptor> mapTwo : new HashMap<>(map.getValue()).entrySet()) {
                Receptor receptor = mapTwo.getValue();
                receptor.unload(receptor.autoSaveWhenServerClose());
            }
        }
    }

    public Metrics getMetrics() {
        return metrics;
    }

    private boolean hasWrongConfiguration(String... strings) {
        for (String str : strings) {
            if (!config.contains(str)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public YamlConfiguration getConfig() {
        return config;
    }

    public static class API {
        public static List<String> getAllReceptorsAt(@NotNull Table table) {
            Validate.notNull(table);

            List<String> list = new ArrayList<>();

            String query = table.getDatabase().getConnectionType().getSelectQuery("*", table.getBruteId(), "", table.getDatabase().getBruteId());
            try (ResultSet result2 = table.getDatabase().createStatement().executeQuery(query)) {
                while (result2.next()) {
                    list.add(result2.getString("bruteid"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return list;
        }
    }

}
