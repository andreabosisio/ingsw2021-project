package it.polimi.ingsw.server.events.send;

public class EndGameSendEvent extends SendEvent{
    public EndGameSendEvent(String nickName) {
        super(nickName);
    }
}
