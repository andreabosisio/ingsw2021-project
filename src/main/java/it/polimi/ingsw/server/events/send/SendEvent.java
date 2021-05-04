package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class SendEvent {
    private final String nickname;

    public SendEvent(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public String toJson(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SendEvent.class, new SendEventAdapter())
                .create();
        return gson.toJson(this);
    }
}