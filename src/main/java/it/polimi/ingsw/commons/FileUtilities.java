package it.polimi.ingsw.commons;

import com.google.gson.*;

import java.io.*;
import java.util.List;

/**
 * This class contains the path of the various Json File and the necessary method to work with these one
 */
public abstract class FileUtilities {
    private static final Gson gson = new Gson();
    public static final String SAVED_GAME_PATH = "src/main/resources/gameSaved.json";
    public static final String MARKET_DEFAULT_CONFIG_PATH = "src/main/resources/defaultMarketConfig.json";
    public static final String SAVED_MARKET_DATA_PATH = "src/main/resources/initialMarketState.json";
    public static final String SAVED_DEV_CARD_DATA_PATH = "src/main/resources/initialGridState.json";
    public static final String SAVED_LEADER_CARD_DATA_PATH = "src/main/resources/initialDeckLeaderState.json";
    public static final String UNMODIFIABLE_DEVELOPMENT_CARDS_PATH = "src/main/resources/developmentCards.json";
    public static final String UNMODIFIABLE_LEADER_CARDS_PATH = "src/main/resources/leaderCards.json";
    public static final String SOLO_UNMODIFIABLE_TOKEN_PATH = "src/main/resources/soloActionTokens.json";
    public static final String SOLO_SAVED_TOKEN_PATH = "src/main/resources/savedActionTokens.json";

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
        File input = new File(path);
        try {
            return JsonParser.parseReader(new FileReader(input));
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("error in the json file format");
            e.printStackTrace();
        }
        return null;
    }


    //Todo maybe remove those getter

    /**
     * Get method that return the Path of the Json File where it are saved the nicknames of the Players
     * and the actions performed.
     *
     * @return the Path
     */
    public static String getSavedGamePath() {
        return SAVED_GAME_PATH;
    }

    /**
     * Get method that return the Path of the Json File where it is saved the initial state of the Market Tray
     *
     * @return the Path
     */
    public static String getSavedMarketDataPath() {
        return SAVED_MARKET_DATA_PATH;
    }

    /**
     * Get method that return the Path of the Json File where it is saved the Development Cards Grid
     *
     * @return the Path
     */
    public static String getSavedDevCardDataPath() {
        return SAVED_DEV_CARD_DATA_PATH;
    }

    /**
     * Get method that return the Path of the Json File where it is saved the Deck of the Leader Card
     *
     * @return the Path
     */
    public static String getSavedLeaderCardDataPath() {
        return SAVED_LEADER_CARD_DATA_PATH;
    }

    /**
     * Get method that return the Path of the Json File where the developmentCards are saved
     *
     * @return the Path
     */
    public static String getUnmodifiableDevelopmentCardsPath() {
        return UNMODIFIABLE_DEVELOPMENT_CARDS_PATH;
    }

    /**
     * Get method that return the Path of the Json File where the LeaderCards are saved
     *
     * @return the Path
     */
    public static String getUnmodifiableLeaderCardsPath() {
        return UNMODIFIABLE_LEADER_CARDS_PATH;
    }

    /**
     * Get method that return the Path of the Json File where the TokenFile are saved
     *
     * @return the Path
     */
    public static String getSoloUnmodifiableTokenPath() {
        return SOLO_UNMODIFIABLE_TOKEN_PATH;
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
