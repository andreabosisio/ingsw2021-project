package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.server.events.receive.EventFromClient;

//todo rename and review javadoc of update method
public interface ReceiveObserver {
    /**
     * This method is called by a notify method to be update of an Event coming from the Client
     *
     * @param eventFromClient the Event from the Client
     */
    void update(EventFromClient eventFromClient);
}
