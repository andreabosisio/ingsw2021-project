package it.polimi.ingsw.server.model.gameBoard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.server.model.cards.CardsGenerator;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements the Development Cards Grid of the Board.
 */
public class DevelopmentCardsGrid implements EndGameSubject {
    private static final int numOfLevels = 3;
    private final List<Map<CardColorsEnum, List<DevelopmentCard>>> mapByLevel;
    private final List<DevelopmentCard> developmentCards;
    private final CardsGenerator generator = new CardsGenerator();
    private EndGameObserver iCheckWinner;

    /**
     * Create a new Development Cards Grid by reading the default configuration in a file and shuffling it.
     */
    public DevelopmentCardsGrid() {
        developmentCards = generator.generateDevelopmentCards();
        shuffle(developmentCards);
        mapByLevel = new ArrayList<>();
        populateGrid();
    }

    /**
     * This method populate the List of Map that contains the Development Cards Grid,
     * for each level it add a map using CardColorsEnum as key and the List of Development Cards as value.
     */
    private void populateGrid() {
        mapByLevel.clear();
        for (int i = 1; i <= numOfLevels; i++) {
            mapByLevel.add(generator.getDevCardsAsGrid(developmentCards, i));
        }
    }

    /**
     * Shuffle the List</DevelopmentCard> given
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
     * Get method that return all the visible Development Cards of the Grid
     *
     * @return a List of the visible Development Cards
     */
    public List<String> getFullGrid() {
        List<String> toReturn = new ArrayList<>();
        mapByLevel.forEach((level) -> level.forEach((key, value) -> {
            if (value.size() != 0) {
                toReturn.add(value.get(0).getID());
            } else
                toReturn.add(DevelopmentCard.getEmptyCardID());
        }));
        return toReturn;
    }


    /**
     * Remove the lowest level card of the given color
     * and calls the method notifyEndGameObserver if there is no more Cards of that color.
     *
     * @param color color of the card to remove
     * @return the iD of the card removed or null
     */
    public DevelopmentCard removeCardByColor(CardColorsEnum color) {
        DevelopmentCard removedCard = null;

        //for and not foreach because foreach can't be interrupted
        for (Map<CardColorsEnum, List<DevelopmentCard>> m : mapByLevel) {
            if (m.get(color).size() != 0) {
                removedCard = m.get(color).remove(0);
                break;
            }
        }

        if (removedCard == null)
            removedCard = new DevelopmentCard();

        if (hasEmptyColumn())
            this.notifyEndGameObserver();
        return removedCard;
    }

    /**
     * Check if all the cards of at least one color have been removed
     * <p>
     * It does so by counting the number of columns with at least one card in them
     * and checking if they are less than 4
     *
     * @return true if one color has no more cards
     */
    public boolean hasEmptyColumn() {
        return mapByLevel.stream().flatMap(map -> map.entrySet().stream()
                .filter(colorSet -> colorSet.getValue().size() > 0)
                .map(Map.Entry::getKey)).distinct().count() < 4;
    }

    /**
     * Remove the specific Development Card from the Grid
     * and calls the method notifyEndGameObserver if a column of Cards is empty
     *
     * @param developmentCard is the Development Card to remove
     * @return true if the grid contain the developmentCard, false otherwise
     */
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
     * This method calls the method update of the SendObserver.
     * Its task is to notify the class SinglePlayerCheckWinner or MultiPlayerCheckWinner
     * of the end of a color of Development Cards.
     */
    @Override
    public void notifyEndGameObserver() {
        iCheckWinner.update(true);
    }

    /**
     * This method find the requested DevelopmentCard and return it
     *
     * @param color the color of the requested card
     * @param level the level of the requested card
     * @return the requested DevelopmentCard
     */
    public DevelopmentCard getCardByColorAndLevel(CardColorsEnum color, int level) {
        //index out of bounds exception if card is not present
        try {
            return mapByLevel.get(level - 1).get(color).get(0);
        } catch (IndexOutOfBoundsException e) {
            return new DevelopmentCard();
        }
    }

    /**
     * Method used in testing: fill the grid following the developmentsCards.json order
     * in order to have no random elements
     */
    public void setNonRandom() {
        developmentCards.clear();
        developmentCards.addAll(generator.generateDevelopmentCards());
        populateGrid();
    }

    /**
     * This method writes in a Json file the ID of the Card of the Development Cards Grid
     */
    public void saveData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cards", Parser.toJsonTree(developmentCards.stream().map(DevelopmentCard::getID).collect(Collectors.toList())));
        FileUtilities.writeJsonElementInFile(jsonObject, FileUtilities.SAVED_DEV_CARD_DATA_PATH);
    }

    /**
     * This method loads from a Json file the ID of the Cards of the Development Cards Grid
     */
    public void loadSavedData() {
        developmentCards.clear();
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.SAVED_DEV_CARD_DATA_PATH);
        JsonArray jsonArrayOfDevelopmentCards = Parser.extractFromField(Objects.requireNonNull(fileElement), "cards").getAsJsonArray();
        for (JsonElement el : jsonArrayOfDevelopmentCards) {
            String cardId = el.getAsString();
            developmentCards.add(generator.generateDevelopmentCardFromId(cardId));
        }
        populateGrid();
    }
}