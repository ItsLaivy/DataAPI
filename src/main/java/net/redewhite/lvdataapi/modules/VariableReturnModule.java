package net.redewhite.lvdataapi.modules;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class VariableReturnModule {

    private final Object value;

    public VariableReturnModule(Object value) {
        this.value = value;
    }

    public String asString() {
        if (value == null) return null;

        return value.toString();
    }

    public boolean isNull() {
        return value == null;
    }

    public Integer asInt() {
        if (value == null) return null;

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ignore) {
            try {
                return ((int) Double.parseDouble(value.toString()));
            } catch (NumberFormatException ignore2) {
                throw new NumberFormatException("thats value cannot be cast to integer.");
            }
        }
    }
    public Long asLong() {
        if (value == null) return null;

        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ignore) {
            try {
                return ((long) Double.parseDouble(value.toString()));
            } catch (NumberFormatException ignore2) {
                throw new NumberFormatException("thats value cannot be cast to long.");
            }
        }
    }
    public Double asDouble() {
        if (value == null) return null;

        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("thats value cannot be cast to double.");
        }
    }
    public List<Object> asList() {
        if (value == null) return new ArrayList<>();

        String[] split = value.toString().split("<SPLIT!>");
        return new ArrayList<>(Arrays.asList(split));
    }
    public Object asList(int i) {
        if (value == null) return new ArrayList<>();

        String[] split = value.toString().split("<SPLIT!>");
        return Arrays.asList(split).get(i);
    }

    public Byte asByte() {
        if (value == null) return null;

        try {
            return Byte.parseByte(value.toString());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("thats value cannot be cast to byte.");
        }
    }
    public Boolean asBoolean() {
        if (value == null) return null;

        try {
            return Boolean.parseBoolean(value.toString());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("thats value cannot be cast to boolean.");
        }
    }

    @Override
    public String toString() {
        if (value == null) return null;

        return value.toString();
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
}
