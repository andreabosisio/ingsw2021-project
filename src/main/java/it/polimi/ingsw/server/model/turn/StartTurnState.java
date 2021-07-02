package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.events.send.choice.*;
import it.polimi.ingsw.server.events.send.graphics.*;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.PersonalBoard;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.ResourceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * State of the Model that accepts the initial actions of the Player
 */
public class StartTurnState extends State {
    public StartTurnState(ModelInterface modelInterface) {
        super(modelInterface);
    }

    private boolean hasAlreadyDoneLeaderAction = false;

    /**
     * Take the chosen resources from the MarketTray and set the current state of the game to
     * WaitResourceTransformation if there are some White Resources to transform or else to
     * WaitResourcePlacementState.
     *
     * @param arrowID is the index of the chosen line of the MarketTray
     * @return true if the state has been changed
     * @throws InvalidIndexException if the arrowID is not correct
     */
    @Override
    public boolean marketAction(int arrowID) throws InvalidIndexException {
        if (getCurrentPlayer().getPersonalBoard().getWarehouse()
                .addResourcesFromMarket(GameBoard.getGameBoard().getMarketTray().takeResources(arrowID))) {

            //graphic Update of marketTray for all players
            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
            graphicUpdateEvent.addUpdate(new MarketUpdate());
            graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
            graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(getCurrentPlayer(), new WarehouseUpdate()));
            graphicUpdateEvent.addUpdate(getCurrentPlayer().getNickname() + " took some resources from the Market Tray! \n Now he's reordering them...");
            turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);

            //if player has to transform some white resources
            ChoiceEvent choiceEvent;
            if (turnLogic.getWhiteResourcesFromMarket().size() > 0) {
                //send event and save choice data
                choiceEvent = new TransformationChoiceEvent(getCurrentPlayer().getNickname(), turnLogic.getWhiteResourcesFromMarket());
                turnLogic.setLastEventSent(choiceEvent);
                turnLogic.getModelInterface().notifyObservers(choiceEvent);
                hasAlreadyDoneLeaderAction = false;
                modelInterface.setCurrentState(modelInterface.getWaitTransformation());
                return true;
            }

