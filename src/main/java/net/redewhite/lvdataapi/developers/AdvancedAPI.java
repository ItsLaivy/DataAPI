package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.events.api.receptors.VariableReceptorRegisterEvent;
import net.redewhite.lvdataapi.events.api.receptors.VariableReceptorUnloadEvent;
import net.redewhite.lvdataapi.events.api.receptors.VariableReceptorLoadEvent;
import net.redewhite.lvdataapi.events.api.receptors.VariableReceptorSaveEvent;
import net.redewhite.lvdataapi.loaders.InactiveVariableLoader;
import net.redewhite.lvdataapi.loaders.ActiveVariableLoader;
import net.redewhite.lvdataapi.database.DatabaseConnection;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import net.redewhite.lvdataapi.creators.VariablesTable;
import net.redewhite.lvdataapi.DataAPI;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.sql.*;

import static net.redewhite.lvdataapi.DataAPI.databaseConnection.SQLITE;
import static net.redewhite.lvdataapi.DataAPI.databaseConnection.MYSQL;
import static net.redewhite.lvdataapi.DataAPI.*;

public class AdvancedAPI {

    public static Boolean connection() {
        String databasetype = config.getString("Database Type");
        boolean success = false;

        if (databasetype.equalsIgnoreCase("MYSQL")) {
            if (DatabaseConnection.connect(MYSQL)) success = true;
        } else if (databasetype.equalsIgnoreCase("SQLITE")) {
            if (DatabaseConnection.connect(SQLITE)) success = true;
        } else {
            ArrayList<String> uses = new ArrayList<>();
            for (String f : databasetype.replace(" ", "").split("!>")) {
                if (!success) {
                    if (uses.contains(f)) {
                        Bukkit.getPluginManager().disablePlugin(instance);
                        throw new IllegalArgumentException("You can't execute a database type two times! if then fail, the connection using that (failed) database cannot be estabilished again.");
                    } uses.add(f);
                    if (f.equalsIgnoreCase("MYSQL")) {
                        if (DatabaseConnection.connect(MYSQL)) success = true;
                    } else if (f.equalsIgnoreCase("SQLITE")) {
                        if (DatabaseConnection.connect(SQLITE)) success = true;
                    } else {
                        DataAPI.broadcastColoredMessage("&cWrong config.yml configuration at '&4Database Type&c'. Stopping plugin...");
                        return false;
                    }
                }
            }
        }

        if (!success) {
            getMessage("Connection cant be established");
        } else {
            return true;
        }

        return false;
    }

    public static Boolean isLoaded(String bruteId) {
        for (ActiveVariableLoader var : getActiveVariables().keySet()) {
            if (var.getOwnerBruteId().equals(bruteId)) return true;
        }
        for (InactiveVariableLoader var : getInactiveVariables().keySet()) {
            if (var.getOwnerBruteId().equals(bruteId)) return true;
        }
        return false;
    }

    public static String getVariableHashedValue(Object value) {
        try {
            if (value != null) {
                return value.toString().replace(",", "<!COMMA>").replace("[", "<!RIGHTBRACKET>").replace("]", "<!LEFTBRACKET>").replace("'", "<!SIMPLECOMMA>");
            }
        } catch (NullPointerException ignore) {
        }
        return "<NULL!>";
    }
    public static String getVariableUnhashedValue(Object value) {
        try {
            boolean bool = value.toString().equals("<NULL!>");
            if (!bool) {
                return value.toString().replace("<!COMMA>", ",").replace("<!RIGHTBRACKET>", "[").replace("<!LEFTBRACKET>", "]").replace("<!SIMPLECOMMA>", "'");
            }
        } catch (NullPointerException ignore) {
        }
        return null;
    }

