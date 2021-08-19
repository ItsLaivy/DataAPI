package net.redewhite.lvdataapi.variables.loaders;

import net.redewhite.lvdataapi.utils.VariableCreationController;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static net.redewhite.lvdataapi.LvDataAPI.getInactivePlayerVariables;
import static net.redewhite.lvdataapi.LvDataAPI.getPlayerVariables;
import static net.redewhite.lvdataapi.LvDataAPI.variableType.*;

public class PlayerVariableLoader {

    private final VariableCreationController variable;
    private final Player player;
    private Object value;

    public PlayerVariableLoader(Player player, VariableCreationController variable, Object value) {
        if (variable == null) throw new NullPointerException("Variable cannot be null!");
        else if (player == null) throw new NullPointerException("Variable player cannot be null!");

        this.variable = variable;
        this.player = player;
        this.value = value;

        if (variable.getType() == NORMAL || variable.getType() == ARRAY) {
            ArrayList<InactivePlayerLoader> array = new ArrayList<>();
            for (InactivePlayerLoader var : getInactivePlayerVariables().keySet()) {
                if (var.getOwner() == player) {
                    if (var.getName().equals(variable.getVariableName())) {
                        this.value = var.getValue();
                        array.add(var);
                    }
                }
            } for (InactivePlayerLoader e : array) getInactivePlayerVariables().remove(e);
        }

        getPlayerVariables().put(this, player);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public VariableCreationController getVariable() {
        return variable;
    }
    public Player getPlayer() {
        return player;
    }
    public Object getValue() {
        return value;
    }
}
