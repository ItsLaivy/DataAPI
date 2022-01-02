package net.redewhite.lvdataapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.io.*;

import static net.redewhite.lvdataapi.DataAPI.*;

public class Updater implements Runnable {
    @Override
    public void run() {
        if (INSTANCE.getConfig().getBoolean("check-updates")) {
            broadcastColoredMessage("Procurando por atualizações...");
            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL("https://api.github.com/repos/LaivyTLife/DataAPI/releases");
                URLConnection urlConnection = url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                bufferedReader.close();
            } catch (Exception e) {
                broadcastColoredMessage("&cNão foi possível verificar por novas atualizações, você está conectado à internet?");
                return;
            }

            List<String> releases = new ArrayList<>();
            JsonArray dataJson = new JsonParser().parse(content.toString()).getAsJsonArray();
            for (JsonElement e : dataJson) {
                JsonObject obj = e.getAsJsonObject();
                releases.add(obj.get("tag_name").getAsString());
            }

            if (!releases.get(0).equals(DataAPI.version)) {
                broadcastColoredMessage("&cA new version of &6LvDataAPI &cis already available.");
                broadcastColoredMessage("&cDownload Link: &6https://github.com/LaivyTLife/DataAPI/releases/" + releases.get(0) + "/");
                broadcastColoredMessage("&cNew version: &6" + releases.get(0) + "&c, Your version: &6" + version + "&c.");
            } else {
                broadcastColoredMessage("&aVocê está atualizado :)");
            }
        }
    }
}
