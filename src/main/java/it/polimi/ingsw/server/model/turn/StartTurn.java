package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.send.PlaceDevCardSendEvent;
import it.polimi.ingsw.server.events.send.PlaceResourcesSendEvent;
import it.polimi.ingsw.server.events.send.TransformationSendEvent;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.PersonalBoard;
import it.polimi.ingsw.server.model.player.Warehouse;
import it.polimi.ingsw.server.model.resources.ResourceFactory;
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

            //if player has to transform some white resources
            if(turnLogic.getWhiteResourcesFromMarket().size()>0){

                //send event
                turnLogic.getModelInterface().
                        notifyObservers(new TransformationSendEvent(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getWhiteResourcesFromMarket()));
                hasAlreadyDoneLeaderAction = false;
                turnLogic.setCurrentState(turnLogic.getWaitTransformation());
                return true;
            }

            //send event
            turnLogic.getModelInterface().
                    notifyObservers(new PlaceResourcesSendEvent(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
            hasAlreadyDoneLeaderAction = false;
            turnLogic.setCurrentState(turnLogic.getWaitResourcePlacement());
            return true;
        }
        return false;

    }

    /**
     * For all the given ProductionCard apply the production with the chosen resources.
     *
     * @param inResourcesForEachProductions containing the chosen ProductionCard and the chosen resources to apply its production
     * @param outResourcesForEachProductions containing the chosen ProdcutionCard and (if possible) the desired resources
     * @return true if the production has been correctly applied
     * @throws InvalidEventException        if one of the production can't be applied
     * @throws InvalidIndexException        if one of the index of the chosen ProductionCard doesn't exists
     * @throws NonStorableResourceException if one of the chosen resources contains a NonStorableResource
     */
    @Override
    public boolean productionAction(Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) throws InvalidEventException, InvalidIndexException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        PersonalBoard personalBoard = turnLogic.getCurrentPlayer().getPersonalBoard();
        Warehouse warehouse = personalBoard.getWarehouse();
        List<Resource> chosenInResources;
        ResourceEnum chosenOutResourceEnum;
        Resource chosenOutResource;
        ProductionCard chosenCard;

        for(Map.Entry<Integer, List<Integer>> production : inResourcesForEachProductions.entrySet()){
            Integer currentKey = production.getKey();
            //todo controllare in caso getRes sia null
            chosenInResources = new ArrayList<>(warehouse.getResources(production.getValue()));

            if(outResourcesForEachProductions.get(currentKey) == null)
                chosenOutResource = null;
            else {
                try {
                    chosenOutResourceEnum = ResourceEnum.valueOf(outResourcesForEachProductions.get(currentKey));
                } catch (IllegalArgumentException e) {
                    throw new InvalidEventException("resource does not exist"); //not existing ResourceEnum
                }
                chosenOutResource = new ResourceFactory().produceResource(chosenOutResourceEnum); //throws NonStorableResourceException if RED or WHITE
            }

            chosenCard = personalBoard.getProductionCard(currentKey);

            List<Resource> finalChosenInResources = chosenInResources;
            Resource finalChosenOutResource = chosenOutResource;
            List<Resource> productionResources = new ArrayList<Resource>(){{
                addAll(finalChosenInResources);
                add(finalChosenOutResource);
            }};

            if(!chosenCard.canDoProduction(productionResources))
                throw new InvalidEventException("selected card can't do production with selected resources");
            if(!chosenCard.usePower(turnLogic))
                throw new InvalidEventException("production failed");
            //payment
            warehouse.takeResources(production.getValue());
        }
        hasAlreadyDoneLeaderAction = false;
        turnLogic.setCurrentState(turnLogic.getEndTurn());
        return true;
    }

    /**
     * Check if the player can place the card and then check if he can buy it with his discounts.
     * If yes buy the card and set the next State of the game to WaitDevelopmentCardPlacement.
     *
     * @param cardColor color of the card to buy
     * @param cardLevel level of the card to buy
     * @param resourcePositions index of the chosen resources
     * @return true if the card has been successfully bought
     * @throws InvalidEventException if the player can't buy the card
     * @throws InvalidIndexException if one of the resource positions is negative
     * @throws EmptySlotException if one of the resource slots is empty
     * @throws NonAccessibleSlotException if one of the resource position represents a slot that's not accessible
     */
    @Override
    public boolean buyAction(String cardColor, int cardLevel, List<Integer> resourcePositions) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        DevelopmentCard chosenDevelopmentCard;
        try {
            CardColorEnum chosenColorEnum = CardColorEnum.valueOf(cardColor.toUpperCase());
            chosenDevelopmentCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(chosenColorEnum, cardLevel);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new InvalidEventException("not existing card color/card level"); //non existing card color type or non existing card level
        }

        //check if the player has discounts
        List<Resource> availableDiscount = new ArrayList<>();
        for (LeaderCard activeCard : turnLogic.getCurrentPlayer().getPersonalBoard().getActiveLeaderCards())
            activeCard.applyDiscount(availableDiscount);

        //check if the player can place and buy the card
        if(turnLogic.getCurrentPlayer().getPersonalBoard().getAvailablePlacement(chosenDevelopmentCard).size() > 0)
            if(chosenDevelopmentCard.buyCard(turnLogic.getCurrentPlayer(), resourcePositions, availableDiscount)) {
                turnLogic.setChosenDevCard(chosenDevelopmentCard);

                //send event
                turnLogic.getModelInterface().notifyObservers(new PlaceDevCardSendEvent(turnLogic.getCurrentPlayer().getNickname(), chosenDevelopmentCard));
                turnLogic.setCurrentState(turnLogic.getWaitDevCardPlacement());
                hasAlreadyDoneLeaderAction = false;
                return true;
            }
        throw new InvalidEventException("could not buy/place the card");
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
            throw new InvalidEventException("this action was already performed");

        Player currentPlayer = turnLogic.getCurrentPlayer();

        //get the chosen leader card
        LeaderCard chosenLeaderCard = currentPlayer.getLeaderHand().stream()
                                .filter(card -> card.getID().equals(ID)).findFirst()
                                .orElseThrow(() -> new InvalidEventException("leaderCard is not owned"));
        //if the card has to be discarded
        if(discard){
            if(!currentPlayer.discardLeader(chosenLeaderCard))
                throw new InvalidEventException("can't discard this card");
        }else
        //if the card has to be activated
        {
            if(!currentPlayer.activateLeaderCard(chosenLeaderCard))
                throw new InvalidEventException("leaderCard activation failed");
        }

        hasAlreadyDoneLeaderAction = true;
        return true;
    }
}
