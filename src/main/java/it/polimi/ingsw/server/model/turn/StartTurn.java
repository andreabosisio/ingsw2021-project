package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.send.choice.*;
import it.polimi.ingsw.server.events.send.graphics.*;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.PersonalBoard;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;
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
        if (turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()
                .addResourcesFromMarket(GameBoard.getGameBoard().getMarketTray().takeResources(arrowID))) {

            //graphic Update of marketTray for all players
            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
            graphicUpdateEvent.addUpdate(new MarketUpdate());
            graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
            graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(),turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
            turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);


            //if player has to transform some white resources
            ChoiceEvent choiceEvent;
            if (turnLogic.getWhiteResourcesFromMarket().size() > 0) {

                //send event and save choice data
                choiceEvent = new TransformationChoiceEvent(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getWhiteResourcesFromMarket());
                turnLogic.setLastEventSent(choiceEvent);
                turnLogic.getModelInterface().
                        notifyObservers(choiceEvent);
                hasAlreadyDoneLeaderAction = false;
                turnLogic.setCurrentState(turnLogic.getWaitTransformation());
                return true;
            }

            //send event and save choice data
            choiceEvent = new PlaceResourcesChoiceEvent(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse());
            turnLogic.setLastEventSent(choiceEvent);
            turnLogic.getModelInterface().
                    notifyObservers(new PlaceResourcesChoiceEvent(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
            hasAlreadyDoneLeaderAction = false;
            turnLogic.setCurrentState(turnLogic.getWaitResourcePlacement());
            return true;
        }
        return false;
    }

    /**
     * For all the given ProductionCard apply the production with the chosen resources.
     *
     * @param inResourcesForEachProductions  containing the chosen ProductionCard and the chosen resources to apply its production
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

        for (Map.Entry<Integer, List<Integer>> production : inResourcesForEachProductions.entrySet()) {
            Integer currentKey = production.getKey();

            chosenInResources = new ArrayList<>(warehouse.getResources(production.getValue()));

            if (outResourcesForEachProductions.get(currentKey) == null)
                chosenOutResource = null;
            else {
                try {
                    chosenOutResourceEnum = ResourceEnum.valueOf(outResourcesForEachProductions.get(currentKey));
                } catch (IllegalArgumentException e) {
                    throw new InvalidEventException("'" + outResourcesForEachProductions.get(currentKey) + "' isn't a valid resource"); //not existing ResourceEnum
                }
                chosenOutResource = new ResourceFactory().produceResource(chosenOutResourceEnum); //throws NonStorableResourceException if RED or WHITE
            }

            chosenCard = personalBoard.getProductionCard(currentKey);

            List<Resource> finalChosenInResources = chosenInResources;
            Resource finalChosenOutResource = chosenOutResource;
            List<Resource> productionResources = new ArrayList<Resource>() {{
                addAll(finalChosenInResources);
                add(finalChosenOutResource);
            }};

            if (!chosenCard.canDoProduction(productionResources))
                throw new InvalidEventException("Slot number " + currentKey + " can't do production with selected resources");
        }

        //if all the selected productions are valid
        for (Map.Entry<Integer, List<Integer>> production : inResourcesForEachProductions.entrySet()) {
            Integer currentKey = production.getKey();

            //finally activate the production
            if (!personalBoard.getProductionCard(currentKey).usePower(turnLogic))
                throw new InvalidEventException("production failed"); //impossible condition

            //payment
            warehouse.takeResources(production.getValue());
        }

        //graphic Update of player's warehouse and faithTrack
        GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
        graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
        graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
        turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);

        hasAlreadyDoneLeaderAction = false;
        turnLogic.setCurrentState(turnLogic.getEndTurn());
        turnLogic.getModelInterface().notifyObservers(new EndTurnChoiceEvent(turnLogic.getCurrentPlayer().getNickname()));
        return true;
    }

    /**
     * Check if the player can place the card and then check if he can buy it with his discounts.
     * If yes buy the card and set the next State of the game to WaitDevelopmentCardPlacement.
     *
     * @param cardColor         color of the card to buy
     * @param cardLevel         level of the card to buy
     * @param resourcePositions index of the chosen resources
     * @return true if the card has been successfully bought
     * @throws InvalidEventException      if the player can't buy the card
     * @throws InvalidIndexException      if one of the resource positions is negative
     * @throws EmptySlotException         if one of the resource slots is empty
     * @throws NonAccessibleSlotException if one of the resource position represents a slot that's not accessible
     */
    @Override
    public boolean buyAction(String cardColor, int cardLevel, List<Integer> resourcePositions) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        DevelopmentCard chosenDevelopmentCard;
        CardColorEnum chosenColorEnum;
        try {
            chosenColorEnum = CardColorEnum.valueOf(cardColor.toUpperCase());
            chosenDevelopmentCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(chosenColorEnum, cardLevel);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new InvalidEventException("not existing card color/card level"); //non existing card color type or non existing card level
        }

        //check if the player has discounts
        List<Resource> availableDiscount = new ArrayList<>();
        for (LeaderCard activeCard : turnLogic.getCurrentPlayer().getPersonalBoard().getActiveLeaderCards())
            activeCard.applyDiscount(availableDiscount);

        //check if the player can place and buy the card
        if (turnLogic.getCurrentPlayer().getPersonalBoard().getAvailablePlacement(chosenDevelopmentCard).size() > 0)
            if (chosenDevelopmentCard.buyCard(turnLogic.getCurrentPlayer(), resourcePositions, availableDiscount)) {
                turnLogic.setChosenDevCard(chosenDevelopmentCard);

                //graphic update of DevelopmentCardsGrid for all players
                GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
                graphicUpdateEvent.addUpdate(new GridUpdate(chosenColorEnum, cardLevel));
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
                turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);

                //send event and save choice
                ChoiceEvent choiceEvent = new PlaceDevCardChoiceEvent(turnLogic.getCurrentPlayer().getNickname(), chosenDevelopmentCard);
                turnLogic.setLastEventSent(choiceEvent);
                turnLogic.getModelInterface().notifyObservers(choiceEvent);
                turnLogic.setCurrentState(turnLogic.getWaitDevCardPlacement());
                hasAlreadyDoneLeaderAction = false;
                return true;
            }
        throw new InvalidEventException("could not buy/place the card");
    }

    /**
     * Activate or Discard a LeaderCard if the player has not done it yet.
     *
     * @param ID      of the chosen LeaderCard
     * @param discard true if the chosen LeaderCard has to be discarded, false if has to be activated
     * @return true if the leaderAction has been successfully applied
     * @throws InvalidEventException if the leaderAction can't be applied
     */
    @Override
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {

        if (hasAlreadyDoneLeaderAction)
            throw new InvalidEventException("this action was already performed");

        Player currentPlayer = turnLogic.getCurrentPlayer();

        //get the chosen leader card
        LeaderCard chosenLeaderCard = currentPlayer.getLeaderHand().stream()
                .filter(card -> card.getID().equals(ID)).findFirst()
                .orElseThrow(() -> new InvalidEventException("leaderCard is not owned"));
        //if the card has to be discarded
        if (discard) {
            if (!currentPlayer.discardLeader(chosenLeaderCard))
                throw new InvalidEventException("can't discard this card");
            else {

                //graphic update of faithTracks and player's owned leaderCards
                GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer()));
                graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
                turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            }
        } else
        //if the card has to be activated
        {
            if (!currentPlayer.activateLeaderCard(chosenLeaderCard))
                throw new InvalidEventException("leaderCard activation failed");
            else {
                //graphic update of leaderCards owned by the player
                GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer()));
                turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            }
        }
        hasAlreadyDoneLeaderAction = true;
        //todo check
        turnLogic.getModelInterface().reSendLastEvent();
        return true;
    }
}
