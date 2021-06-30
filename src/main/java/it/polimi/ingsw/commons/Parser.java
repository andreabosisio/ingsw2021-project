package it.polimi.ingsw.commons;

import com.google.gson.*;

public abstract class Parser {
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
