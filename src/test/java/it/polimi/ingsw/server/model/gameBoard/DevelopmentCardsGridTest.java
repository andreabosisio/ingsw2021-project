package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardsGridTest {

    @Test
    void getAvailableCardsTest() {
        GameBoard.getGameBoard().reset();
        List<DevelopmentCard> cards;
        List<DevelopmentCard> cards2;
        cards = GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards();
        cards2 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards();
        assertEquals(12,cards.size());
        assertEquals(cards,cards2);
    }

    @Test
    void hasEmptyColumnTest() {
        GameBoard.getGameBoard().reset();
        List<Player> players = new ArrayList<>();
        players.add(new Player("Bob"));
        TurnLogic turnLogic = new TurnLogic(players,null);
        assertFalse(GameBoard.getGameBoard().getDevelopmentCardsGrid().hasEmptyColumn());
        //check that removing 11 out of 12 cards of the same color leaves no empty column
        for(int i = 0;i<11;i++) {
            assertNotNull(GameBoard.getGameBoard().getDevelopmentCardsGrid().removeCardByColor(CardColorsEnum.BLUE));
            assertFalse(GameBoard.getGameBoard().getDevelopmentCardsGrid().hasEmptyColumn());
        }
        //remove the last card and check for a true empty column
        assertNotEquals("empty", GameBoard.getGameBoard().getDevelopmentCardsGrid().removeCardByColor(CardColorsEnum.BLUE).getID());
        assertTrue(GameBoard.getGameBoard().getDevelopmentCardsGrid().hasEmptyColumn());
        //check that trying to remove a color when all cards of that color have been removed return false
        assertEquals("empty", GameBoard.getGameBoard().getDevelopmentCardsGrid().removeCardByColor(CardColorsEnum.BLUE).getID());
        assertTrue(GameBoard.getGameBoard().getDevelopmentCardsGrid().hasEmptyColumn());
        //check that available cards is now a list of 9 cards (12 - 3 (one for each row of the column removed))
        assertEquals(9,GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards().size());
    }
    @Test
    void hasAllEmptyColumnTest() {
        GameBoard.getGameBoard().reset();
        List<Player> players = new ArrayList<>();
        players.add(new Player("Bob"));
        TurnLogic turnLogic = new TurnLogic(players,null);
        //check that removing all but 1 card for each color leaves no emptyColumn
        for(int i = 0;i<11;i++) {
            for(CardColorsEnum color: EnumSet.allOf(CardColorsEnum.class)) {
                assertNotEquals("empty", GameBoard.getGameBoard().getDevelopmentCardsGrid().removeCardByColor(color).getID());
            }
            assertFalse(GameBoard.getGameBoard().getDevelopmentCardsGrid().hasEmptyColumn());
        }
        //check that there are only 4 cards available (one for each color)
        assertEquals(4,GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards().size());
        //check they are all level 3
        for(DevelopmentCard card : GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards()){
            assertEquals(3,card.getLevel());
        }
        //check that removing all cards doesn't break the grid
        for(CardColorsEnum color: EnumSet.allOf(CardColorsEnum.class)) {
            assertNotEquals("empty", GameBoard.getGameBoard().getDevelopmentCardsGrid().removeCardByColor(color).getID());
            assertTrue(GameBoard.getGameBoard().getDevelopmentCardsGrid().hasEmptyColumn());
        }
        //check that asking for cards with none available returns an empty list
        assertEquals(0,GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards().size());
    }

    @Test
    void getCardByColorAndLevelTest() {
        DevelopmentCardsGrid developmentCardsGrid = new DevelopmentCardsGrid();
        assertEquals(12,developmentCardsGrid.getAvailableCards().size());
        DevelopmentCard developmentCard;
        //remove all card of one color and one level
        for(int i = 0;i<4;i++) {
            developmentCard = developmentCardsGrid.getCardByColorAndLevel(CardColorsEnum.GREEN, 1);
            assertEquals(CardColorsEnum.GREEN, developmentCard.getColor());
            assertEquals(1, developmentCard.getLevel());
            assertTrue(developmentCardsGrid.removeCard(developmentCard));
        }

        assertEquals("empty",  developmentCardsGrid.getCardByColorAndLevel(CardColorsEnum.GREEN,1).getID());
    }
}