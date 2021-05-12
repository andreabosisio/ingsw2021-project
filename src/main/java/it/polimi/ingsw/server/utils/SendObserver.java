package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.send.SendEvent;

public interface SendObserver {
    /**
     * This method is called by the ModelInterface to notify this class
     * of an amendment of the Model
     *
     * @param sendEvent the Event from the Model
     */
    void update(SendEvent sendEvent);
}
