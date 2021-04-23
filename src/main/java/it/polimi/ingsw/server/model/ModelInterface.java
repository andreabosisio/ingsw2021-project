package it.polimi.ingsw.server.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.send.SendEvent;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import it.polimi.ingsw.server.observer.Observable;
import it.polimi.ingsw.server.observer.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModelInterface implements Observable {

    private final List<Player> players = new ArrayList<>();
    private final TurnLogic turnLogic;
    private final SetupManager setupManager;
    private final List<Observer> virtualViews;

    public ModelInterface(List<String> nicknames) {
        for (String username : nicknames) {
            this.players.add(new Player(username));
        }
        virtualViews = new ArrayList<>();
        //Collections.shuffle(players);
        turnLogic = new TurnLogic(players, this);
        //todo passa modelInterface a setup manager
        setupManager = new SetupManager(players);
    }

    /**
     * Getter of current player's nickname
     *
     * @return nickname of current player
     */
    public String getCurrentPlayer() {
        return turnLogic.getCurrentPlayer().getNickName();
    }

    public TurnLogic getTurnLogic() {
        return turnLogic;
    }

    public boolean marketAction(int arrowID) throws InvalidEventException, InvalidIndexException {
        return turnLogic.marketAction(arrowID);
    }

    public boolean productionAction(Map<Integer, List<Integer>> productionMapIN, Map<Integer, String> productionMapOUT) throws InvalidEventException, InvalidIndexException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return turnLogic.productionAction(productionMapIN, productionMapOUT);
    }

    public boolean buyAction(String cardColor, int cardLevel, List<Integer> resourcesPositions) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        return turnLogic.buyAction(cardColor, cardLevel, resourcesPositions);
    }

    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {
        return turnLogic.leaderAction(ID, discard);
    }

    public boolean placeResourceAction(List<Integer> swapPairs) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        return turnLogic.placeResourceAction(swapPairs);
    }

    public boolean placeDevCardAction(int slotPosition) throws InvalidEventException {
        return turnLogic.placeDevCardAction(slotPosition);
    }

    public boolean transformationAction(List<String> chosenColors) throws InvalidEventException, NonStorableResourceException {
        return turnLogic.transformationAction(chosenColors);
    }

    public boolean endTurn() throws InvalidEventException {
        return turnLogic.endTurn();
    }

    @Override
    public void registerObserver(Observer virtualView) {
        virtualViews.add(virtualView);
    }

    @Override
    public void removeObserver(Observer virtualView) {
        int i = virtualViews.indexOf(virtualView);
        if (i >= 0) {
            virtualViews.remove(virtualView);
        }
    }

    /**
     * Not used here
     *
     * @param receiveEvent event received
     */
    @Override
    public void notifyObservers(ReceiveEvent receiveEvent) {

    }

    @Override
    public void notifyObservers(SendEvent sendEvent) {
        virtualViews.forEach(view -> view.update(sendEvent));
    }
}
