package it.polimi.ingsw.server.virtualView;

import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.observer.Observable;
import it.polimi.ingsw.server.observer.Observer;

public class VirtualView implements Observer, Observable {
    private Observer controllerObserver;
    @Override
    public void registerObserver(Observer observer) {
        this.controllerObserver=observer;
    }

    @Override
    public void removeObserver(Observer observer) {

    }

    @Override
    public void notifyObservers(ReceiveEvent receiveEventFromClient) {
        controllerObserver.update(receiveEventFromClient);
    }

    @Override
    public void update(ReceiveEvent receiveEvent) {
        //check if player is owner of this virtual view
        //if yes send serializable event with data to client
    }
}
