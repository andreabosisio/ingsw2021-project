package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.send.SendEvent;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.observer.Observer;

import java.util.List;

public class Controller implements Observer {
    private final ModelInterface modelInterface;

    public Controller(List<String> nicknames) {
        modelInterface = new ModelInterface(nicknames);
    }

    public ModelInterface getModelInterface() {
        return modelInterface;
    }

    @Override
    public synchronized void update(ReceiveEvent receiveEvent) {
        //todo try catch di tutti gli errori e allo stesso modo avverte se if è false
        if (modelInterface.getCurrentPlayerNickname().equals(receiveEvent.getNickname())) {
            try {
                receiveEvent.doAction(modelInterface);
            } catch (InvalidIndexException | NonStorableResourceException | EmptySlotException | NonAccessibleSlotException | InvalidEventException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Not used here
     *
     * @param sendEvent
     */
    @Override
    public void update(SendEvent sendEvent) {

    }
}
