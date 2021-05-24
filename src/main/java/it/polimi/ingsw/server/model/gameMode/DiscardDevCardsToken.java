package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.events.send.graphics.GridUpdate;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.turn.TurnLogic;


/**
 * Class that represents the token that discards two Development Cards
 * of the color indicated.
 */
public class DiscardDevCardsToken implements SoloActionToken {
    private final CardColorEnum color;
    private static final int CARDS_TO_DISCARD = 2;

    public DiscardDevCardsToken(CardColorEnum color) {
        this.color = color;
    }

    /**
     * This method implements the action of the Token
     *
     * @param lorenzo   is not used
     * @param turnLogic is the TurnLogic reference
     * @return false
     */
    @Override
    public boolean doAction(Lorenzo lorenzo, TurnLogic turnLogic) {
        DevelopmentCard removedCard;
        DevelopmentCard currentCard;

        for (int i = 0; i < CARDS_TO_DISCARD; i++) {
            removedCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().removeCardByColor(this.color);

            if (!removedCard.getID().equals("empty")) {
                try {
                    currentCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(removedCard.getColor(), removedCard.getLevel());
                } catch (IndexOutOfBoundsException e) {
                    currentCard = removedCard;
                }
            } else
                currentCard = removedCard;

            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
            graphicUpdateEvent.addUpdate(new GridUpdate(currentCard.getColor(), currentCard.getLevel()));
            graphicUpdateEvent.addUpdate(lorenzo.getNickname() + " destroyed one " + this.color + " card");
            turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
        }

        return false;
    }
}