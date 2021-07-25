package net.redewhite.LVDataAPI.Database;

import net.redewhite.LVDataAPI.Main;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Variable {

    private final String name;
    private final String varname;
    private final Object value;
    private String type;

    public Variable(Plugin plugin, String name, Object value) {
        this.name = name;
        this.value = value;
        this.varname = plugin.getName() + "_" + name;

        try {
            Integer.parseInt(String.valueOf(value));
            this.type = "INT";
        } catch (IllegalArgumentException e) {
            this.type = "TEXT";
        }

        int trycreate = tryCreateColumn();
        if (trycreate == 1 || trycreate == 2) {
            Main.dataapi.put(this, varname);
            if (trycreate == 1) {
                Main.broadcastInfo("Successfully loaded variable '" + name + "' of the plugin '" + plugin.getName() + "'.");
            } else {
                Main.broadcastInfo("Successfully created variable '" + name + "' of the plugin '" + plugin.getName() + "'.");
            }
        }
    }

    private Integer tryCreateColumn() {
        try {
            PreparedStatement pst = SQLiteConnection.conn.prepareStatement("ALTER TABLE `wn_data` ADD COLUMN " + varname + " " + type + " DEFAULT '" + value + "';");
            pst.execute();
            pst.close();
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name:")) {
                e.printStackTrace();
                Main.broadcastWarn("SQLite variable named '" + name + "' couldn't be created.");
                return 0;
            } else {
                return 1;
            }
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
}
