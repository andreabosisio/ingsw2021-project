package it.polimi.ingsw.client.events.send;

import com.google.gson.Gson;
import it.polimi.ingsw.client.NetworkHandler;


public abstract class SendEvent {
    private String sender;
    private final String type;

    public SendEvent (String type) {
        this.type = type;
    }

    public String toJson(String sender){
        this.sender = sender;
        return new Gson().toJson(this);
    }
}
