package it.polimi.ingsw.client.utils;

import it.polimi.ingsw.client.events.send.EventToServer;

/**
 * This interface is implemented by the classes that listen for the inputs taken from the users
 * and it notify the class NetworkHandler.
 */
public interface CommandListener {

    /**
     * This method is used to notify the commandListenerObserver of the player choices
     *
     * @param eventToServer event containing the player choices data
     */
    void notifyObservers(EventToServer eventToServer);

    /**
     * This method is used to register a commandListenerObserver as observer of this class
     *
     * @param commandListenerObserver observer interested in the player actions
     */
    void registerObservers(CommandListenerObserver commandListenerObserver);
}
