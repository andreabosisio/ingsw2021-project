package it.polimi.ingsw.server.observer;

import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.send.SendEvent;

public interface Observer {
    void update(ReceiveEvent receiveEvent);
    void update(SendEvent sendEvent);
}
