package net.redewhite.lvdataapi.variables.loaders;

import net.redewhite.lvdataapi.utils.VariableCreationController;
import org.bukkit.entity.Player;

import static net.redewhite.lvdataapi.LvDataAPI.getInactiveVariables;
import static net.redewhite.lvdataapi.LvDataAPI.getVariables;

public class InactivePlayerVariable {

    private final Player owner;
    private final Object value;
    private final String name;

    public InactivePlayerVariable(String varname, Object value, Player owner) {
        if (varname == null) throw new NullPointerException("Variable name cannot be null!");
        else if (owner == null) throw new NullPointerException("Variable data type cannot be null!");

        this.owner = owner;
        this.name = varname;
        this.value = value;
        for (VariableCreationController var : getVariables().keySet()) {
            if (var.getVariableName().equals(name)) {
                new PlayerVariable(owner, var, value);
                return;
            }
        }
        getInactiveVariables().put(this, name);
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
