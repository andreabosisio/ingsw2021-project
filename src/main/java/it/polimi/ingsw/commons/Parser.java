package it.polimi.ingsw.commons;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Messages Parser.
 */
public abstract class Parser {

    //client to server
    public static final String BUY_ACTION_TYPE = "buyAction";
    public static final String CARD_PLACEMENT_ACTION_TYPE = "cardPlacementAction";
    public static final String SETUP_ACTION_TYPE = "setupAction";
    public static final String END_TURN_ACTION_TYPE = "endTurnAction";
    public static final String LEADER_ACTION_TYPE = "leaderAction";
    public static final String MARKET_ACTION_TYPE = "marketAction";
    public static final String PRODUCTION_ACTION_TYPE = "productionAction";
    public static final String RESOURCES_PLACEMENT_ACTION_TYPE = "resourcesPlacementAction";
    public static final String TRANSFORMATION_ACTION_TYPE = "transformationAction";
    public static final String DISCONNECT_TYPE = "disconnectAction";
    public static final String CHEAT_TYPE = "cheatAction";
    //server to client
    public static final String INFO_TYPE = "info";
    public static final String ERROR_TYPE = "error";
    public static final String LOGIN_TYPE = "login";
    public static final String MATCHMAKING_TYPE = "matchmaking";
    public static final String SETUP_TYPE = "setup";
    public static final String LOBBY_CHOICE_TYPE = "lobbyChoice";
    public static final String GRAPHIC_UPDATE_TYPE = "graphicUpdate";
    public static final String PLACE_DEV_CARD_TYPE = "placeDevCard";
    public static final String TRANSFORMATION_TYPE = "transformation";
    public static final String START_TURN_TYPE = "startTurn";
    public static final String PLACE_RESOURCES_TYPE = "placeResources";
    public static final String END_TURN_CHOICE_TYPE = "endTurnChoice";
    public static final String GAME_STARTED_TYPE = "gameStarted";
    public static final String END_GAME_TYPE = "endGame";
    //both ways
    public static final String RECONNECT_TYPE = "reconnect";

    public static final Gson gson = new Gson();

    public static final String TYPE_ID = "type";

    /**
     * Return a json representation of an object
     *
     * @param obj object to represent
     * @return a Json representing the object
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Return a jsonTree representation of an object
     *
     * @param obj object to represent
     * @return a JsonElement representing the object
     */
    public static JsonElement toJsonTree(Object obj) {
        return gson.toJsonTree(obj);
    }

    /**
     * Extract the content of the field MSG_TYPE_ID from a JsonObject message.
     *
     * @param jsonObject The JsonObject containing the message
     * @return the extracted content of the field MSG_TYPE_ID
     */
    public static String getTypeFieldAsString(JsonObject jsonObject) {
        return jsonObject.get(TYPE_ID).getAsString();
    }

    /**
     * Extract the content of the specified field from a JsonObject message.
     *
     * @param jsonElement The JsonObject containing the message
     * @param type The field to extract
     * @return the extracted content
     */
    public static JsonElement extractFromField(JsonElement jsonElement, String type) {
        return jsonElement.getAsJsonObject().get(type);
    }
}
