package net.redewhite.lvdataapi.variables.loaders;

import net.redewhite.lvdataapi.utils.VariableCreationController;
import org.bukkit.entity.Player;

import static net.redewhite.lvdataapi.LvDataAPI.getInactivePlayerVariables;
import static net.redewhite.lvdataapi.LvDataAPI.getVariables;

public class InactivePlayerLoader {

    private final Object value;
    private final Player owner;
    private final String name;

    public InactivePlayerLoader(String varname, Object value, Player owner) {
        if (varname == null) throw new NullPointerException("Variable name cannot be null!");
        else if (owner == null) throw new NullPointerException("Variable data type cannot be null!");

        this.name = varname;
        this.owner = owner;
        this.value = value;

        for (VariableCreationController var : getVariables().keySet()) {
            if (var.getVariableName().equals(name)) {
                new PlayerVariableLoader(owner, var, value);
                return;
            }
        }
        getInactivePlayerVariables().put(this, name);
    }

    public Player getOwner() {
        return owner;
    }
    public Object getValue() {
        return value;
    }
    public String getName() {
        return name;
    }

}
