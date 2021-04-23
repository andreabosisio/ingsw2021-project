package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.CardsGenerator;
import it.polimi.ingsw.server.model.enums.CardColorEnum;

import java.util.*;

public class DevelopmentCardsGrid implements EndGameSubject {
    private static final int numOfLevels = 3;
    private List<Map<CardColorEnum, List<DevelopmentCard>>> mapByLevel;
    private final List<DevelopmentCard> developmentCards;
    private final CardsGenerator generator = new CardsGenerator();
    private EndGameObserver iCheckWinner;

    public DevelopmentCardsGrid() {
        developmentCards = generator.generateDevelopmentCards();
        shuffle(developmentCards);
        mapByLevel = new ArrayList<>();
        for (int i = 1; i <= numOfLevels; i++) {
            mapByLevel.add(generator.getDevCardsAsGrid(developmentCards, i));
        }
    }

    /**
     * shuffle the List</developmentCard> given
     *
     * @param cards List of cards to shuffle
     */
    private void shuffle(List<DevelopmentCard> cards) {
        Collections.shuffle(cards);
    }

    /**
     * Create a list of all the cards in the grid visible for the player
     *
     * @return the List of available devCards
     */
    public List<DevelopmentCard> getAvailableCards() {
        List<DevelopmentCard> toReturn = new ArrayList<>();
        mapByLevel.forEach((level) -> level.forEach((key, value) -> {
            if (value.size() != 0) {
                toReturn.add(value.get(0));
            }
        }));
        return toReturn;
    }

    /**
     * Remove the lowest level card of the given color
     * and calls the method notifyEndGameObserver if there is no more Cards of that color.
     *
     * @param color color of the card to remove
     * @return true if there was at least one card of that color in the grid
     */
    public boolean removeCardByColor(CardColorEnum color) {
        boolean emptyColumn = true;
        //for and not foreach because foreach can't be interrupted
        for (Map<CardColorEnum, List<DevelopmentCard>> m : mapByLevel)
            if (m.get(color).size() != 0) {
                m.get(color).remove(0);
                emptyColumn = false;
                break;
            }

        if (hasEmptyColumn())
            this.notifyEndGameObserver();
        return !emptyColumn;
    }

    /**
     * Check if all the cards of at least one color have been removed
     *
     * @return true if one color has no more cards
     */
    public boolean hasEmptyColumn() {
        for (CardColorEnum color : EnumSet.allOf(CardColorEnum.class)) {
            boolean empty = true;
            for (Map<CardColorEnum, List<DevelopmentCard>> m : mapByLevel) {
                if (m.get(color).size() != 0) {
                    empty = false;
                    break;
                }
            }
            if (empty) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove the specific Development Card from the Grid
     * and calls the method notifyEndGameObserver if a column of Cards is empty
     *
     * @param developmentCard is the Development Card to remove
     */
    // TODO: Remember to test! And in case changes index of level
    public boolean removeCard(DevelopmentCard developmentCard) {
        if (!mapByLevel.get(developmentCard.getLevel() - 1).get(developmentCard.getColor()).contains(developmentCard))
            return false;
        mapByLevel.get(developmentCard.getLevel() - 1).get(developmentCard.getColor()).remove(developmentCard);
        if (hasEmptyColumn())
            notifyEndGameObserver();
        return true;
    }

    /**
     * This method is used by the Game Board to register the unique observer
     *
     * @param iCheckWinner is the object to add.
     */
    @Override
    public void registerEndGameObserver(EndGameObserver iCheckWinner) {
        this.iCheckWinner = iCheckWinner;
    }

    /**
     * This method calls the method update of the Observer.
     * Its task is to notify the class SinglePlayerCheckWinner or MultiPlayerCheckWinner
     * of the end of a color of Development Cards.
     */
    @Override
    public void notifyEndGameObserver() {
        iCheckWinner.update(true);
    }


    //todo remember to test for index starting by 0 or 1
    public DevelopmentCard getCardByColorAndLevel(CardColorEnum color,int level) throws IndexOutOfBoundsException{
        //index out of bounds exception
        return mapByLevel.get(level-1).get(color).get(0);
    }
}