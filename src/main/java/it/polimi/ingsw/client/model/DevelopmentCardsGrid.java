package it.polimi.ingsw.client.model;


import it.polimi.ingsw.server.model.enums.CardColorEnum;

import java.util.*;

/**
 * Class that has the capacity to print the Development Cards Grid
 */
public class DevelopmentCardsGrid {
    private List<Map<String, String>> mapByLevel;

    private Integer level;
    private String color;
    private String iD;
    private final List<String> fullGrid;

    private static final int CARD_LEVEL = 3;
    private static final int CARD_LINES = 9;
    private static final String EMPTY_CARD = "empty";

    public DevelopmentCardsGrid(List<String> fullGrid) {
        this.fullGrid = fullGrid;
    }

    /**
     * Update method that checks if there is a first implementation of this class
     */
    public void update(){
        if (fullGrid != null) {
            setup();
            Board.getBoard().setDevelopmentCardsGrid(this);
        }
        else {
            Board.getBoard().getDevelopmentCardsGrid().setDevelopmentCard(this.color, this.level, this.iD);
        }
    }

    /**
     * This method set the variable mapByLevel:
     * It is a list of three Maps, three like the three possible level of the Cards.
     * Each Map has a key represented by the color of the Card and a value represented by the Card's ID
     */
    private void setup() {
        this.mapByLevel = new ArrayList<>();
        for (int i = 0; i < CARD_LEVEL; i++) {
            mapByLevel.add(new HashMap<>());
        }

        for (String indexCard : fullGrid) {
            String[] splitIndex = indexCard.split("_");
            mapByLevel.get(Integer.parseInt(splitIndex[1]) - 1).put(splitIndex[0], indexCard);
        }
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
    public List<String> getPrintable() {
        List<String> cardsGrid = new ArrayList<>();
        DevelopmentCard developmentCard = new DevelopmentCard();

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

    public Integer getLevel() {
        return level;
    }

    public String getColor() {
        return color;
    }

    public String getiD() {
        return iD;
    }
}
