package net.redewhite.lvdataapi.types;

import net.redewhite.lvdataapi.DataAPI;

public enum ConnectionType {
    SQLITE(
            "INSERT INTO '%1' (name, bruteid, last_update) VALUES ('%2', '%3', '%4');",
            "SELECT %1 FROM '%2' %3;",
            "CREATE TABLE '%1' ('id' INT AUTO_INCREMENT PRIMARY KEY, 'name' TEXT, 'bruteid' TEXT, 'last_update' TEXT)",
            "ALTER TABLE '%1' ADD COLUMN '%2' TEXT DEFAULT '%3';",
            "UPDATE '%1' SET %2 WHERE bruteid = '%3';"
    ),
    MYSQL(
            "INSERT INTO %5.%1 (id, name, bruteid, last_update) VALUES (DEFAULT, '%2', '%3', '%4');",
            "SELECT %2.%1 FROM %4.%2 %3;",
            "CREATE TABLE %2.%1 (id INT AUTO_INCREMENT PRIMARY KEY, name TEXT, bruteid TEXT, last_update TEXT)",
            "ALTER TABLE %4.%1 ADD COLUMN %2 TEXT DEFAULT '%3';",
            "UPDATE %4.%1 SET %2 WHERE bruteid = '%3';"
    );

    private final String INSERT;
    private final String SELECT;
    private final String TABLECREATION;
    private final String VARIABLECREATION;
    private final String UPDATE;

    ConnectionType(String insertQuery, String selectQuery, String creationTableQuery, String creationVariableQuery, String updateQuery) {
        INSERT = insertQuery;
        SELECT = selectQuery;
        TABLECREATION = creationTableQuery;
        VARIABLECREATION = creationVariableQuery;
        UPDATE = updateQuery;
    }

    public String getInsertQuery(String table, String name, String bruteID, String database) {
        return replace(INSERT, table, name, bruteID, DataAPI.getDate(), database);
    }
    public String getSelectQuery(String id, String table, String where, String database) {
        return replace(SELECT, id, table, where, database);
    }
    public String getTableCreationQuery(String table, String database) {
        return replace(TABLECREATION, table, database);
    }
    public String getVariableCreationQuery(String table, String variable, String variableDefault, String database) {
        return replace(VARIABLECREATION, table, variable, variableDefault, database);
    }
    public String getUpdateQuery(String table, String query, String bruteID, String database) {
        return replace(UPDATE, table, query, bruteID, database);
    }

    public static String replace(String replaceFrom, String... to) {
        int row = 1;
        for (String replace : to) {
            replaceFrom = replaceFrom.replace("%" + row, replace);
            row++;
        }
        return replaceFrom;
    }
}