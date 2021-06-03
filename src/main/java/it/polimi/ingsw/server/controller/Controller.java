package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.receive.SetupReceiveEvent;
import it.polimi.ingsw.server.events.send.GameStartedSendEvent;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.personal.VirtualView;
import it.polimi.ingsw.server.utils.ReceiveObserver;

import java.util.Collections;
import java.util.List;

public class Controller implements ReceiveObserver {

    private final ModelInterface modelInterface;
    private final List<String> nicknames;

    public Controller(List<String> nicknames) {
        Collections.shuffle(nicknames);
        this.modelInterface = new ModelInterface(nicknames);
        this.nicknames = nicknames;
    }

    public ModelInterface getModelInterface() {
        return modelInterface;
    }

    /**
     * This method is called by the VirtualView to notify this class
     * of an Event coming from the Client
     * If the owner is the currently playing player the action is performed
     * If the action is not performed successfully the last event sent to the client is resented
     *
     * @param receiveEvent the Event from the Client
     */
    @Override
    public synchronized void update(ReceiveEvent receiveEvent) {

        ClientHandler currentClientHandler = Lobby.getLobby().getVirtualViewByNickname(receiveEvent.getNickname()).getClientHandler();

        if (modelInterface.getCurrentPlayerNickname().equals(receiveEvent.getNickname()) || receiveEvent instanceof SetupReceiveEvent) {
            try {
                receiveEvent.doAction(modelInterface);
            } catch (InvalidIndexException | NonStorableResourceException | EmptySlotException | NonAccessibleSlotException | InvalidEventException e) {
                currentClientHandler.sendErrorMessage(e.getMessage());
                //if exception was created by a choice re send choice event
                modelInterface.reSendLastEvent();
            } catch (InvalidSetupException ex){
                //if exception was created by a failed setup event resend setup choice event
                currentClientHandler.sendErrorMessage(ex.getMessage());
                modelInterface.reSendSetup(receiveEvent.getNickname());
            }
        }else
            currentClientHandler.sendErrorMessage("It's not your turn!");
    }

    /**
     * This function is used to register all the virtualViews as observers of the model
     * It also notifies all players that the game is starting
     *
     * @param virtualViews virtualViews to set as observers
     */
    public void setupObservers(List<VirtualView> virtualViews) {
        virtualViews.forEach(modelInterface::registerObserver);
        virtualViews.forEach(virtualView -> virtualView.registerObserver(this));
        //notify players of game starting
        modelInterface.notifyObservers(new GameStartedSendEvent(nicknames));
        modelInterface.startSetup();
    }

    /**
     * Method used to set a player as offline
     *
     * @param nickname offline player
     */
    public synchronized void disconnectPlayer(String nickname){
        modelInterface.disconnectPlayer(nickname);
    }
    public synchronized void reconnectPlayer(String nickname) {
        modelInterface.reconnectPlayer(nickname);
    }
}