            //send event and save choice data
            choiceEvent = new PlaceResourcesChoiceEvent(getCurrentPlayer().getNickname());
            turnLogic.setLastEventSent(choiceEvent);
            turnLogic.getModelInterface().
                    notifyObservers(new PlaceResourcesChoiceEvent(getCurrentPlayer().getNickname()));
            hasAlreadyDoneLeaderAction = false;
            modelInterface.setCurrentState(modelInterface.getWaitResourcePlacement());
            return true;
        }
        return false;
    }


    @Override
    public boolean productionAction(Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) throws InvalidEventException, InvalidIndexException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        PersonalBoard personalBoard = getCurrentPlayer().getPersonalBoard();
        Warehouse warehouse = personalBoard.getWarehouse();
        List<Resource> chosenInResources;
        ResourcesEnum chosenOutResourceEnum;
        Resource chosenOutResource;
        ProductionCard chosenCard;

        for (Map.Entry<Integer, List<Integer>> production : inResourcesForEachProductions.entrySet()) {
            Integer currentKey = production.getKey();

            chosenInResources = new ArrayList<>(warehouse.getResources(production.getValue()));

            if (outResourcesForEachProductions.get(currentKey) == null)
                chosenOutResource = null;
            else {
                try {
                    chosenOutResourceEnum = ResourcesEnum.valueOf(outResourcesForEachProductions.get(currentKey));
                } catch (IllegalArgumentException e) {
                    throw new InvalidEventException("'" + outResourcesForEachProductions.get(currentKey) + "' isn't a valid resource"); //not existing ResourcesEnum
                }
                chosenOutResource = ResourceFactory.produceResource(chosenOutResourceEnum); //throws NonStorableResourceException if RED or WHITE
            }

            chosenCard = personalBoard.getProductionCard(currentKey);

            List<Resource> finalChosenInResources = chosenInResources;
            Resource finalChosenOutResource = chosenOutResource;
            List<Resource> productionResources = new ArrayList<>() {{
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
            else {
                GraphicUpdateEvent infoMsg = new GraphicUpdateEvent();
                infoMsg.addUpdate(getCurrentPlayer().getNickname() + " produced this resources: " + personalBoard.getProductionCard(currentKey).getOutResources().stream().map(r -> r.getColor().toString()).collect(Collectors.toList()) + "!");
                turnLogic.getModelInterface().notifyObservers(infoMsg);
            }

            //payment
            warehouse.takeResources(production.getValue());
        }

        //graphic Update of player's warehouse and faithTrack
        GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
        graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(getCurrentPlayer(), new WarehouseUpdate()));
        graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
        turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);

        hasAlreadyDoneLeaderAction = false;
        EndTurnChoiceEvent endTurnChoiceEvent = new EndTurnChoiceEvent(getCurrentPlayer().getNickname());
        turnLogic.setLastEventSent(endTurnChoiceEvent);
        turnLogic.getModelInterface().notifyObservers(endTurnChoiceEvent);
        modelInterface.setCurrentState(modelInterface.getEndTurn());
        return true;
    }

    /**
     * Check if the player can place the card and then check if he can buy it with his discounts.
     * If yes buy the card and set the next State of the game to WaitDevelopmentCardPlacementState.
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
        CardColorsEnum chosenColorEnum;
        try {
            chosenColorEnum = CardColorsEnum.valueOf(cardColor.toUpperCase());
            chosenDevelopmentCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(chosenColorEnum, cardLevel);
            if (chosenDevelopmentCard.isTheEmptyCard())
                throw new InvalidEventException("Invalid card level");
        } catch (IllegalArgumentException e) {
            throw new InvalidEventException("Invalid card color"); //non existing card color type
        }

        //check if the player has discounts
        List<Resource> availableDiscount = new ArrayList<>();
        for (LeaderCard activeCard : getCurrentPlayer().getPersonalBoard().getActiveLeaderCards())
            activeCard.applyDiscount(availableDiscount);

        //check if the player can place and buy the card
        if (getCurrentPlayer().getPersonalBoard().getAvailablePlacement(chosenDevelopmentCard).size() > 0)
            if (chosenDevelopmentCard.buyCard(getCurrentPlayer(), resourcePositions, availableDiscount)) {
                turnLogic.setChosenDevCard(chosenDevelopmentCard);

                //graphic update of DevelopmentCardsGrid for all players
                GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
                graphicUpdateEvent.addUpdate(new GridUpdate(chosenColorEnum, cardLevel));
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(getCurrentPlayer(), new WarehouseUpdate()));
                graphicUpdateEvent.addUpdate(getCurrentPlayer().getNickname() + " purchased a " + chosenColorEnum.toString().toUpperCase(Locale.ROOT) + " Development Card of level " + cardLevel + "!");
                turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);

                //send event and save choice
                ChoiceEvent choiceEvent = new PlaceDevCardChoiceEvent(getCurrentPlayer().getNickname(), chosenDevelopmentCard);
                turnLogic.setLastEventSent(choiceEvent);
                turnLogic.getModelInterface().notifyObservers(choiceEvent);
                modelInterface.setCurrentState(modelInterface.getWaitDevCardPlacement());
                hasAlreadyDoneLeaderAction = false;
                return true;
            }
        throw new InvalidEventException("Could not buy (or place) the card");
    }

    /**
     * Activate or Discard a LeaderCard if the player has not done it yet.
     *
     * @param cardID  of the chosen LeaderCard
     * @param discard true if the chosen LeaderCard has to be discarded, false if has to be activated
     * @return true if the leaderAction has been successfully applied
     * @throws InvalidEventException if the leaderAction can't be applied
     */
    @Override
    public boolean leaderAction(String cardID, boolean discard) throws InvalidEventException {

        if (hasAlreadyDoneLeaderAction)
            throw new InvalidEventException("You have already performed a Leader Action!");

        executeLeaderAction(cardID, discard);

        hasAlreadyDoneLeaderAction = true;
        turnLogic.getModelInterface().reSendLastEvent();
        return true;
    }

    /**
     * This method tries to do a leader action be it activation or discard
     *
     * @param cardID ID of the leader card
     * @param discard true if the card must be discarded/ false if activated
     * @throws InvalidEventException if the vent fails
     */
    protected void executeLeaderAction(String cardID, boolean discard) throws InvalidEventException {

        //get the chosen leader card
        LeaderCard chosenLeaderCard = getCurrentPlayer().getLeaderHand().stream()
                .filter(card -> card.getID().equals(cardID)).findFirst()
                .orElseThrow(() -> new InvalidEventException("Chosen Leader Card is not owned"));


        //if the card has to be discarded
        if (discard) {
            if (!getCurrentPlayer().discardLeader(chosenLeaderCard))
                throw new InvalidEventException("You cannot discard this card right now");
            else {
                //graphic update of faithTracks and player's owned leaderCards
                GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(getCurrentPlayer(), new LeaderCardSlotsUpdate()));
                graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
                graphicUpdateEvent.addUpdate(getCurrentPlayer().getNickname() + " discarded a Leader Card!");
                turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            }
        } else
        //if the card has to be activated
        {
            if (!getCurrentPlayer().activateLeaderCard(chosenLeaderCard))
                throw new InvalidEventException("You cannot activate this card right now");
            else {
                //graphic update of leaderCards owned by the player
                GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(getCurrentPlayer(), new LeaderCardSlotsUpdate()));
                graphicUpdateEvent.addUpdate(getCurrentPlayer().getNickname() + " activated a Leader Card!");
                turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            }
        }
    }
}
