package it.polimi.ingsw.server.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.receive.SetupReceiveEvent;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.personal.VirtualView;
import it.polimi.ingsw.server.utils.ReceiveObserver;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Controller implements ReceiveObserver {

    private final ModelInterface modelInterface;

    public Controller(List<String> nicknames) {
        Collections.shuffle(nicknames);
        modelInterface = new ModelInterface(nicknames);
    }

    public ModelInterface getModelInterface() {
        return modelInterface;
    }

    /**
     * This method is called by the VirtualView to notify this class
     * of an Event coming from the Client
     *
     * @param receiveEvent the Event from the Client
     */
    @Override
    public synchronized void update(ReceiveEvent receiveEvent) {

        ClientHandler currentClientHandler = Lobby.getLobby().getVirtualViewByNickname(receiveEvent.getNickname()).getClientHandler();

        if (modelInterface.getCurrentPlayerNickname().equals(receiveEvent.getNickname()) || receiveEvent instanceof SetupReceiveEvent) {
            try {
                if(receiveEvent.doAction(modelInterface))
                    currentClientHandler.sendInfoMessage(receiveEvent.getNickname()+" performed a valid "+receiveEvent.getClass().getSimpleName()+"!");

                //fixme this is for testing!!
                /*
                String nickname = receiveEvent.getNickname();
                Player currentSetupPlayer = modelInterface.getTurnLogic().getPlayers().stream()
                        .filter(player -> player.getNickname().equals(nickname)).findFirst()
                        .orElseThrow(() -> new InvalidEventException("Invalid nickname"));

                JsonObject info = new JsonObject();
                info.addProperty("playerNick", currentSetupPlayer.getNickname());
                info.addProperty("leaderHand", new Gson().toJson(currentSetupPlayer.getLeaderHand().stream().map(LeaderCard::getID).collect(Collectors.toList())));
                info.addProperty("leaderActive", new Gson().toJson(currentSetupPlayer.getPersonalBoard().getActiveLeaderCards().stream().map(LeaderCard::getID).collect(Collectors.toList())));
                info.addProperty("warehouse", new Gson().toJson(currentSetupPlayer.getPersonalBoard().getWarehouse().getAllResources()));
                info.addProperty("faithTrack", GameBoard.getGameBoard().getFaithTrackOfPlayer(currentSetupPlayer).getFaithMarker());
                currentClientHandler.sendJsonMessage(info.toString());

                 */

            } catch (InvalidIndexException | NonStorableResourceException | EmptySlotException | NonAccessibleSlotException | InvalidEventException e) {
                currentClientHandler.sendErrorMessage(e.getMessage());
                //if exception was created by a choice re send choice event
                modelInterface.reSendChoice();
            } catch (InvalidSetupException ex){
                //if exception was created by a failed setup event resend setup choice event
                currentClientHandler.sendErrorMessage(ex.getMessage());
                modelInterface.reSendSetup(receiveEvent.getNickname());
            }
        }else
            currentClientHandler.sendErrorMessage("It's not your turn!");
    }

    public void setupObservers(List<VirtualView> virtualViews) {
        virtualViews.forEach(modelInterface::registerObserver);
        virtualViews.forEach(virtualView -> virtualView.registerObserver(this));
        modelInterface.startSetup();
    }
}