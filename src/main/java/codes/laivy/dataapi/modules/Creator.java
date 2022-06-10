package codes.laivy.dataapi.modules;

import codes.laivy.dataapi.main.DataAPI;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public abstract class Creator {

    @NotNull
    public static String getBruteIdOf(CreatorType type, Plugin plugin, String name, String bruteId) {
        if (type == CreatorType.RECEPTOR_CREATOR) {
            Validate.notNull(bruteId, "Brute Id cannot be null");
            return bruteId;
        } else {
            Validate.notNull(name, "Name cannot be null");
            if (type == CreatorType.DATABASE_CREATOR) {
                if (name.equals("default")) return plugin.getName() + "_" + name;
                return name;
            } else {
                Validate.notNull(plugin);
                Validate.notNull(name);
                return plugin.getName() + "_" + name;
            }
        }
    }

    protected String name;
    protected final Plugin plugin;
    private final CreatorType creatorType;

    private final String bruteId;

    public Creator(@Nullable Plugin plugin, @Nullable String name, @NotNull CreatorType type) {
        this(plugin, name, type, null);
    }
    public Creator(@Nullable Plugin plugin, @Nullable String name, @NotNull CreatorType type, String bruteId) {
        Validate.notNull(type);

        if (plugin == null) plugin = DataAPI.plugin();
        if (name == null || name.equals("")) name = "default";

        this.bruteId = getBruteIdOf(type, plugin, name, bruteId);

        this.name = name;
        this.plugin = plugin;
        this.creatorType = type;

        List<String> allowedChars = Arrays.asList("abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".split(""));
        for (String n : getName().split("")) {
            if (!allowedChars.contains(n)) {
                throw new IllegalStateException("That's " + getCreatorType().name + " name contains illegal characters (" + n + ") - (Name: " + getName() + ", Plugin: " + getPlugin().getName());
            }
        }

        if (getBruteId().length() > 128) {
            throw new IllegalArgumentException(getCreatorType().name + " id (bruteId) is too big, the maximum allowed is 128 (BruteId: " + getBruteId() + ", Plugin: " + getPlugin().getName() + ", Name:" + getName() + ")");
        }
        if (getName().length() > 128) {
            throw new IllegalArgumentException("that's name \"" + getCreatorType().name + "\" is too long, the maximum allowed is 128. (BruteId: " + getBruteId() + ", Plugin: " + getPlugin().getName() + ")");
        }
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Plugin getPlugin() {
        return plugin;
    }

    @NotNull
    public CreatorType getCreatorType() {
        return creatorType;
    }

    @NotNull
    public String getBruteId() {
        return bruteId;
    }

    public enum CreatorType {
        DATABASE_CREATOR("database"),
        TABLE_CREATOR("table"),
        VARIABLE_CREATOR("variable"),
        RECEPTOR_CREATOR("receptor");

        private final String name;

        CreatorType(String name) {
            this.name = name;
        }

        @NotNull
        public String getName() {
            return name;
        }
    }

}
