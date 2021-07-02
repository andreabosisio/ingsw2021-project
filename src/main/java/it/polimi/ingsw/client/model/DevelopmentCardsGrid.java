package it.polimi.ingsw.client.model;


import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.cli.Printable;
import it.polimi.ingsw.commons.enums.CardColorsEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class that has the capacity to print the Development Cards Grid
 */
public class DevelopmentCardsGrid extends Printable {
    private List<Map<String, String>> mapByLevel;

    private Integer level;
    private String color;
    private String iD;
    private final List<String> fullGrid;

    private static final int CARD_LEVELS = 3;
    private static final int CARD_LINES = 9;
    private static final int CARD_PER_LEVEL = 4;

    /**
     * Create a new Development Cards Grid by specifying its content.
     *
     * @param fullGrid A list containing all the Cards to put in the Grid in a specific order
     */
    public DevelopmentCardsGrid(List<String> fullGrid) {
        this.fullGrid = fullGrid;
    }

    /**
     * Update method that checks if there is a first implementation of this class,
     * if yes it sets the map that represents the Grid and it saves his self in the Board,
     * otherwise it just update the map with the new Card.
     *
     * @param view The UI
     */
    public void update(View view) {
        if (fullGrid != null) {
            setup();
            Board.getBoard().setDevelopmentCardsGrid(this);
        } else {
            Board.getBoard().getDevelopmentCardsGrid().setDevelopmentCard(this.color, this.level, this.iD);
            view.gridUpdate(this.iD);
        }
    }

    /**
     * This method set the variable mapByLevel:
     * It is a list of three Maps, three like the three possible level of the Cards.
     * Each Map has a key represented by the color of the Card and a value represented by the Card's ID
     */
    private void setup() {
        this.mapByLevel = new ArrayList<>();
        for (int i = 0; i < CARD_LEVELS; i++) {
            mapByLevel.add(new HashMap<>());
        }

        for (String indexCard : fullGrid) {
            if (!indexCard.equals(DevelopmentCard.EMPTY_CARD_ID)) {
                String[] splitIndex = indexCard.split("_");
                mapByLevel.get(Integer.parseInt(splitIndex[1]) - 1).put(splitIndex[0], indexCard);
            }
        }

        for (int level_card = 0; level_card < CARD_LEVELS; level_card++) {
            for (CardColorsEnum cardColorEn : CardColorsEnum.values()) {
                mapByLevel.get(level_card).putIfAbsent(String.valueOf(cardColorEn), DevelopmentCard.EMPTY_CARD_ID);
            }
        }
    }

    /**
     * This method updates the variable mapByLevel with the new Card in input
     *
     * @param color is the color of the Card
     * @param level is the level of the Card
     * @param id    is the ID of the Card
     */
    private void setDevelopmentCard(String color, int level, String id) {
        mapByLevel.get(level - 1).put(color, id);
    }

    /**
     * This method return the print of all the Development Cards Grid
     *
     * @return a List composed by the lines of the Development Cards Grid
     */
    @Override
    public List<String> getPrintable() {
        List<String> cardsGrid = new ArrayList<>();

        for (int level_card = 0; level_card < CARD_LEVELS; level_card++) {
            for (int cardLine = 0; cardLine < CARD_LINES; cardLine++) {
                StringBuilder cardsGridLine = new StringBuilder();
                for (CardColorsEnum cardColorEn : CardColorsEnum.values()) {
                    String color_card = cardColorEn.toString();
                    String ID_card = mapByLevel.get(level_card).get(color_card);
                    cardsGridLine.append(DevelopmentCardsDatabase.getDevelopmentCardsDatabase().createDevelopmentCardByID(ID_card).getPrintable().get(cardLine));
                }
                cardsGrid.add(cardsGridLine.toString());
            }
        }

        setWidth(cardsGrid);
        return cardsGrid;
    }

    /**
     * This method returns a list with all the IDs saved in the grid
     * The IDs are in order of ascending level first and color second
     *
     * @return a list with all the IDs
     */
    public List<String> toStringList() {
        List<String> toReturn = new ArrayList<>();
        mapByLevel.forEach((level) -> level.forEach((key, value) -> toReturn.add(value)));
        return toReturn;
    }
}