    public static Boolean databaseLoad(VariableReceptor receptor, VariablesTable table) {

        VariableReceptorLoadEvent event = new VariableReceptorLoadEvent(receptor, table);
        instance.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (isLoaded(receptor.getNameBruteId())) return false;

            databaseRegister(receptor.getNameBruteId(), receptor.getName(), table);
            if (database_type == SQLITE || database_type == MYSQL) {
                Statement statement = DatabaseConnection.createStatement();
                assert statement != null;

                try (ResultSet result = statement.executeQuery("SELECT * FROM '" + table.getTableBruteId() + "' WHERE bruteid = '" + receptor.getNameBruteId() + "';")) {
                    while (result.next()) {
                        ResultSetMetaData rmtd = result.getMetaData();
                        for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                            if (row > 4) {
                                new InactiveVariableLoader(table, rmtd.getColumnName(row), result.getObject(row), receptor.getNameBruteId());
                            }
                        }
                    }
                    return true;
                } catch (SQLException e) {
                    if (debug) e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public static Boolean databaseSave(VariableReceptor receptor, VariablesTable table) {

        VariableReceptorSaveEvent event = new VariableReceptorSaveEvent(receptor, table);
        instance.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (database_type == SQLITE || database_type == MYSQL) {
                StringBuilder query = new StringBuilder();
                for (ActiveVariableLoader var : getActiveVariables().keySet()) {
                    if (var.getVariable().getTable() == table) {
                        if (var.getOwnerBruteId().equals(receptor.getNameBruteId())) {
                            query.append(var.getVariable().getVariableBruteId()).append(" = '").append(getVariableHashedValue(var.getValue())).append("', ");
                        }
                    }
                }
                return executeQuery("UPDATE '" + table.getTableBruteId() + "' SET " + query + "last_update = '" + getDate() + "' WHERE bruteid = '" + receptor.getNameBruteId() + "';");
            }
        }
        return false;
    }
    public static Boolean databaseUnload(VariableReceptor receptor, VariablesTable table) {

        VariableReceptorUnloadEvent event = new VariableReceptorUnloadEvent(receptor, table);
        instance.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (database_type == SQLITE || database_type == MYSQL) {
                boolean successfully = databaseSave(receptor, table);
                if (successfully) receptor.unload();
                return successfully;
            }
        }
        return false;
    }
    public static Boolean executeQuery(String query) {
        try (PreparedStatement pst = DatabaseConnection.conn.prepareStatement(query)) {
            pst.execute();
            return true;
        } catch (SQLException e) {
            if (debug) e.printStackTrace();
        }
        return false;
    }

    public static Object getInactiveVariable(String inactive_variable_name, VariableReceptor receptor) {
        for (InactiveVariableLoader var : getInactiveVariables().keySet()) {
            if (var.getOwnerBruteId().equals(receptor.getNameBruteId())) {
                if (var.getVariableBruteId().equals(inactive_variable_name)) {
                    if (var.getValue().equals("")) return null;
                    return var.getValue();
                }
            }
        }
        throw new NullPointerException("Couldn't find any inactive variable with this parameters");
    }

    public static Boolean databaseRegister(String bruteid, String name, VariablesTable table) {

        VariableReceptorRegisterEvent event = new VariableReceptorRegisterEvent(bruteid, name, table);
        instance.getServer().getPluginManager().callEvent(event);

        bruteid = event.getBruteId();
        table = event.getTable();
        name = event.getName();

        if (!event.isCancelled()) {
            if (getDatabaseVariablesAmountOf(bruteid, table) != null) return null;

            try {
                PreparedStatement pstmt = null;
                if (database_type == MYSQL) pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO '" + table.getTableBruteId() + "' (id, name, bruteid, last_update) VALUES (DEFAULT, '" + name + "', '" + bruteid + "', '" + getDate() + "');");
                else if (database_type == SQLITE) pstmt = DatabaseConnection.conn.prepareStatement("INSERT INTO '" + table.getTableBruteId() + "' (name, bruteid, last_update) VALUES ('" + name + "', '" + bruteid + "', '" + getDate() + "');");
                assert pstmt != null;

                pstmt.execute();
                getMessage("Receptor variable created", name, table.getName());
                return true;
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
                getMessage("Receptor variable register error", name, table.getName());
                return false;
            }
        }
        return false;
    }

    public static Integer getDatabaseVariablesAmountOf(String bruteid, VariablesTable table) {
        if (database_type == SQLITE || database_type == MYSQL) {
            Statement statement = DatabaseConnection.createStatement();
            assert statement != null;

            try (ResultSet result = statement.executeQuery("SELECT * FROM '" + table.getTableBruteId() + "' WHERE bruteid = '" + bruteid + "';")) {
                if (result.next()) {
                    ResultSetMetaData rmtd = result.getMetaData();
                    int number = 0;

                    for (int row = 1; row <= rmtd.getColumnCount(); row++) {
                        if (row > 4) number++;
                    }

                    return number;
                }
            } catch (SQLException e) {
                if (debug) e.printStackTrace();
            }
        }
        return null;
    }

}
