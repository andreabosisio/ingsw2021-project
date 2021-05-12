package it.polimi.ingsw.server.events.send.choice;

import com.google.gson.Gson;
import it.polimi.ingsw.server.events.send.SendEvent;

public abstract class ChoiceEvent implements SendEvent {
    private final String nickname;
    private final String type;

    public ChoiceEvent(String nickname, String type) {
        this.nickname = nickname;
        this.type = type;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toJson(){
        return new Gson().toJson(this);
    }

    @Override
    public boolean isForYou(String nickname) {
        return nickname.equals(this.nickname);
    }
}