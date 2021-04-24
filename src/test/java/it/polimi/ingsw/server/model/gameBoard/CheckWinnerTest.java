package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameMode.LorenzoDoingNothing;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CheckWinnerTest {

    @Test
    @BeforeEach
    void resetOfTheGameBoard() {
        GameBoard.getGameBoard().reset();
    }

    @Test
    void testBuySeventhDevelopmentCardSinglePlayer() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Mihawk"));
        TurnLogic turnLogic = new TurnLogic(players);
        List<DevelopmentCard> cards = GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards();

        List<DevelopmentCard> cardslvl1 = new ArrayList<>();
        List<DevelopmentCard> cardslvl2 = new ArrayList<>();
        List<DevelopmentCard> cardslvl3 = new ArrayList<>();

        for (DevelopmentCard card : cards) {
            if (card.getLevel() == 1)
                cardslvl1.add(card);
            if (card.getLevel() == 2)
                cardslvl2.add(card);
            if (card.getLevel() == 3)
                cardslvl3.add(card);
        }

        for (int i = 0; i < 3; i++) {
            assertTrue(players.get(0).getPersonalBoard().setNewDevelopmentCard(i + 1, cardslvl1.get(i)));
        }
        for (int i = 0; i < 3; i++) {
            assertTrue(players.get(0).getPersonalBoard().setNewDevelopmentCard(i + 1, cardslvl2.get(i)));
        }
        // The Game is not over
        assertFalse(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        // The player buys his seventh card
        assertTrue(players.get(0).getPersonalBoard().setNewDevelopmentCard(1, cardslvl3.get(0)));
        // The Game is over
        assertTrue(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
    }

    @Test
    void testBuySeventhDevelopmentCardMultiPlayer() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Mihawk"));
        players.add(new Player("Brook"));
        TurnLogic turnLogic = new TurnLogic(players);
        List<DevelopmentCard> cards = GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards();

        List<DevelopmentCard> cardslvl1 = new ArrayList<>();
        List<DevelopmentCard> cardslvl2 = new ArrayList<>();
        List<DevelopmentCard> cardslvl3 = new ArrayList<>();

        for (DevelopmentCard card : cards) {
            if (card.getLevel() == 1)
                cardslvl1.add(card);
            if (card.getLevel() == 2)
                cardslvl2.add(card);
            if (card.getLevel() == 3)
                cardslvl3.add(card);
        }

        for (int i = 0; i < 3; i++) {
            assertTrue(players.get(0).getPersonalBoard().setNewDevelopmentCard(i + 1, cardslvl1.get(i)));
        }
        for (int i = 0; i < 3; i++) {
            assertTrue(players.get(0).getPersonalBoard().setNewDevelopmentCard(i + 1, cardslvl2.get(i)));
        }
        // The Game is not over
        assertFalse(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        // The player buys his seventh card
        assertTrue(players.get(0).getPersonalBoard().setNewDevelopmentCard(1, cardslvl3.get(0)));
        // The Game is over
        assertTrue(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
    }

    @Test
    void testEndOfTheFaithTrack() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        players.add(new Player("Monkey"));
        players.add(new Player("Nico"));
        players.add(new Player("Vinsmoke"));
        TurnLogic turnLogic = new TurnLogic(players);

        assertEquals("Lorenzo il Magnifico doing nothing", turnLogic.getGameMode().getLorenzo().getNickname());
        assertTrue(turnLogic.getGameMode().getLorenzo() instanceof LorenzoDoingNothing);
        assertFalse(turnLogic.getGameMode().getLorenzo().play());

        // The Game is not over
        assertFalse(turnLogic.getGameMode().getICheckWinner().isTheGameOver());

        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(0), 8)); // Roronoa = 8
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(1), 7)); // Monkey = 7
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(2), 4)); // Nico = 4
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(3), 5)); // Vinsmoke = 5

        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(0), 6)); // Roronoa = 14
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(1), 9)); // Monkey = 16
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(2), 9)); // Nico = 13
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(3), 7)); // Vinsmoke = 12

        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(0), 7)); // Roronoa = 21
        // The Game is not over
        assertFalse(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(1), 10)); // Monkey = 24
        // The Game is over
        assertTrue(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(2), 5)); // Nico = 18
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(3), 7)); // Vinsmoke = 19

        assertEquals(21, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getFaithMarker());
        assertEquals(24, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).getFaithMarker());
        assertEquals(18, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(2)).getFaithMarker());
        assertEquals(19, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(3)).getFaithMarker());

        assertEquals(16 + 9, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
        assertEquals(20 + 7, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).getVictoryPoints());
        assertEquals(12, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(2)).getVictoryPoints());
        assertEquals(12, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(3)).getVictoryPoints());

        // The Winner is Monkey
        assertEquals(players.get(1), turnLogic.getGameMode().getICheckWinner().getWinner());
        // The others are not the Winner
        assertNotSame(players.get(2), turnLogic.getGameMode().getICheckWinner().getWinner());
        assertNotSame(players.get(3), turnLogic.getGameMode().getICheckWinner().getWinner());
        assertNotSame(players.get(0), turnLogic.getGameMode().getICheckWinner().getWinner());
    }

    @Test
    void testEndOfTheFaithTrackWinnerResources() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        players.add(new Player("Monkey"));
        TurnLogic turnLogic = new TurnLogic(players);

        // The Game is not over
        assertFalse(turnLogic.getGameMode().getICheckWinner().isTheGameOver());

        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(0), 7)); // Roronoa = 7
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(1), 8)); // Monkey = 8

        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(0), 8)); // Roronoa = 15
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(1), 8)); // Monkey = 16

        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(0), 8)); // Roronoa = 23
        // The Game is not over
        assertFalse(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(1), 8)); // Monkey = 24
        // The Game is over
        assertTrue(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(0), 1)); // Roronoa = 24

        assertEquals(24, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getFaithMarker());
        assertEquals(24, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).getFaithMarker());

        assertEquals(20 + 9, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
        assertEquals(20 + 9, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).getVictoryPoints());

        players.get(0).getPersonalBoard().getWarehouse().addResourcesFromMarket(new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.BLUE));
            add(new StorableResource(ResourceEnum.PURPLE));
        }});
        players.get(0).getPersonalBoard().getWarehouse().swap(0,4);
        players.get(0).getPersonalBoard().getWarehouse().swap(1,6);
        players.get(0).getPersonalBoard().getWarehouse().swap(2,8);

        players.get(1).getPersonalBoard().getWarehouse().addResourcesFromMarket(new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.BLUE));
        }});
        players.get(1).getPersonalBoard().getWarehouse().swap(0,4);
        players.get(1).getPersonalBoard().getWarehouse().swap(1,6);
        players.get(1).getPersonalBoard().getWarehouse().swap(2,14);

        // The Winner is Roronoa because he has more resources
        assertEquals(players.get(0), turnLogic.getGameMode().getICheckWinner().getWinner());
        // The Winner is not Monkey
        assertNotSame(players.get(1), turnLogic.getGameMode().getICheckWinner().getWinner());
    }
}