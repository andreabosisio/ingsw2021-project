package it.polimi.ingsw.server.events.send;

public interface SendEvent {
    boolean isForYou(String nickname);
    String toJson();
}