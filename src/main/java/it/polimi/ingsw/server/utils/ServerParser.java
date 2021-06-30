package it.polimi.ingsw.server.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.events.receive.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ServerParser extends Parser {

    private static final Map<String, Object> eventFromClientTypes = new HashMap<String,Object>() {{
        put("buyAction", BuyEventFromClient.class);
        put("cardPlacementAction", PlaceDevelopmentCardEventFromClient.class);
        put("setupAction", SetupEventFromClient.class);
        put("endTurnAction", EndTurnEventFromClient.class);
        put("leaderAction", LeaderHandEventFromClient.class);
        put("marketAction", MarketEventFromClient.class);
        put("productionAction", ProductionEventFromClient.class);
        put("resourcesPlacementAction", PlaceResourcesEventFromClient.class);
        put("transformationAction", TransformationEventFromClient.class);
        put("reconnect", ReconnectEventFromClient.class);
        put("disconnect", DisconnectEventFromClient.class);
        put("cheat", CheatEventFromClient.class);
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
