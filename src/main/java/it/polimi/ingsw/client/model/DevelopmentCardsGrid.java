package it.polimi.ingsw.client.model;


import it.polimi.ingsw.server.model.enums.CardColorEnum;

import java.util.*;

/**
 * Class that contains all the information to print the Development Cards Grid
 */
public class DevelopmentCardsGrid {
    private final List<Map<String, String>> mapByLevel = new ArrayList<>();
    private DevelopmentCard developmentCard;

    private final int CARD_LEVEL = 3;
    private final int CARD_LINES = 9;
    private final String EMPTY_CARD = "empty";

    public DevelopmentCardsGrid(List<String> cardIndexes) {
        firstCardsGrid(cardIndexes);
    }

    /**
     * This method set the variable mapByLevel:
     * It is a list of three Maps, three like the three possible level of the Cards.
     * Each Map has a key represented by the color of the Card and a value represented by the Card's ID
     *
     * @param cardIndexes is the List of the indexes of the Cards
     */
    private void firstCardsGrid(List<String> cardIndexes) {
        for (int i = 0; i < CARD_LEVEL; i++) {
            mapByLevel.add(new HashMap<>());
        }

        for (String indexCard : cardIndexes) {
            List<String> splitIndex = Arrays.asList(indexCard.split("_"));
            mapByLevel.get(Integer.parseInt(splitIndex.get(1)) - 1).put(splitIndex.get(0), indexCard);
        }

        // First initialize of the information about all the Development Cards
        developmentCard = new DevelopmentCard();
    }

    /**
     * This method updates the variable mapByLevel with the new Card in input
     *
     * @param color is the color of the Card
     * @param level is the level of the Card
     * @param id    is the ID of the Card
     */
    public void setDevelopmentCard(String color, int level, String id) {
        mapByLevel.get(level - 1).put(color, id);
    }

    /**
     * This method return the print of all the Development Cards Grid
     *
     * @return a List composed by the lines of the Development Cards Grid
     */
    public List<String> getPrintableDevelopmentCardsGrid() {
        List<String> cardsGrid = new ArrayList<>();

        for (int level_card = 0; level_card < CARD_LEVEL; level_card++) {
            for (int cardLine = 0; cardLine < CARD_LINES; cardLine++) {
                StringBuilder cardsGridLine = new StringBuilder();
                for (CardColorEnum cardColorEn : CardColorEnum.values()) {
                    String color_card = cardColorEn.toString();
                    String ID_card = mapByLevel.get(level_card).get(color_card);
                    if (ID_card.equals(EMPTY_CARD))
                        cardsGridLine.append(developmentCard.getPrintableEmptyDevelopmentCard().get(cardLine));
                    else
                        cardsGridLine.append(developmentCard.getPrintableDevelopmentCard(ID_card).get(cardLine));
                }
                cardsGrid.add(cardsGridLine.toString());
            }
        }
        return cardsGrid;
    }
}
