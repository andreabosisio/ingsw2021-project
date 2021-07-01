package it.polimi.ingsw.client.utils;

import it.polimi.ingsw.client.events.send.EventToServer;

/**
 * This interface is implemented by the classes that are Observer of the CLI/GUI CommandListener class:
 * from them it receives the Events to send to the Server.
 */
public interface CommandListenerObserver {

    /**
     * This method creates the Json File from an Object of type EventToServer and
     * it sends the message through the class ClientConnection.
     *
     * @param eventToServer Event to send to the Server
     */
    void update(EventToServer eventToServer);

    /**
     * Method that set the Nickname of the Player
     *
     * @param nickname of the Player
     */
    void setNickname(String nickname);
}
