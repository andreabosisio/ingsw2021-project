package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.events.send.graphics.GridUpdate;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import it.polimi.ingsw.server.utils.ServerParser;


/**
 * Class that represents the token that discards two Development Cards
 * of the color indicated.
 */
public class DiscardDevCardsToken extends SoloActionToken {
    private final CardColorsEnum color;
    private final int CARDS_TO_DISCARD = 2;

    /**
     * Create a new DiscardDevCardsToken by specifying the Development Card Color to discard.
     *
     * @param color The Development Card Color to discard
     */
    public DiscardDevCardsToken(CardColorsEnum color) {
        super(ServerParser.DISCARD_CARD_TOKEN_TYPE);
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

            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();

            if (!removedCard.isTheEmptyCard()) {
                graphicUpdateEvent.addUpdate(lorenzo.getNickname() + " destroyed one " + this.color + " card of level " + removedCard.getLevel());

                currentCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(removedCard.getColor(), removedCard.getLevel());

                if (currentCard.isTheEmptyCard()) {
                    currentCard = removedCard;
                }

            } else {
                graphicUpdateEvent.addUpdate(lorenzo.getNickname() + " destroyed all " + this.color + " card");
                currentCard = removedCard;
            }

            graphicUpdateEvent.addUpdate(new GridUpdate(this.color, currentCard.getLevel()));

            turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
        }

        return false;
    }
}