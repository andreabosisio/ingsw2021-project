package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.gameMode.MultiPlayerCheckWinner;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardsGridTest {
    EndGameObserver iCheckWinner = new MultiPlayerCheckWinner();

    @Test
    void getAvailableCardsTest() {
        DevelopmentCardsGrid.getDevelopmentCardsGrid().reset();
        List<DevelopmentCard> cards;
        List<DevelopmentCard> cards2;
        cards = DevelopmentCardsGrid.getDevelopmentCardsGrid().getAvailableCards();
        cards2 = DevelopmentCardsGrid.getDevelopmentCardsGrid().getAvailableCards();
        assertEquals(12,cards.size());
        assertEquals(cards,cards2);
    }

    @Test
    void hasEmptyColumnTest() {
        DevelopmentCardsGrid.getDevelopmentCardsGrid().reset();
        DevelopmentCardsGrid.getDevelopmentCardsGrid().registerEndGameObserver(iCheckWinner);
        assertFalse(DevelopmentCardsGrid.getDevelopmentCardsGrid().hasEmptyColumn());
        //check that removing 11 out of 12 cards of the same color leaves no empty column
        for(int i = 0;i<11;i++) {
            assertTrue(DevelopmentCardsGrid.getDevelopmentCardsGrid().removeCardByColor(CardColorEnum.BLUE));
            assertFalse(DevelopmentCardsGrid.getDevelopmentCardsGrid().hasEmptyColumn());
        }
        //remove the last card and check for a true empty column
        assertTrue(DevelopmentCardsGrid.getDevelopmentCardsGrid().removeCardByColor(CardColorEnum.BLUE));
        assertTrue(DevelopmentCardsGrid.getDevelopmentCardsGrid().hasEmptyColumn());
        //check that trying to remove a color when all cards of that color have been removed return false
        assertFalse(DevelopmentCardsGrid.getDevelopmentCardsGrid().removeCardByColor(CardColorEnum.BLUE));
        assertTrue(DevelopmentCardsGrid.getDevelopmentCardsGrid().hasEmptyColumn());
        //check that available cards is now a list of 9 cards (12 - 3 (one for each row of the column removed))
        assertEquals(9,DevelopmentCardsGrid.getDevelopmentCardsGrid().getAvailableCards().size());
    }
    @Test
    void hasAllEmptyColumnTest() {
        DevelopmentCardsGrid.getDevelopmentCardsGrid().reset();
        //check that removing all but 1 card for each color leaves no emptyColumn
        for(int i = 0;i<11;i++) {
            for(CardColorEnum color: EnumSet.allOf(CardColorEnum.class)) {
                assertTrue(DevelopmentCardsGrid.getDevelopmentCardsGrid().removeCardByColor(color));
            }
            assertFalse(DevelopmentCardsGrid.getDevelopmentCardsGrid().hasEmptyColumn());
        }
        //check that there are only 4 cards available (one for each color)
        assertEquals(4,DevelopmentCardsGrid.getDevelopmentCardsGrid().getAvailableCards().size());
        //check they are all level 3
        for(DevelopmentCard card : DevelopmentCardsGrid.getDevelopmentCardsGrid().getAvailableCards()){
            assertEquals(3,card.getLevel());
        }
        //check that removing all cards doesn't break the grid
        for(CardColorEnum color: EnumSet.allOf(CardColorEnum.class)) {
            assertTrue(DevelopmentCardsGrid.getDevelopmentCardsGrid().removeCardByColor(color));
            assertTrue(DevelopmentCardsGrid.getDevelopmentCardsGrid().hasEmptyColumn());
        }
        //check that asking for cards with none available returns an empty list
        assertEquals(0,DevelopmentCardsGrid.getDevelopmentCardsGrid().getAvailableCards().size());

    }
}