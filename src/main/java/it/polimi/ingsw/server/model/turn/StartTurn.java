package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.PersonalBoard;
import it.polimi.ingsw.server.model.player.Warehouse;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartTurn extends State {
    public StartTurn(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Take the chosen resources from the MarketTray and set the current state of the game to
     * WaitResourceTransformation if there are some White Resources to transform or else to
     * WaitResourcePlacement.
     *
     * @param arrowID is the index of the chosen line of the MarketTray
     * @return true if the state has been changed
     * @throws InvalidIndexException if the arrowID is not correct
     */
    @Override
    public boolean marketAction(int arrowID) throws InvalidIndexException {
        if(turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()
                .addResourcesFromMarket(GameBoard.getGameBoard().getMarketTray().takeResources(arrowID))){
            if(turnLogic.getWhiteResourcesFromMarket().size()>0){
                //todo evento in uscita
                turnLogic.setCurrentState(turnLogic.getWaitTransformation());
                return true;
            }
            //todo evento in uscita
            turnLogic.setCurrentState(turnLogic.getWaitResourcePlacement());
        }
        return false;

    }

    /**
    * Apply the production of the chosen ProductionCard with the chosen resources.
    *
    * @param productionMap containing the chosen ProductionCard and the chosen resources for that card
    * @return true if the production has been correctly applied
    * @throws InvalidEventException if one of the production can't be applied
    * @throws InvalidIndexException if one of the index of the chosen ProductionCard doesn't exists
    * @throws NonStorableResourceException if one of the chosen resources contains a NonStorableResource
     */
    @Override
    public boolean productionAction(Map<Integer, List<Integer>> productionMap) throws InvalidEventException, InvalidIndexException, NonStorableResourceException {
        PersonalBoard personalBoard = turnLogic.getCurrentPlayer().getPersonalBoard();
        Warehouse warehouse = personalBoard.getWarehouse();
        List<Resource> chosenResources;
        ProductionCard chosenCard;

        for(Map.Entry production : productionMap.entrySet()){
            chosenResources = new ArrayList<>(warehouse.getResources((List<Integer>) production.getValue()));
            chosenCard = personalBoard.getProductionCard((Integer) production.getKey());
            if(!chosenCard.canDoProduction(chosenResources))
                throw new InvalidEventException();
            if(!chosenCard.usePower(turnLogic))
                throw new InvalidEventException();
        }
        turnLogic.setCurrentState(turnLogic.getEndGame());
        return true;
    }

    @Override
    public boolean buyAction(int cardGridIndex, List<Integer> positions) throws InvalidEventException {
        return super.buyAction(cardGridIndex, positions);
    }

    /**
     * Activate or Discard a LeaderCard. If done successfully change the state of the game to LeaderState where
     * another LeaderAction is not accepted.
     *
     * @param ID of the chosen LeaderCard
     * @param discard true if the chosen LeaderCard has to be discarded, false if has to be activated
     * @return true if the leaderAction has been successfully applied
     * @throws InvalidEventException if the leaderAction can't be applied
     */
    @Override
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {
        Player currentPlayer = turnLogic.getCurrentPlayer();

        //get the chosen leader card
        LeaderCard chosenLeaderCard = currentPlayer.getLeaderHand().stream()
                                .filter(card -> card.getID().equals(ID)).findFirst()
                                .orElseThrow(() -> new InvalidEventException());
        //if the card has to be discarded
        if(discard){
            if(!currentPlayer.discardLeader(chosenLeaderCard))
                throw new InvalidEventException();
        }else
        //if the card has to be activated
        {
            if(!currentPlayer.activateLeaderCard(chosenLeaderCard))
                throw new InvalidEventException();
        }

        turnLogic.setCurrentState(turnLogic.getLeaderState());
        return true;
    }
}
