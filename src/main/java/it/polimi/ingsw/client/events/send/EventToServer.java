package it.polimi.ingsw.client.events.send;

import com.google.gson.Gson;

/**
 * This class is the implementation of the messages from the Client to the Server.
 */
public abstract class EventToServer {
    private String sender;
    private final String type;

    /**
     * Instantiates a new Event by defining its type.
     *
     * @param type The type of the message.
     */
    public EventToServer(String type) {
        this.type = type;
    }

    /**
     * Translates this message in a JSON format.
     *
     * @param sender The nickname of this Client
     * @return a new String containing the JSON message
     */
    public String toJson(String sender) {
        this.sender = sender;
        return new Gson().toJson(this);
    }
}
