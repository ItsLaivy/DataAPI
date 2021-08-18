package net.redewhite.lvdataapi.utils;

import static net.redewhite.lvdataapi.utils.YamlDatabaseAPI.loadPlayerVariable;
import static net.redewhite.lvdataapi.utils.SQLDatabaseAPI.createColumn;
import static net.redewhite.lvdataapi.LvDataAPI.databaseConnection.*;
import static net.redewhite.lvdataapi.LvDataAPI.variableType.*;
import static net.redewhite.lvdataapi.LvDataAPI.*;

public class VariableCreationAPI {

    public static String parseType(VariableCreationController variable) {
        if (variable.getType() == NORMAL) {
            try {
                Integer.parseInt(String.valueOf(variable.getValue()));
                return "INT";
            } catch (IllegalArgumentException ignore) {
                return "TEXT";
            }
        }
        else if (variable.getType() == ARRAY) return "OBJECT";
        else if (variable.getType() == TEMPORARY) return "OBJECT";
        return null;
    }

    public static Integer tryCreateColumn(VariableCreationController variable) {
        if (database_type == SQLITE || database_type == MYSQL) {
            return createColumn(variable);
        } else if (database_type == YAML) {
            loadPlayerVariable(variable);
            return 3;
        }
        return 0;
    }

}
