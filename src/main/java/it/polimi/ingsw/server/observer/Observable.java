package it.polimi.ingsw.server.observer;

import it.polimi.ingsw.server.events.receive.ReceiveEvent;

public interface Observable {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(ReceiveEvent receiveEvent);
}
