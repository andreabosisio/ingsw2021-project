package it.polimi.ingsw.server.observer;

import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.send.SendEvent;

public interface Observable {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(ReceiveEvent receiveEvent);
    void notifyObservers(SendEvent sendEvent);
}
