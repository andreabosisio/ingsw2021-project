package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.send.PlaceDevCardSendEvent;
import it.polimi.ingsw.server.events.send.PlaceResourcesSendEvent;
import it.polimi.ingsw.server.events.send.TransformationSendEvent;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
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

    private boolean hasAlreadyDoneLeaderAction = false;

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
                //todo evento in uscita (fatto)
                turnLogic.getModelInterface().
                        notifyObservers(new TransformationSendEvent(turnLogic.getCurrentPlayer().getNickName(), turnLogic.getWhiteResourcesFromMarket()));
                hasAlreadyDoneLeaderAction=false;
                turnLogic.setCurrentState(turnLogic.getWaitTransformation());
                return true;
            }
            //todo evento in uscita (fatto)
            turnLogic.getModelInterface().
                    notifyObservers(new PlaceResourcesSendEvent(turnLogic.getCurrentPlayer().getNickName(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
            hasAlreadyDoneLeaderAction=false;
            turnLogic.setCurrentState(turnLogic.getWaitResourcePlacement());
        }
        return false;

    }

    /**
     * For all the given ProductionCard apply the production with the chosen resources.
     *
     * @param productionMap containing the chosen ProductionCard and the chosen resources for that card
     * @return true if the production has been correctly applied
     * @throws InvalidEventException        if one of the production can't be applied
     * @throws InvalidIndexException        if one of the index of the chosen ProductionCard doesn't exists
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
        hasAlreadyDoneLeaderAction = false;
        turnLogic.setCurrentState(turnLogic.getEndGame());
        return true;
    }

    /**
     * Check if the player can place the card and then check if he can buy it with his discounts.
     * If yes buy the card and set the next State of the game to WaitDevCardPlacement.
     *
     * @param cardColor color of the card to buy
     * @param cardLevel level of the card to buy
     * @param resourcesPositions index of the chosen resources
     * @return true if the card has been successfully bought
     * @throws InvalidEventException if the player can't buy the card
     * @throws InvalidIndexException if one of the resource positions is negative
     * @throws EmptySlotException if one of the resource slots is empty
     * @throws NonAccessibleSlotException if one of the resource position represents a slot that's not accessible
     */
    @Override
    public boolean buyAction(String cardColor, int cardLevel, List<Integer> resourcesPositions) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        DevelopmentCard chosenDevCard;
        try {
            CardColorEnum chosenColorEnum = CardColorEnum.valueOf(cardColor.toUpperCase());
            chosenDevCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(chosenColorEnum, cardLevel);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new InvalidEventException(); //non existing card color type or non existing card level
        }

        //check if the player has discounts
        List<Resource> availableDiscount = new ArrayList<>();
        for (LeaderCard activeCard : turnLogic.getCurrentPlayer().getPersonalBoard().getActiveLeaderCards())
            activeCard.applyDiscount(availableDiscount);

        //check if the player can place and buy the card
        if(turnLogic.getCurrentPlayer().getPersonalBoard().getAvailablePlacement(chosenDevCard).size() > 0)
            if(chosenDevCard.buyCard(turnLogic.getCurrentPlayer(), resourcesPositions, availableDiscount)) {
                turnLogic.setChosenDevCard(chosenDevCard);
                // todo evento di uscita
                turnLogic.getModelInterface().notifyObservers(new PlaceDevCardSendEvent(turnLogic.getCurrentPlayer().getNickName()));
                turnLogic.setCurrentState(turnLogic.getWaitDevCardPlacement());
                hasAlreadyDoneLeaderAction = false;
                return true;
            }
        throw new InvalidEventException();
    }

    /**
     * Activate or Discard a LeaderCard if the player has not done it yet.
     *
     * @param ID of the chosen LeaderCard
     * @param discard true if the chosen LeaderCard has to be discarded, false if has to be activated
     * @return true if the leaderAction has been successfully applied
     * @throws InvalidEventException if the leaderAction can't be applied
     */
    @Override
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {

        if(hasAlreadyDoneLeaderAction)
            throw new InvalidEventException();

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

        hasAlreadyDoneLeaderAction = true;
        return true;
    }
}
