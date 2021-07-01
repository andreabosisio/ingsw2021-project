package it.polimi.ingsw.server.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.events.receive.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class ServerParser extends Parser {

    private static final Map<String, Object> eventFromClientTypes = new HashMap<String, Object>() {{
        put(BUY_ACTION_TYPE, BuyEventFromClient.class);
        put(CARD_PLACEMENT_ACTION_TYPE, PlaceDevelopmentCardEventFromClient.class);
        put(SETUP_ACTION_TYPE, SetupEventFromClient.class);
        put(END_TURN_ACTION_TYPE, EndTurnEventFromClient.class);
        put(LEADER_ACTION_TYPE, LeaderHandEventFromClient.class);
        put(MARKET_ACTION_TYPE, MarketEventFromClient.class);
        put(PRODUCTION_ACTION_TYPE, ProductionEventFromClient.class);
        put(RESOURCES_PLACEMENT_ACTION_TYPE, PlaceResourcesEventFromClient.class);
        put(TRANSFORMATION_ACTION_TYPE, TransformationEventFromClient.class);
        put(RECONNECT_TYPE, ReconnectEventFromClient.class);
        put(DISCONNECT_TYPE, DisconnectEventFromClient.class);
        put(CHEAT_TYPE, CheatEventFromClient.class);
    }};

    public static EventFromClient getEventFromClient(JsonElement message) {
        try {
            JsonObject action = message.getAsJsonObject();
            Type eventType = (Type) eventFromClientTypes.get(action.get(MSG_TYPE_ID).getAsString());
            return gson.fromJson(action, eventType);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
