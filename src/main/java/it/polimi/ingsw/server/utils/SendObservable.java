package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.send.SendEvent;

public interface SendObservable {
    /**
     * This method is used to notify all the Observers of an Event from the Model
     *
     * @param sendEvent the Event from the Model
     */
    void notifyObservers(SendEvent sendEvent);

    /**
     * This method is used to set an Observer
     *
     * @param virtualView object to set
     */
    void registerObserver(SendObserver virtualView);
}
