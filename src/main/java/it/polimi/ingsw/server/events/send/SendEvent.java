package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;

public abstract class SendEvent {
    private final String nickname;
    private final Gson gson = new Gson();

    public SendEvent(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public String toJson(){return null;}

    public Gson getGson() {
        return gson;
    }
}