package net.redewhite.lvdataapi.database;

import net.redewhite.lvdataapi.LvDataPlugin;
import net.redewhite.lvdataapi.LvDataPluginAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;

import static net.redewhite.lvdataapi.LvDataPlugin.*;
import static net.redewhite.lvdataapi.database.DatabaseConnection.createStatement;

public class Variable {

    private final String name;
    private final String varname;
    private final Object value;
    private final Plugin plugin;
    private String type;

    public Variable(Plugin plugin, String name, Object value) {

        this.plugin = plugin;
        this.name = name;
        this.value = value;
        this.varname = plugin.getName() + "_" + name;

        for (Variable var : LvDataPlugin.variables.keySet()) {
            if (var.getVariableName().equalsIgnoreCase(varname)) {
                return;
            }
        }

        if (name.contains("-")) {
            broadcastColoredMessage("§cVariable '§4" + name + "§c' couldn't be created because it has illegal characters ('§4-§c')");
            return;
        }

        try {
            Integer.parseInt(String.valueOf(value));
            this.type = "INT";
        } catch (IllegalArgumentException ignore) {
            this.type = "TEXT";
        }

        int trycreate = tryCreateColumn();
        if (trycreate == 1 || trycreate == 2) {
            LvDataPlugin.variables.put(this, varname);
            if (trycreate == 1) {
                broadcastColoredMessage("§aSuccessfully loaded variable '§2" + name + "§a' of the plugin '§2" + plugin.getName() + "§a'.");

                Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
                    for (Player player : instance.getServer().getOnlinePlayers()) {
                        if (LvDataPluginAPI.isLoaded(player)) {
                            Statement statement = createStatement();
                            assert statement != null;

                            try (ResultSet result = statement.executeQuery("SELECT " + varname + " FROM `" + tableName + "` WHERE uuid = '" + player.getUniqueId() + "';")) {
                                while (result.next()) {
                                    new PlayerVariable(player, plugin, name, result.getObject(varname), "VARIABLE");
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    }
                });

            } else {
                broadcastColoredMessage("§aSuccessfully created variable '§2" + name + "§a' of the plugin '§2" + plugin.getName() + "§a'.");
                for (Player player : instance.getServer().getOnlinePlayers()) {
                    new PlayerVariable(player, plugin, name, value, "VARIABLE");
                }
            }
        }
    }

    private Integer tryCreateColumn() {
        try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement("ALTER TABLE `" + tableName + "` ADD COLUMN " + varname + " " + type + " DEFAULT '" + value + "';")) {
            pst.execute();
        } catch (SQLException e) {
            if (!e.getMessage().contains("uplicate column name")) {
                if (LvDataPlugin.debug) e.printStackTrace();
                broadcastColoredMessage("§cSQLite variable named '§4" + name + "§c' couldn't be created.");
                return 0;
            } else {
                return 1;
            }
        } catch (NullPointerException e) {
            return 0;
        }
        return 2;
    }

    public String getName() {
        return name;
    }
    public String getVariableName() {
        return varname;
    }
    public String getType() {
        return type;
    }
    public Plugin getPlugin() {
        return plugin;
    }
}
