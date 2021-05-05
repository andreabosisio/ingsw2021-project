package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.receive.SetupReceiveEvent;
import it.polimi.ingsw.server.events.send.SendEvent;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.observer.Observer;
import it.polimi.ingsw.server.virtualView.VirtualView;

import java.util.Collections;
import java.util.List;

public class Controller implements Observer {

    private final ModelInterface modelInterface;

    public Controller(List<String> nicknames) {
        Collections.shuffle(nicknames);
        modelInterface = new ModelInterface(nicknames);
    }

    public ModelInterface getModelInterface() {
        return modelInterface;
    }

    @Override
    public synchronized void update(ReceiveEvent receiveEvent) {
        //todo try catch di tutti gli errori e allo stesso modo avverte se if è false
        //todo instanceof per la setupAction
        VirtualView currentVirtualView = modelInterface.getVirtualViewByNickname(receiveEvent.getNickname());
        if(receiveEvent.getNickname() == null)
            return;

        if (modelInterface.getCurrentPlayerNickname().equals(receiveEvent.getNickname()) || receiveEvent instanceof SetupReceiveEvent) {
            try {
                if(receiveEvent.doAction(modelInterface))
                    currentVirtualView.getPlayerData().getClientHandler().sendInfoMessage(receiveEvent.getNickname()+" performed a valid "+receiveEvent.getClass().getSimpleName()+"!");
            } catch (InvalidIndexException | NonStorableResourceException | EmptySlotException | NonAccessibleSlotException | InvalidEventException e) {
                currentVirtualView.getPlayerData().getClientHandler().sendErrorMessage(e.getMessage());
                //e.printStackTrace();
            }
        }
    }

    public void setupObservers(List<VirtualView> virtualViews) {
        virtualViews.forEach(modelInterface::registerObserver);
        virtualViews.forEach(virtualView -> virtualView.registerObserver(this));
        modelInterface.startSetup();
    }

    /**
     * Not used here
     *
     * @param sendEvent null
     */
    @Override
    public void update(SendEvent sendEvent) {

    }
}