package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.receive.EventFromClient;

/**
 * Should be implemented by all the Classes that needs to be updated when an Event from the Client has been received.
 */
public interface EventsFromClientObserver {
    /**
     * Notify the observers that an Event from the Client has been received.
     *
     * @param eventFromClient The Event from the Client
     */
    void update(EventFromClient eventFromClient);
}
