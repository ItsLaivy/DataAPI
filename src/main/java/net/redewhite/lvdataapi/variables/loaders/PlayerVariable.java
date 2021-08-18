package net.redewhite.lvdataapi.variables.loaders;

import net.redewhite.lvdataapi.utils.VariableCreationController;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static net.redewhite.lvdataapi.LvDataAPI.getInactiveVariables;
import static net.redewhite.lvdataapi.LvDataAPI.variableType.*;
import static net.redewhite.lvdataapi.LvDataAPI.getPlayers;

public class PlayerVariable {

    private final VariableCreationController variable;
    private final Player player;
    private Object value;

    public PlayerVariable(Player player, VariableCreationController variable, Object value) {
        this.variable = variable;
        this.player = player;
        this.value = value;

        if (variable == null) throw new NullPointerException("Variable cannot be null!");
        else if (player == null) throw new NullPointerException("Variable player cannot be null!");

        if (variable.getType() == NORMAL || variable.getType() == ARRAY) {
            ArrayList<InactivePlayerVariable> array = new ArrayList<>();
            for (InactivePlayerVariable var : getInactiveVariables().keySet()) {
                if (var.getOwner() == player) {
                    if (var.getName().equals(variable.getVariableName())) {
                        this.value = var.getValue();
                        array.add(var);
                    }
                }
            } for (InactivePlayerVariable e : array) getInactiveVariables().remove(e);
        }

        getPlayers().put(this, player);
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
