package it.polimi.ingsw.commons;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Parser {

    //client to server
    public static final String buyActionType = "buyAction";
    public static final String cardPlacementActionType = "cardPlacementAction";
    public static final String setupActionType = "setupAction";
    public static final String endTurnActionType = "endTurnAction";
    public static final String leaderActionType = "leaderAction";
    public static final String marketActionType = "marketAction";
    public static final String productionActionType = "productionAction";
    public static final String resourcesPlacementActionType = "resourcesPlacementAction";
    public static final String transformationActionType = "transformationAction";
    public static final String disconnectType = "disconnectAction";
    public static final String cheatType = "cheatAction";
    //server to client
    public static final String infoType = "info";
    public static final String errorType = "error";
    public static final String loginType = "login";
    public static final String matchmakingType = "matchmaking";
    public static final String setupType = "setup";
    public static final String lobbyChoiceType = "lobbyChoice";
    public static final String graphicUpdateType = "graphicUpdate";
    public static final String placeDevCardType = "placeDevCard";
    public static final String transformationType = "transformation";
    public static final String startTurnType = "startTurn";
    public static final String placeResourcesType = "placeResources";
    public static final String endTurnChoiceType = "endTurnChoice";
    public static final String gameStartedType = "gameStarted";
    public static final String endGameType = "endGame";
    //both ways
    public static final String reconnectType = "reconnect";

    public static final Gson gson = new Gson();

    public static final String MSG_TYPE_ID = "type";

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

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
        return jsonObject.get(MSG_TYPE_ID).getAsString();
    }

    public static JsonElement extractFromField(JsonElement jsonElement, String type) {
        return jsonElement.getAsJsonObject().get(type);
    }


}
