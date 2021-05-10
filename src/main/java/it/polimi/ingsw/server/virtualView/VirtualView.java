package it.polimi.ingsw.server.virtualView;

import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.send.SendEvent;
import it.polimi.ingsw.server.network.personal.PlayerData;
import it.polimi.ingsw.server.utils.Observable;
import it.polimi.ingsw.server.utils.Observer;

public class VirtualView implements Observer, Observable {
    private Observer controllerObserver;
    private final PlayerData playerData;

    public VirtualView(PlayerData playerData) {
        this.playerData = playerData;
        playerData.getClientHandler().setVirtualView(this);
    }

    @Override
    public void registerObserver(Observer observer) {
        this.controllerObserver = observer;
    }

    @Override
    public void removeObserver(Observer observer) {
    }

    /**
     * This method notify the Controller of the reach of an Event from the Client
     *
     * @param receiveEventFromClient the Event from the Client
     */
    @Override
    public void notifyObservers(ReceiveEvent receiveEventFromClient) {
        controllerObserver.update(receiveEventFromClient);
    }

    /**
     * This method is called by the ModelInterface to notify this class
     * of an amendment of the Model
     *
     * @param sendEvent the Event from the Model
     */
    @Override
    public void update(SendEvent sendEvent) {
        if (sendEvent.getNickname().equals(playerData.getNickname())) {
            playerData.getClientHandler().sendJsonMessage(sendEvent.toJson());
        }
        //check if player is owner of this virtual view
        //if yes send serializable event with data to client
    }

    /**
     * Method not used here
     *
     * @param sendEvent //
     */
    @Override
    public void notifyObservers(SendEvent sendEvent) {
    }

    /**
     * Method not used here
     *
     * @param receiveEvent //
     */
    @Override
    public void update(ReceiveEvent receiveEvent) {
    }
}