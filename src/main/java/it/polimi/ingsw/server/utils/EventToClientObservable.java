package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.send.EventToClient;

/**
 * Should be implemented by all the Classes that sends Events to the Client.
 */
public interface EventToClientObservable {
    /**
     * This method is used to notify all the Observers of an Event from the Model
     *
     * @param eventToClient the Event from the Model
     */
    void notifyObservers(EventToClient eventToClient);

    /**
     * This method is used to set an Observer
     *
     * @param virtualView object to set
     */
    void registerObserver(EventToClientObserver virtualView);
}
