package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.cards.BasicPowerCard;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.gameBoard.EndGameObserver;
import it.polimi.ingsw.server.model.gameBoard.EndGameSubject;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of the deckProduction slots
 * Basic power:                        position 0
 * Column 1 of DevelopmentCards:       position 1
 * Column 2 of DevelopmentCards:       position 2
 * Column 3 of DevelopmentCards:       position 3
 * ProductionLeaderCards(optional):    position 4-5
 * Columns are lists with latest devCard at index 0
 */
public class PersonalBoard implements EndGameSubject {
    private static final int resPointsDivider = 5;
    private static final int lastDevSlotIndex = 4;
    private static final int firstDevSpaceIndex = 1;
    private static final int basicPowerIndex = 0;
    private static final int leaderHandSize = 2;
    private final List<LeaderCard> activeLeaderCards;
    private final List<List<ProductionCard>> productionDeck;
    private final Warehouse warehouse;
    private EndGameObserver endGameObserver;

    public PersonalBoard() {
        warehouse = new Warehouse();
        productionDeck = new ArrayList<>();
        IntStream.range(0, lastDevSlotIndex).forEach(i -> productionDeck.add(new ArrayList<>()));
        activeLeaderCards = new ArrayList<>();
        productionDeck.get(0).add(new BasicPowerCard());
    }

    /**
     * Get the player's warehouse
     *
     * @return the player's warehouse
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * Get all the currently active LeaderCards
     *
     * @return List</ LeaderCard> of active LeaderCards
     */
    public List<LeaderCard> getActiveLeaderCards() {
        return activeLeaderCards;
    }


    /**
     * Get the possible placement of a Development card
     *
     * @param card card to place
     * @return List</ Integer> of indexes where the card could be placed
     */
    public List<Integer> getAvailablePlacement(DevelopmentCard card) {
        List<Integer> toReturn = new ArrayList<>();
        for (int i = firstDevSpaceIndex; i < lastDevSlotIndex; i++) {
            if (productionDeck.get(i).size() == card.getLevel() - 1) {
                toReturn.add(i);
            }
        }
        return toReturn;
    }

    /**
     * Get all the Production Cards' IDs in all the Production Slots (including the Basic Power Card).
     * All the cards in a slot are ordered by level and the lower level card is on the first position.
     *
     * @return a list containing a all the bought cards' IDs for each slot
     */
    public List<List<String>> getAllBoughtDevelopmentCardsIDs() {
        return productionDeck.stream().limit(lastDevSlotIndex).map(slot -> {
            List<String> newSlotIDs = slot.stream().map(ProductionCard::getID).collect(Collectors.toList());
            if (newSlotIDs.size() == 0)
                newSlotIDs.add(DevelopmentCard.getEmptyCardID());
            Collections.reverse(newSlotIDs);
            return newSlotIDs;
        }).collect(Collectors.toList());
    }

    /**
     * place new DevCard at index[0] in specified position (only accept position between 1 and 3 )
     * and calls the method notifyEndGameObserver if the Player purchased his seventh card
     *
     * @param pos  placement position
     * @param card development card to place
     * @return true if placed correctly
     */
    public boolean setNewProductionCard(int pos, DevelopmentCard card) {
        if (pos < firstDevSpaceIndex || pos >= lastDevSlotIndex || productionDeck.stream().anyMatch(el -> el.contains(card))) {
            return false;
        }
        //check that pos is compliant with rules of placement
        if (getAvailablePlacement(card).contains(pos)) {
            //check that if there is a card under, it is acceptable to place the new one over it
            if (productionDeck.get(pos).size() > 0) {
                DevelopmentCard last = (DevelopmentCard) productionDeck.get(pos).get(0);
            }

            productionDeck.get(pos).add(0, card);
            int numberOfDevCards = 0;
            for (int i = 1; i < lastDevSlotIndex; i++)
                numberOfDevCards = numberOfDevCards + productionDeck.get(i).size();

            if (numberOfDevCards == 7)
                this.notifyEndGameObserver();
            return true;
        }
        return false;
    }

    /**
     * Add a LeaderCard to the list of active ProductionCards
     *
     * @param leader LeaderCard to place
     * @return true if successfully placed
     */
    public boolean setNewProductionCard(ProductionCard leader) {
        if (activeLeaderCards.contains(leader) || (productionDeck.size() >= 6))
            return false;

        List<ProductionCard> newLevel = new ArrayList<>();
        newLevel.add(leader);
        productionDeck.add(newLevel);
        return true;
    }

    /**
     * Add leaderCard to the list of active LeaderCards
     *
     * @param card card to activate
     * @return true if successfully activated
     */
    public boolean addToActiveLeaders(LeaderCard card) {
        if (!activeLeaderCards.contains(card) && activeLeaderCards.size() < leaderHandSize) {
            activeLeaderCards.add(card);
            return true;
        }
        return false;
    }

    /**
     * Get the total number of game points counting everything currently on the board of the Player.
     *
     * @param player The Player
     * @return total number of points
     */
    public int getPoints(Player player) {
        List<Integer> points = new ArrayList<>();
        for (int i = firstDevSpaceIndex; i < lastDevSlotIndex; i++) {
            productionDeck.get(i).forEach(el -> points.add(el.getPoints()));
        }
        activeLeaderCards.forEach(c -> points.add(c.getPoints()));
        points.add(GameBoard.getGameBoard().getFaithTrackOfPlayer(player).getVictoryPoints());
        points.add((warehouse.getAllResources().size() / resPointsDivider));
        return points.stream().reduce(0, Integer::sum);
    }

    /**
     * This method is used to register an observer
     *
     * @param endGameObserver is the object to add.
     */
    @Override
    public void registerEndGameObserver(EndGameObserver endGameObserver) {
        this.endGameObserver = endGameObserver;
    }

    /**
     * This method calls the method update of the SendObserver.
     * Its task is to notify the class MultiPlayerCheckWinner
     * when the Player buys is seventh Development Card.
     */
    @Override
    public void notifyEndGameObserver() {
        endGameObserver.update(false);
    }

    /**
     * Getter of all the DevelopmentCards currently on the board
     *
     * @return List</ DevelopmentCard> of cards on the board
     */
    public List<DevelopmentCard> getAllDevelopmentCards() {
        List<DevelopmentCard> toReturn = new ArrayList<>();
        for (int i = firstDevSpaceIndex; i < lastDevSlotIndex; i++) {
            productionDeck.get(i).forEach(card -> toReturn.add((DevelopmentCard) card));
        }
        return toReturn;
    }

    /**
     * Getter of the ID of all the DevelopmentCards visible on the board
     *
     * @return List<String> of ids of visible development cards
     */
    public List<String> getVisibleDevelopmentCardsIDs() {
        List<String> toReturn = new ArrayList<>();
        for (int i = basicPowerIndex; i < lastDevSlotIndex; i++) {
            try {
                toReturn.add(getProductionCard(i).getID());
            } catch (InvalidIndexException e) {
                toReturn.add(DevelopmentCard.getEmptyCardID());
            }
        }
        return toReturn;
    }

    /**
     * Getter of the Production Card of the specific Slot Position
     *
     * @param slotPosition of the selected slot
     * @return the top ProductionCard of the selected slot
     * @throws InvalidIndexException if slotPosition is an invalid index
     */
    public ProductionCard getProductionCard(int slotPosition) throws InvalidIndexException {
        try {
            return productionDeck.get(slotPosition).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidIndexException("Invalid production slot");
        }
    }
}