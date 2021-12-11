package net.redewhite.lvdataapi.types;

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

    private final String insertQuery;
    private final String selectQuery;
    private final String creationTableQuery;
    private final String creationVariableQuery;
    private final String updateQuery;

    ConnectionType(String insertQuery,
                   String selectQuery,
                   String creationTableQuery,
                   String creationVariableQuery,
                   String updateQuery
    ) {
        this.insertQuery = insertQuery;
        this.selectQuery = selectQuery;
        this.creationTableQuery = creationTableQuery;
        this.creationVariableQuery = creationVariableQuery;
        this.updateQuery = updateQuery;
    }

    public String getUpdateQuery() {
        return updateQuery;
    }
    public String getCreationVariableQuery() {
        return creationVariableQuery;
    }
    public String getInsertQuery() {
        return insertQuery;
    }
    public String getSelectQuery() {
        return selectQuery;
    }
    public String getCreationTableQuery() {
        return creationTableQuery;
    }

    public static String replace(String replaceFrom, String... to) {
        String value = replaceFrom;
        int row = 1;

        for (String replace : to) {
            value = value.replace("%" + row, replace);
            row++;
        }
        return value;
    }
}
