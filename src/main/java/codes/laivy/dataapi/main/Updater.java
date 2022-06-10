package codes.laivy.dataapi.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.istack.internal.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static codes.laivy.dataapi.main.DataAPI.plugin;

public class Updater implements Runnable {

    private static String updateAvailable = null;

    @Override
    public void run() {
        if (plugin().getConfig().getBoolean("check-updates")) {
            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL("https://api.github.com/repos/ItsLaivy/DataAPI/releases");
                URLConnection urlConnection = url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                bufferedReader.close();
            } catch (Exception e) {
                return;
            }

            List<String> releases = new ArrayList<>();
            JsonArray dataJson = new JsonParser().parse(content.toString()).getAsJsonArray();
            for (JsonElement e : dataJson) {
                JsonObject obj = e.getAsJsonObject();
                if (!obj.get("prerelease").getAsBoolean()) {
                    releases.add(obj.get("tag_name").getAsString());
                }
            }

            if (!releases.get(0).equals(plugin().getDescription().getVersion())) {
                plugin().broadcastColoredMessage("&cA new version of &6LvDataAPI &cis already available.");
                plugin().broadcastColoredMessage("&cDownload Link: &6https://github.com/ItsLaivy/DataAPI/releases/" + releases.get(0) + "/");
                plugin().broadcastColoredMessage("&cNew version: &6" + releases.get(0) + "&c, Your version: &6" + plugin().getDescription().getVersion() + "&c.");

                updateAvailable = releases.get(0);
            }
        }
    }

    public static boolean hasNewVersion() {
        return updateAvailable != null;
    }
    @NotNull
    public static String getNewVersion() {
        return updateAvailable;
    }

    public static int versionNumber() {
        return 400;
    }

}
