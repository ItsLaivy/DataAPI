package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.events.variables.ActiveVariableValueChangeEvent;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import net.redewhite.lvdataapi.types.VariablesType;
import org.bukkit.Bukkit;

import java.util.*;

import static net.redewhite.lvdataapi.DataAPI.getVariableHashedValue;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class VariableValue {

    private final ActiveVariable variable;

    public VariableValue(ActiveVariable variable) {
        this.variable = variable;
    }

    public void setValue(Object value) {
        ActiveVariableValueChangeEvent event = new ActiveVariableValueChangeEvent(this.variable, value);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        String newValue = null;

        if (value != null) {
            if (value instanceof List) {
                ArrayList<Object> array = new ArrayList<>();
                for (Object e : (ArrayList<?>) value) {
                    array.add(getVariableHashedValue(e));
                }

                if (array.size() != 0) {
                    newValue = array.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
                }
            } else if (value instanceof Map) {
                Map<String, String> map = new HashMap<>();
                for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
                    map.put(getVariableHashedValue(e.getKey()), getVariableHashedValue(e.getValue()));
                }

                if (map.size() != 0) {
                    newValue = map.toString().replace(", ", "<SPLIT!>").replace("{", "").replace("}", "").replace("=", "<MAPSPLIT!>");
                }
            } else {
                newValue = getVariableHashedValue(value);
            }
        }

        if (getVariableHashedValue(variable.getValue()).equals(newValue)) return;
        variable.setValue(newValue);
    }
    public void removeValue(Object value) {
        if (value == null) return;

        if (variable.getValue() == null) {
            variable.setValue(value);
            return;
        }

        if (variable.getVariable().getType() == VariablesType.LIST) {
            List<Object> newList = new ArrayList<>();
            List<Object> addToNewList = new ArrayList<>();
            if (value instanceof List) {
                addToNewList.addAll((ArrayList<?>) value);
            } else {
                addToNewList.add(value);
            }

            for (Object a : addToNewList) {
                boolean success = false;
                for (Object e : variable.getValueModule().asList()) {
                    if (!success) {
                        if (e.equals(a)) {
                            success = true;
                            continue;
                        }
                    }
                    newList.add(e);
                }
            }

            String e = newList.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
            if (newList.size() == 0) variable.setValue(null);
            else variable.setValue(e);
        } else if (variable.getVariable().getType() == VariablesType.MAP) {
            Map<String, String> newMap = asMap();
            List<Object> keys = new ArrayList<>();

            if (value instanceof Map) {
                for (Object e : ((Map<?, ?>) value).keySet()) {
                    keys.add(e.toString());
                }
            } else {
                keys.add(value.toString());
            }

            for (Object a : keys) {
                newMap.remove(a.toString());
            }

            String e = newMap.toString().replace(", ", "<SPLIT!>").replace("{", "").replace("}", "").replace("=", "<MAPSPLIT!>");
            if (newMap.size() == 0) variable.setValue(null);
            else variable.setValue(e);
        } else {
            try {
                Double.parseDouble(value.toString());
            } catch (NumberFormatException ignore) {
                try {
                    Double.parseDouble(variable.getValue().toString());
                } catch (NumberFormatException ignore2) {
                    throw new IllegalStateException("the variable value isn't a number");
                }
                throw new IllegalStateException("the value isn't a number");
            } try {
                Double.parseDouble(variable.getValue().toString());
            } catch (NumberFormatException ignore2) {
                throw new IllegalStateException("the variable value isn't a number");
            }

            double num1 = Double.parseDouble(variable.getValue().toString());
            double num2 = Double.parseDouble(value.toString());

            variable.setValue(num1 - num2);
        }
    }
    public void addValue(Object value) {
        if (value == null) return;

        if (variable.getValue() == null) {
            variable.setValue(value);
            return;
        }

        if (variable.getVariable().getType() == VariablesType.LIST) {
            List<Object> newList = asList();
            if (value instanceof List) {
                newList.addAll((ArrayList<?>) value);
            } else {
                newList.add(value);
            }

            String e = newList.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
            variable.setValue(e);
        } else if (variable.getVariable().getType() == VariablesType.MAP) {
            List<Object> keys = new ArrayList<>();
            if (value instanceof Map) {
                keys.addAll(((Map<?, ?>) value).keySet());
            } else {
                keys.add(value);
            }

            Map<String, String> map = asMap();
            for (Object key : keys) {
                map.remove(key.toString());
            }

            String e = map.toString().replace(", ", "<SPLIT!>").replace("{", "").replace("}", "").replace("=", "<MAPSPLIT!>");
            variable.setValue(e);
        } else {
            if (value instanceof Number && variable.getValue() instanceof Number) {
                throw new IllegalStateException("Esse valor ou o valor da variável não é um número");
            }

            double num1 = Float.parseFloat(variable.getValue().toString());
            double num2 = Float.parseFloat(value.toString());

            variable.setValue(num1 + num2);
        }
    }

    public String asString() {
        if (value() == null) return null;
        return value().toString();
    }

    public Map<String, String> asMap() {
        if (value() == null) return null;
        Map<String, String> map = new HashMap<>();
        for (String e : value().toString().split("<SPLIT!>")) {
            String[] split = e.split("<MAPSPLIT!>");
            map.put(split[0], split[1]);
        }

        return map;
    }

    public boolean isNull() {
        return value() == null;
    }

    public Integer asInt() {
        if (value() == null) return null;
        return Integer.parseInt(value().toString());
    }

    public Long asLong() {
        if (value() == null) return null;

        try {
            return Long.parseLong(value().toString());
        } catch (NumberFormatException ignore) {
            return ((long) Double.parseDouble(value().toString()));
        }
    }

    public Double asDouble() {
        if (value() == null) return null;
        return Double.parseDouble(value().toString());
    }

    public List<Object> asList() {
        if (value() == null) return new ArrayList<>();
        String[] split = value().toString().split("<SPLIT!>");
        return new ArrayList<>(Arrays.asList(split));
    }
    public Object asList(int index) {
        if (value() == null) return new ArrayList<>();
        String[] split = value().toString().split("<SPLIT!>");
        return Arrays.asList(split).get(index);
    }

    public Byte asByte() {
        if (value() == null) return null;
        return Byte.parseByte(value().toString());
    }

    public Boolean asBoolean() {
        if (value() == null) return null;
        return Boolean.parseBoolean(value().toString());
    }

    public UUID asUUID() {
        if (value() == null) return null;
        return UUID.fromString(value().toString());
    }

    public Object get() {
        return value();
    }

    @Override
    public String toString() {
        if (value() == null) return null;

        return value().toString();
    }

    Object value() {
        return variable.getValue();
    }
}
