package it.polimi.ingsw.client.utils;

import it.polimi.ingsw.client.events.send.EventToServer;

public interface CommandListener {

    void notifyObservers(EventToServer eventToServer);

    void registerObservers(CommandListenerObserver commandListenerObserver);
}
