package net.redewhite.lvdataapi.types;

import net.redewhite.lvdataapi.types.variables.Pair;

import java.util.List;
import java.util.Map;

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

    public static String replaceListVariable(List<?> listToString) {
        return listToString.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
    }
    public static String replaceMapVariable(Map<?, ?> mapToString) {
        return mapToString.toString().replace(", ", "<SPLIT!>").replace("{", "").replace("}", "").replace("=", "<MAPSPLIT!>");
    }
    public static String replacePairVariable(Pair<?, ?> pairToString) {
        return pairToString.toString().replace("=", "<PAIRSPLIT!>");
    }

}
