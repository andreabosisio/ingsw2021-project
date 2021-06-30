package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.receive.EventFromClient;

public interface ReceiveObservable {
    /**
     * This method is used to notify all the Observers of an Event from the Client
     *
     * @param eventFromClientFromClient the Event from the Client
     */
    void notifyObservers(EventFromClient eventFromClientFromClient);

    /**
     * This method is used to set an Observer
     *
     * @param observer object to set
     */
    void registerObserver(EventsForClientObserver observer);
}
