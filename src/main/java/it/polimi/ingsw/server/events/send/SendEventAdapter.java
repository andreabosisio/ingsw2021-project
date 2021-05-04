package it.polimi.ingsw.server.events.send;

import com.google.gson.*;

import java.lang.reflect.Type;

public class SendEventAdapter implements JsonSerializer<SendEvent> {
    @Override
    public JsonElement serialize(SendEvent sendEvent, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "choice");
        jsonObject.addProperty("payload", new Gson().toJson(sendEvent));

        return jsonObject;
    }
}
