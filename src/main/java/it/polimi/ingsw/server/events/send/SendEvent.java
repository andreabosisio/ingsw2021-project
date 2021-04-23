package it.polimi.ingsw.server.events.send;

public abstract class SendEvent {
    private final String nickname;

    public SendEvent(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
