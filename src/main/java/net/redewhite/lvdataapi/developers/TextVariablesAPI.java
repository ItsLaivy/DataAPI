package net.redewhite.lvdataapi.developers;

import net.redewhite.lvdataapi.variables.receptors.TextVariableReceptor;
import org.bukkit.Bukkit;

import static net.redewhite.lvdataapi.developers.DatabaseAPI.*;
import static net.redewhite.lvdataapi.LvDataAPI.instance;

public class TextVariablesAPI {

    public static void loadTextType(TextVariableReceptor textVariable) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> loadDatabaseTextVariables(textVariable));
    }
    public static void loadTextType(TextVariableReceptor textVariable, Boolean async) {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(instance, () -> loadDatabaseTextVariables(textVariable));
        else loadDatabaseTextVariables(textVariable);
    }

    public static void saveTextType(TextVariableReceptor textVariable, Boolean async) {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(instance, () -> databaseSaveText(textVariable));
        else databaseSaveText(textVariable);
    }

    public static void unloadTextType(TextVariableReceptor textVariable) {
        unloadDatabaseTextVariables(textVariable);
    }

}
