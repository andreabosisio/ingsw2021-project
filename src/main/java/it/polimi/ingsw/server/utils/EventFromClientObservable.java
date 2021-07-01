package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.receive.EventFromClient;

/**
 * Should be implemented by all the Classes that receives Events from the Client.
 */
public interface EventFromClientObservable {
    /**
     * This method is used to notify all the Observers of an Event from the Client
     *
     * @param eventFromClient the Event from the Client
     */
    void notifyObservers(EventFromClient eventFromClient);

    /**
     * This method is used to set an Observer
     *
     * @param observer object to set
     */
    void registerObserver(EventsFromClientObserver observer);
}
