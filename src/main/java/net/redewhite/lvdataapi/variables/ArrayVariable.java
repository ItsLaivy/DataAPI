package net.redewhite.lvdataapi.variables;

import net.redewhite.lvdataapi.LvDataPlugin;
import net.redewhite.lvdataapi.developers.API;
import net.redewhite.lvdataapi.database.DatabaseConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;

import static net.redewhite.lvdataapi.LvDataPlugin.*;
import static net.redewhite.lvdataapi.database.DatabaseConnection.createStatement;

public class ArrayVariable {

    private final String name;
    private final String varname;
    private final Object value;
    private final Plugin plugin;
    private final String type;

    public ArrayVariable(Plugin plugin, String name, ArrayList value) {

        ArrayList<String> finalArray = new ArrayList<>();
        if (value != null) {
            for (Object str : value) {
                finalArray.add(str.toString().replace(",", "<COMMA>"));
            }
        } else {
            finalArray.add("");
        }

        this.plugin = plugin;
        this.name = name;
        this.value = finalArray.toString().replace("[", "").replace("]", "");
        this.varname = plugin.getName() + "_ARRAYLIST_" + name;
        this.type = "TEXT";

        for (ArrayVariable var : arrayvariables.keySet()) {
            if (var.getVariableName().equalsIgnoreCase(varname)) {
                return;
            }
        }

        if (name.contains("-")) {
            broadcastColoredMessage("§cArray variable '§4" + name + "§c' couldn't be created because it has illegal characters ('§4-§c')");
            return;
        }

        int trycreate = tryCreateColumn();
        if (trycreate == 1 || trycreate == 2) {
            arrayvariables.put(this, varname);
            if (trycreate == 1) {
                broadcastColoredMessage("§aSuccessfully loaded array variable '§2" + name + "§a' of the plugin '§2" + plugin.getName() + "§a'.");

                Bukkit.getScheduler().runTaskAsynchronously(LvDataPlugin.getInstance(), () -> {
                    for (Player player : instance.getServer().getOnlinePlayers()) {
                        if (API.isLoaded(player)) {
                            Statement statement = createStatement();
                            assert statement != null;

                            try (ResultSet result = statement.executeQuery("SELECT " + varname + " FROM `" + tableName + "` WHERE uuid = '" + player.getUniqueId() + "';")) {
                                while (result.next()) {
                                    new PlayerVariable(player, plugin, name, result.getObject(varname), variableType.ARRAY);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } else {
                broadcastColoredMessage("§aSuccessfully created array variable '§2" + name + "§a' of the plugin '§2" + plugin.getName() + "§a'.");
                for (Player player : instance.getServer().getOnlinePlayers()) {
                    new PlayerVariable(player, plugin, name, value, variableType.ARRAY);
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
                broadcastColoredMessage("§cSQLite array variable named '§4" + name + "§c' couldn't be created.");
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
    public Plugin getPlugin() {
        return plugin;
    }
    public variableType getVariableType() {
        return variableType.ARRAY;
    }
}
