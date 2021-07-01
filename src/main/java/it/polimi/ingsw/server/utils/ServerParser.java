package it.polimi.ingsw.server.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.events.receive.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class ServerParser extends Parser {

    private static final Map<String, Object> eventFromClientTypes = new HashMap<String,Object>() {{
        put(buyActionType, BuyEventFromClient.class);
        put(cardPlacementActionType, PlaceDevelopmentCardEventFromClient.class);
        put(setupActionType, SetupEventFromClient.class);
        put(endTurnActionType, EndTurnEventFromClient.class);
        put(leaderActionType, LeaderHandEventFromClient.class);
        put(marketActionType, MarketEventFromClient.class);
        put(productionActionType, ProductionEventFromClient.class);
        put(resourcesPlacementActionType, PlaceResourcesEventFromClient.class);
        put(transformationActionType, TransformationEventFromClient.class);
        put(reconnectType, ReconnectEventFromClient.class);
        put(disconnectType, DisconnectEventFromClient.class);
        put(cheatType, CheatEventFromClient.class);
    }};

    public static EventFromClient getEventFromClient(JsonElement message) {
        try {
            JsonObject action = message.getAsJsonObject();
            Type eventType = (Type) eventFromClientTypes.get(action.get(MSG_TYPE_ID).getAsString());
            return gson.fromJson(action, eventType);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }
}
