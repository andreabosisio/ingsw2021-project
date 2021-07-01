package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.send.EventToClient;

/**
 * Should be implemented by all the Classes that receive Events from the Model.
 */
public interface EventToClientObserver {

    /**
     * This method is used to be updated of an Event coming from the Model
     *
     * @param eventToClient the Event from the Model
     */
    void update(EventToClient eventToClient);
}
