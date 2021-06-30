package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.send.EventToClient;

public interface SendObserver {
    /**
     * This method is called by a notify method to be update of an Event coming from the Model
     *
     * @param eventToClient the Event from the Model
     */
    void update(EventToClient eventToClient);
}
