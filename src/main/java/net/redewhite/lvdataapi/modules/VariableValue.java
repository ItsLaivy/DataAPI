package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.events.variables.receptors.ActiveVariableValueChangeEvent;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import net.redewhite.lvdataapi.types.VariablesType;
import net.redewhite.lvdataapi.types.variables.Pair;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import java.util.*;

import static net.redewhite.lvdataapi.DataAPI.getVariableHashedValue;
import static net.redewhite.lvdataapi.DataAPI.getVariableUnhashedValue;
import static net.redewhite.lvdataapi.types.VariablesType.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class VariableValue {

    private final ActiveVariable variable;

    private final VariablesType t;

    public VariableValue(ActiveVariable variable) {
        this.variable = variable;
        this.t = variable.getVariable().getType();
    }

    public VariableValue removeMapValue(String key) {
        return put(key, null);
    }
    public VariableValue put(String key, Object value) {
        Validate.notNull(key, "Key cannot be null");

        if (t == MAP || t == PAIR) {
            if (c(key, value)) {
                return this;
            }

            if (t == MAP) {
                Map<String, String> map = asMapHashed();

                if (value == null) {
                    map.remove(getVariableHashedValue(key));
                } else {
                    map.put(getVariableHashedValue(key), getVariableHashedValue(value));
                }

                variable.setValue(replaceMapVariable(map));
            } else {
                Pair<String, String> pair = new Pair<>(getVariableHashedValue(key), getVariableHashedValue(value));
                variable.setValue(replacePairVariable(pair));
            }
        } else {
            throw new IllegalStateException("The variable needs to be a MapVariable or a PairVariable");
        }

        return this;
    }
    public VariableValue setValue(String key, Object value) {
        return put(key, value);
    }

    public VariableValue setValue(Object value) {
        if (c(null, value)) {
            return this;
        }

        if (value instanceof Map || value instanceof Pair) {
            throw new IllegalStateException("Use setValue(key, value) to change map/pair variable's values");
        }

        String newValue = null;

        if (value != null) {
            if (value instanceof List) {
                ArrayList<Object> array = new ArrayList<>();
                for (Object e : (ArrayList<?>) value) {
                    array.add(getVariableHashedValue(e));
                }

                if (array.size() != 0) {
                    newValue = replaceListVariable(array);
                }
            } else {
                newValue = getVariableHashedValue(value);
            }
        }

        if (getVariableHashedValue(variable.getValue()).equals(newValue)) return this;
        variable.setValue(newValue);

        return this;
    }
    public VariableValue removeValue(Object value) {
        if (c(null, value)) {
            return this;
        }

        if (value == null) return this;

        if (variable.getValue() == null) {
            variable.setValue(value);
            return this;
        }

        if (t == MAP) {
            throw new IllegalStateException("Use removeMapValue() to remove map variable's values");
        }

        if (t == PAIR) {
            throw new IllegalStateException("You cannot remove values from a Pair variable!");
        }

        if (t == LIST) {
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

            String e = replaceListVariable(newList);
            if (newList.size() == 0) variable.setValue(null);
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

        return this;
    }
    public VariableValue addValue(Object value) {
        if (c(null, value)) {
            return this;
        }

        if (value == null) return this;

        if (variable.getValue() == null) {
            variable.setValue(value);
            return this;
        }

        if (value instanceof Map) {
            throw new IllegalStateException("Use addValue(key, value) to add map variable's values");
        }

        if (t == PAIR) {
            throw new IllegalStateException("You cannot add values into a Pair variable!");
        }

        if (t == LIST) {
            List<Object> newList = a();
            if (value instanceof List) {
                newList.addAll((ArrayList<?>) value);
            } else {
                newList.add(value);
            }

            String e = replaceListVariable(newList);
            variable.setValue(e);
        } else {
            if (value instanceof Number && variable.getValue() instanceof Number) {
                throw new IllegalStateException("Esse valor ou o valor da variável não é um número");
            }

            double num1 = Float.parseFloat(variable.getValue().toString());
            double num2 = Float.parseFloat(value.toString());

            variable.setValue(num1 + num2);
        }

        return this;
    }
    private boolean c(String key, Object value) {
        ActiveVariableValueChangeEvent event;

        event = new ActiveVariableValueChangeEvent(!Bukkit.isPrimaryThread(), this.variable, value, key);
        Bukkit.getServer().getPluginManager().callEvent(event);

        return event.isCancelled();
    }

    public String getByKeyAsString(String key) {
        mapCheck(key);
        return asMap().get(key);
    }
    public Integer getByKeyAsInt(String key) {
        mapCheck(key);
        return Integer.parseInt(asMap().get(key));
    }
    public Long getByKeyAsLong(String key) {
        mapCheck(key);
        return Long.parseLong(asMap().get(key));
    }
    public Double getByKeyAsDouble(String key) {
        mapCheck(key);
        return Double.parseDouble(asMap().get(key));
    }
    private void mapCheck(String key) {
        if (t != MAP) {
            throw new IllegalStateException("To use that method the variable type needs to be map");
        }

        if (!asMap().containsKey(key)) {
            throw new NullPointerException("This map doesn't contains that key");
        }
    }


    public String asString() {
        if (value() == null) return null;
        return value().toString();
    }

    public Pair<String, String> asPair() {
        if (value() == null) return new Pair<>();

        String[] split = value().toString().split("<PAIRSPLIT!>");
        return new Pair<>(split[0], split[1]);
    }

    private Map<String, String> asMapHashed() {
        if (value() == null) return new HashMap<>();

        Map<String, String> map = new HashMap<>();
        for (String e : value().toString().split("<SPLIT!>")) {
            String[] split = e.split("<MAPSPLIT!>");
            if (split.length == 2) {
                map.put(split[0], split[1]);
            }
        }

        return map;
    }
    public Map<String, String> asMap() {
        if (value() == null) return new HashMap<>();

        Map<String, String> map = new HashMap<>();
        for (String e : value().toString().split("<SPLIT!>")) {
            String[] split = e.split("<MAPSPLIT!>");
            if (split.length == 2) {
                map.put(getVariableUnhashedValue(split[0]), getVariableUnhashedValue(split[1]));
            }
        }

        return map;
    }

    private List<Object> a() {
        if (value() == null) return new ArrayList<>();

        String[] split = value().toString().split("<SPLIT!>");
        return new ArrayList<>(Arrays.asList(split));
    }

    public List<String> asList() {
        if (value() == null) return new ArrayList<>();

        String[] split = value().toString().split("<SPLIT!>");
        return new ArrayList<>(Arrays.asList(split));
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

    public String asList(int index) {
        return asList().get(index);
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
