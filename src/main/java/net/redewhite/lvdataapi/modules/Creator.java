package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.DataAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

abstract class Creator {

    protected String name;
    protected final Plugin plugin;
    private final CreatorType creatorType;

    private final String bruteId;

    public Creator(Plugin plugin, String name, CreatorType type) {
        this(plugin, name, type, null);
    }
    public Creator(Plugin plugin, String name, CreatorType type, String bruteId) {
        if (plugin == null) plugin = DataAPI.INSTANCE;
        if (name == null || name.equals("")) name = "default";

        if (type == CreatorType.RECEPTOR_CREATOR) {
            Validate.notNull(bruteId);
            this.bruteId = bruteId;
        } else {
            Validate.notNull(name);
            this.bruteId = plugin.getName() + "_" + name;
        }

        this.name = name;
        this.plugin = plugin;
        this.creatorType = type;

        List<String> allowedChars = Arrays.asList("abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""));
        for (String n : getName().split("")) {
            if (!allowedChars.contains(n)) {
                throw new IllegalStateException("That's " + getCreatorType().name + " name contains illegal characters (" + n + ") - (Name: " + getName() + ", Plugin: " + getPlugin().getName());
            }
        }

        if (getBruteId().length() > 64) {
            throw new IllegalArgumentException(getCreatorType().name + " id (bruteId) is too big (BruteId: " + getBruteId() + ", Plugin: " + getPlugin().getName() + ", Name:" + getName() + ")");
        }
    }

    public String getName() {
        return name;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public CreatorType getCreatorType() {
        return creatorType;
    }

    public String getBruteId() {
        return bruteId;
    }

    protected enum CreatorType {
        DATABASE_CREATOR("database"),
        TABLE_CREATOR("table"),
        VARIABLE_CREATOR("variable"),
        RECEPTOR_CREATOR("receptor");

        private final String name;

        CreatorType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
