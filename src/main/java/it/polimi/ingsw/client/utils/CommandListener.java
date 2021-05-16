package it.polimi.ingsw.client.utils;

import it.polimi.ingsw.client.events.send.SendEvent;

public interface CommandListener {

    void notifyObservers(SendEvent sendEvent);
    void registerObservers(CommandListenerObserver commandListenerObserver);
}
