package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.receive.ReceiveEvent;

public interface ReceiveObservable {
    /**
     * This method notify the Controller of the reach of an Event from the Client
     *
     * @param receiveEventFromClient the Event from the Client
     */
    void notifyObservers(ReceiveEvent receiveEventFromClient);
    void registerObserver(ReceiveObserver observer);
}
