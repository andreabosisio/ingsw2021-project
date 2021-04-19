package it.polimi.ingsw.server.observer;

import it.polimi.ingsw.server.events.receive.ReceiveEvent;

public interface Observer {
    void update(ReceiveEvent receiveEvent);
}
