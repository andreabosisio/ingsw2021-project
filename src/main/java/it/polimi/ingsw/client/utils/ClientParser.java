package it.polimi.ingsw.client.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.client.events.receive.*;
import it.polimi.ingsw.commons.Parser;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClientParser extends Parser {

    private static final Map<String, Object> eventFromServerTypes = new HashMap<String,Object>() {{
        put(infoType, InfoMessageEvent.class);
        put(errorType, ErrorMessageEvent.class);
        put(loginType, LoginEvent.class);
        put(matchmakingType, MatchMakingEvent.class);
        put(setupType, ChooseSetupEvent.class);
        put(lobbyChoiceType, ChooseNumberPlayersEvent.class);
        put(graphicUpdateType, GraphicUpdateEvent.class);
        put(placeDevCardType, PlaceDevCardReceiveEvent.class);
        put(transformationType, TransformationReceiveEvent.class);
        put(startTurnType, StartTurnUpdateEvent.class);
        put(placeResourcesType, PlaceResourcesReceiveEvent.class);
        put(endTurnChoiceType, EndTurnReceiveEvent.class);
        put(gameStartedType, GameStartedEvent.class);
        put(endGameType, EndGameEvent.class);
        put(reconnectType, ReconnectEvent.class);
    }};

    public static EventFromServer getEventFromServer(String message) {
        try {
            JsonElement jsonElement = JsonParser.parseString(message);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return getEventFromServer(jsonObject);
            } else {
                System.out.println("Malformed json from Server");
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EventFromServer getEventFromServer(JsonObject jsonObject) {
        if(jsonObject != null) {
            try {
                return gson.fromJson(jsonObject, (Type) eventFromServerTypes.get(jsonObject.get(MSG_TYPE_ID).getAsString()));
            } catch (NullPointerException e) {
                //malformed message
                System.out.println("FAILED: " + jsonObject);
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }
}
