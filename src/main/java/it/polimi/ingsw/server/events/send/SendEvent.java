package it.polimi.ingsw.server.events.send;

public abstract class SendEvent {
    private final String nickName;

    public SendEvent(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }
}
