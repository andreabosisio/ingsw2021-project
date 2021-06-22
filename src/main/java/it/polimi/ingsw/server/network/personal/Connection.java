package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.server.network.PongObserver;

public interface Connection{
    void sendMessage(String message);
    String getMessage();
    void clearStack();
    void setPongObserver(PongObserver pongObserver);
    void close();
    void sendPing();
}
