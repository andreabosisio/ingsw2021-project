package it.polimi.ingsw.client.events.send;

import com.google.gson.Gson;

public abstract class SendEvent {

    private final String type;

    public SendEvent(String type) {
        this.type = type;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
