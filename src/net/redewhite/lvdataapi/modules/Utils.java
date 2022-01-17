package net.redewhite.lvdataapi.modules;

import org.bukkit.plugin.Plugin;

import static net.redewhite.lvdataapi.DataAPI.broadcastColoredMessage;

class Utils {

    protected static boolean iS(String s, Plugin plugin, String name) {
        String[] blocked = "-S,S=S[S]S.S/S*S-S+S;S:S(S)".split("S");
        for (String block : blocked) {
            if (name.contains(block)) {
                broadcastColoredMessage("&cThat's " + s + " name contains illegal characters (" + block + ") - (Name: " + name + ", Plugin: " + plugin.getName());
                return true;
            }
        }

        return false;
    }
    protected static void bG(String s, Plugin plugin, String bruteID) {
        if (bruteID.length() > 64) {
            throw new IllegalArgumentException(s + " name (bruteID) is too big (BruteID: " + bruteID + ", Plugin: " + plugin.getName() + ")");
        }
    }

}
