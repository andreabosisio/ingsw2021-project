package it.polimi.ingsw.commons;

import com.google.gson.*;

import java.io.*;
import java.util.List;
import java.util.Objects;

/**
 * This class contains the path of the various Json File and the necessary method to work with these one
 */
public abstract class FileUtilities {
    private static final Gson gson = new Gson();
    public static final String SAVED_GAME_PATH = "/gameSaved.json";
    public static final String MARKET_DEFAULT_CONFIG_PATH = "/defaultMarketConfig.json";
    public static final String SAVED_MARKET_DATA_PATH = "/initialMarketState.json";
    public static final String SAVED_DEV_CARD_DATA_PATH = "/initialGridState.json";
    public static final String SAVED_LEADER_CARD_DATA_PATH = "/initialDeckLeaderState.json";
    public static final String UNMODIFIABLE_DEVELOPMENT_CARDS_PATH = "/developmentCards.json";
    public static final String UNMODIFIABLE_LEADER_CARDS_PATH = "/leaderCards.json";
    public static final String UNMODIFIABLE_SOLO_TOKEN_PATH = "/soloActionTokens.json";
    public static final String SOLO_SAVED_TOKEN_PATH = "/savedActionTokens.json";

    /**
     * Reset the Game Data saved in the SaveGame file.
     *
     * @param nicknames List of String containing the nicknames of the current game
     */
    public static void resetGameData(List<String> nicknames) {
        try (FileWriter file = new FileWriter(SAVED_GAME_PATH)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("players", gson.toJsonTree(nicknames));
            jsonObject.add("actions", new JsonArray());
            gson.toJson(jsonObject, file);
            file.flush();
            resetTokenData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse a JSON file to create a JsonElement.
     *
     * @param path of the JSON file
     * @return the JsonElement of the parsed file
     */
    public static JsonElement getJsonElementFromFile(String path) {
        try {
            return JsonParser.parseReader(new InputStreamReader(Objects.requireNonNull(FileUtilities.class.getResourceAsStream(path))));
        } catch (Exception e) {
            System.err.println("error in the json file format");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Write to file a JsonElement.
     *
     * @param jsonElement to write
     * @param path        of the file to write
     */
    public static void writeJsonElementInFile(JsonElement jsonElement, String path) {
        try (FileWriter file = new FileWriter(path)) {
            //We can write any JSONArray or JSONObject instance to the file
            gson.toJson(jsonElement, file);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method reset the savedActionTokens.json Json File with an empty body
     */
    public static void resetTokenData() {
        try (FileWriter file = new FileWriter(SOLO_SAVED_TOKEN_PATH)) {
            JsonNull jsonObject = new JsonNull();
            gson.toJson(jsonObject, file);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
