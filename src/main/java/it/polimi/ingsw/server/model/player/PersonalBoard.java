package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.cards.BasicPowerCard;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.gameBoard.EndGameObserver;
import it.polimi.ingsw.server.model.gameBoard.EndGameSubject;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.ArrayList;
import java.util.List;
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
    private final int resPointsDivider=5;
    private final int lastColumnIndex = 4;
    private final int firstColumnIndex = 1;
    private final int leaderHandSize = 2;
    private List<LeaderCard> activeLeaderCards;
    private List<List<ProductionCard>> deckProduction;
    private Warehouse warehouse;
    private EndGameObserver endGameObserver;

    public PersonalBoard() {
        warehouse = new Warehouse();
        deckProduction = new ArrayList<>();
        IntStream.range(0, lastColumnIndex).forEach(i -> deckProduction.add(new ArrayList<>()));
        activeLeaderCards = new ArrayList<>();
        deckProduction.get(0).add(new BasicPowerCard());
    }

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
        for (int i = firstColumnIndex; i < lastColumnIndex; i++) {
            if (deckProduction.get(i).size() == card.getLevel() - 1) {
                toReturn.add(i);
            }
        }
        return toReturn;
    }

    /**
     * place new DevCard at index[0] in specified position (only accept position between 1 and 3 )
     * and calls the method notifyEndGameObserver if the Player purchased his seventh card
     *
     * @param pos  placement position
     * @param card development card to place
     * @return true if placed correctly
     */
    public boolean setNewDevelopmentCard(int pos, DevelopmentCard card) {
        if (pos < firstColumnIndex || pos >= lastColumnIndex || deckProduction.stream().anyMatch(el -> el.contains(card))) {
            return false;
        }
        //check that pos is compliant with rules of placement
        if (getAvailablePlacement(card).contains(pos)) {
            //check that if there is a card under, it is acceptable to place the new one over it
            if (deckProduction.get(pos).size() > 0) {
                DevelopmentCard last = (DevelopmentCard) deckProduction.get(pos).get(0);
                //todo is this useless? methods that put cards won't allow this situation
                if (last.getLevel() >= card.getLevel()) {
                    return false;
                }
            }
            deckProduction.get(pos).add(0, card);
            int numberOfDevCards = 0;
            for (int i = 1; i < lastColumnIndex; i++)
                numberOfDevCards = numberOfDevCards + deckProduction.get(i).size();

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
    public boolean setNewDevelopmentCard(ProductionCard leader) {
        if (activeLeaderCards.contains(leader) || (deckProduction.size() >= 6)) {
            return false;
        }
        List<ProductionCard> newLevel = new ArrayList<>();
        newLevel.add(leader);
        deckProduction.add(newLevel);
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
     * get the total number of endGame points for everything currently on the board
     *
     * @return total number of points
     */
    public int getPoints(Player player) {
       List<Integer> points = new ArrayList<>();
       for(int i = firstColumnIndex;i<lastColumnIndex;i++){
           deckProduction.get(i).forEach(el->points.add(el.getPoints()));
       }
       activeLeaderCards.forEach(c->points.add(c.getPoints()));
       points.add(GameBoard.getGameBoard().getFaithTrackOfPlayer(player).getVictoryPoints());
       points.add((warehouse.getAllResources().size()/resPointsDivider));
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
     * This method calls the method update of the Observer.
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
     * @return List</DevelopmentCard> of cards on the board
     */
    public List<DevelopmentCard> getAllDevelopmentCards() {
        List<DevelopmentCard> toReturn = new ArrayList<>();
        for (int i = firstColumnIndex; i < lastColumnIndex; i++) {
            deckProduction.get(i).forEach(card -> toReturn.add((DevelopmentCard) card));
        }
        return toReturn;
    }

    //todo test this method!
    /**
     *
     * @return the the top ProductionCard of the selected slot
     * @param slotPosition of the selected slot
     */
    public ProductionCard getProductionCard(int slotPosition) throws InvalidIndexException {
        try {
            return deckProduction.get(slotPosition).get(0);
        }catch (IndexOutOfBoundsException e){
            throw new InvalidIndexException();
        }
    }
}