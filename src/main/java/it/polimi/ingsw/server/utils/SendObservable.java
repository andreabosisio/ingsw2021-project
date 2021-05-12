package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.send.SendEvent;

public interface SendObservable {

    /**
     * This method notify the Virtual Views of an amendment of the Model
     *
     * @param sendEvent the Event from the Model
     */
    void notifyObservers(SendEvent sendEvent);
    void registerObserver(SendObserver virtualView);
    void removeObserver(SendObserver virtualView);
}
