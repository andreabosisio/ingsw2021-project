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

/**
 * Messages Parser for Client Side.
 */
public abstract class ClientParser extends Parser {

    private static final Map<String, Object> eventFromServerTypes = new HashMap<>() {{
        put(INFO_TYPE, InfoMessageEvent.class);
        put(ERROR_TYPE, ErrorMessageEvent.class);
        put(LOGIN_TYPE, LoginEvent.class);
        put(MATCHMAKING_TYPE, MatchMakingEvent.class);
        put(SETUP_TYPE, ChooseSetupEvent.class);
        put(LOBBY_CHOICE_TYPE, ChooseNumberPlayersEvent.class);
        put(GRAPHIC_UPDATE_TYPE, GraphicUpdateEvent.class);
        put(PLACE_DEV_CARD_TYPE, PlaceDevCardReceiveEvent.class);
        put(TRANSFORMATION_TYPE, TransformationReceiveEvent.class);
        put(START_TURN_TYPE, StartTurnUpdateEvent.class);
        put(PLACE_RESOURCES_TYPE, PlaceResourcesReceiveEvent.class);
        put(END_TURN_CHOICE_TYPE, EndTurnReceiveEvent.class);
        put(GAME_STARTED_TYPE, GameStartedEvent.class);
        put(END_GAME_TYPE, EndGameEvent.class);
        put(RECONNECT_TYPE, ReconnectEvent.class);
    }};

    /**
     * Create a new EventFromServer Object from a message.
     *
     * @param message A String containing a Json structured message
     * @return The new Event, null if message is malformed
     */
    public static EventFromServer getEventFromServer(String message) {
        try {
            JsonElement jsonElement = JsonParser.parseString(message);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return getEventFromServer(jsonObject);
            } else {
                System.out.println("Malformed JSON from Server");
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a new EventFromServer Object from a message.
     *
     * @param jsonObject A jsonObject
     * @return The new Event, null if message is malformed
     */
    public static EventFromServer getEventFromServer(JsonObject jsonObject) {
        if (jsonObject != null) {
            try {
                return gson.fromJson(jsonObject, (Type) eventFromServerTypes.get(jsonObject.get(TYPE_ID).getAsString()));
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
