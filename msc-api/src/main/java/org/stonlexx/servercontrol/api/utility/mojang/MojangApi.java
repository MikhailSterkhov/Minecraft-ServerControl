package org.stonlexx.servercontrol.api.utility.mojang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.stonlexx.servercontrol.api.player.BasePlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class MojangApi {

    public final String UUID_URL_STRING                     = "https://api.mojang.com/users/profiles/minecraft/";
    protected final String SKIN_URL_STRING                  = "https://sessionserver.mojang.com/session/minecraft/profile/";
    public final JsonParser JSON_PARSER                     = new JsonParser();

    public final Map<String, MojangSkin> MOJANG_SKIN_MAP    = new HashMap<>();


    @SneakyThrows
    public String readURL(@NonNull String url) {
        HttpURLConnection connection = ((HttpURLConnection) new URL(url).openConnection());

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", RandomStringUtils.randomAlphanumeric(16));
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);

        StringBuilder output = new StringBuilder();

        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            while (bufferedReader.ready()) {
                output.append(bufferedReader.readLine());
            }
        }

        return output.toString();
    }

    public String getUserUUID(@NonNull String playerName) {
        JsonElement jsonElement = JSON_PARSER.parse(readURL(UUID_URL_STRING + playerName));

        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject().get("id").getAsString();
        }

        return null;
    }

    public String getOriginalName(@NonNull String playerName) {
        JsonElement jsonElement = JSON_PARSER.parse(readURL(UUID_URL_STRING + playerName));

        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject().get("name").getAsString();
        }

        return playerName;
    }

   public boolean isPremium(@NonNull BasePlayer corePlayer) {
       String playerUuid = corePlayer.getUniqueId().toString().replace("-", "");
       String mojangUuid = MojangApi.getUserUUID(corePlayer.getName());

   //    System.out.println("[MojangAPI]:");
   //    System.out.println(" Player: " + corePlayer.getName());
   //    System.out.println(" Current UUID: " + playerUuid);
   //    System.out.println(" Mojang UUID: " + mojangUuid);
   //    System.out.println(" Premium account: " + playerUuid.equals(mojangUuid));

       return playerUuid.equals(mojangUuid);
   }

    @SneakyThrows
    public MojangSkin getMojangSkin(@NonNull String playerSkin) {
        MojangSkin mojangSkin = MOJANG_SKIN_MAP.get(playerSkin.toLowerCase());

        if (mojangSkin != null && !mojangSkin.isExpired()) {
            return mojangSkin;
        }

        String playerUUID = getUserUUID(playerSkin);

        if (playerUUID == null) {
            return getMojangSkin("Steve");
        }

        String skinUrl = readURL(SKIN_URL_STRING + playerUUID + "?unsigned=false");

        JsonObject textureProperty = JSON_PARSER.parse(skinUrl)
                .getAsJsonObject()

                .get("properties")
                .getAsJsonArray()

                .get(0)
                .getAsJsonObject();

        String texture = textureProperty.get("value").getAsString();
        String signature = textureProperty.get("signature").getAsString();

        mojangSkin = new MojangSkin(playerSkin, playerUUID, texture, signature, System.currentTimeMillis());

        MOJANG_SKIN_MAP.put(playerSkin.toLowerCase(), mojangSkin);
        return mojangSkin;
    }
}
