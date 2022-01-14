package net.redewhite.lvdataapi.types;

public enum VariablesType {

    NORMAL("Variable"),
    LIST("§6ListVariable"),
    TEMPORARY("§bTemporary"),
    MAP("§5MapVariable"),
    PAIR("§9PairVariable");

    private final String name;

    VariablesType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
