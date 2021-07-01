package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.receive.EventFromClient;

public interface EventsFromClientObserver {
    /**
     * This method is called by a Virtual View to notify the observers that an Event from the Client has been received.
     *
     * @param eventFromClient The Event from the Client
     */
    void update(EventFromClient eventFromClient);
}
