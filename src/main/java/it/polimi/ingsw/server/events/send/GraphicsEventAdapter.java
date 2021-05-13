package it.polimi.ingsw.server.events.send;

import com.google.gson.*;
import it.polimi.ingsw.server.events.send.choice.ChoiceEvent;

import java.lang.reflect.Type;

public class GraphicsEventAdapter implements JsonSerializer<ChoiceEvent> {
    //todo to check
    @Override
    public JsonElement serialize(ChoiceEvent choiceEvent, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "choice");
        jsonObject.addProperty("payload", new Gson().toJson(choiceEvent));

        return jsonObject;
    }
}
