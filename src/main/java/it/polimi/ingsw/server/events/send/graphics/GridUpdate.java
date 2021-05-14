package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.List;
import java.util.stream.Collectors;

public class GridUpdate {
    private final Integer level;
    private final String color;
    private String iD;
    private final List<String> fullGrid;

    public GridUpdate(CardColorEnum color, int level) {
        this.level = level;
        this.color = color.toString();
        //todo: togli il try catch e metti return null (getCardByColorAndLevel) e iD final
        try {
            this.iD = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(color, level).getID();
        } catch (IndexOutOfBoundsException e) {
            this.iD = "empty";
        }
        this.fullGrid = null;
    }

    public GridUpdate() {
        this.level = null;
        this.color = null;
        this.iD = null;
        this.fullGrid = GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards()
                .stream().map(DevelopmentCard::getID)
                .collect(Collectors.toList());
    }
}
