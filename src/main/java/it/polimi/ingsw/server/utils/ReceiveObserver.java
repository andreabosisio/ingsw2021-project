package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.receive.ReceiveEvent;

public interface ReceiveObserver {
    /**
     * This method is called by the VirtualView to notify this class
     * of an Event coming from the Client
     *
     * @param receiveEvent the Event from the Client
     */
    void update(ReceiveEvent receiveEvent);
}
