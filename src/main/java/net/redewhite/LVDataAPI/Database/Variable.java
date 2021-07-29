package net.redewhite.lvdataapi.database;

import net.redewhite.lvdataapi.LvDataPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        if (name.contains("-")) {
            LvDataPlugin.broadcastWarn("Variable '" + name + "' couldn't be created because it has illegal characters ('-')");
            return;
        }

        try {
            Integer.parseInt(String.valueOf(value));
            this.type = "INT";
        } catch (IllegalArgumentException e) {
            this.type = "TEXT";
        }

        int trycreate = tryCreateColumn();
        if (trycreate == 1 || trycreate == 2) {
            LvDataPlugin.dataapi.put(this, varname);
            if (trycreate == 1) {
                LvDataPlugin.broadcastInfo("Successfully loaded variable '" + name + "' of the plugin '" + plugin.getName() + "'.");

                Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
                    for (Player player : LvDataPlugin.instance.getServer().getOnlinePlayers()) {
                        ResultSet result;
                        Statement statement = createStatement();

                        try {
                            assert statement != null;
                            result = statement.executeQuery("SELECT " + varname + " FROM `wn_data` WHERE uuid = '" + player.getUniqueId() + "';");
                            while (result.next()) {
                                new PlayerVariable(player, plugin, name, result.getObject(varname));
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });

            } else {
                LvDataPlugin.broadcastInfo("Successfully created variable '" + name + "' of the plugin '" + plugin.getName() + "'.");
                for (Player player : LvDataPlugin.instance.getServer().getOnlinePlayers()) {
                    new PlayerVariable(player, plugin, name, value);
                }
            }
        }
    }

    private Integer tryCreateColumn() {
        try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement("ALTER TABLE `wn_data` ADD COLUMN " + varname + " " + type + " DEFAULT '" + value + "';")) {
            pst.execute();
        } catch (SQLException e) {
            if (!e.getMessage().contains("uplicate column name")) {
                if (LvDataPlugin.debug) e.printStackTrace();
                LvDataPlugin.broadcastWarn("SQLite variable named '" + name + "' couldn't be created.");
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
