package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Various tests for the Faith Track:
 * Critical players progress and check of the correct number of Victory Points.
 */
public class FaithTrackTest {
    private final int endOfTheFaithTrack = 24;

    @Test
    @BeforeEach
    void resetOfTheGameBoard() {
        GameBoard.getGameBoard().reset();
    }

    @Test
    void testOnePlayerTile1False() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        TurnLogic turnLogic = new TurnLogic(players,null);
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(0), 2)); // No tiles are flipped up
        assertEquals(2, GameBoard.getGameBoard().getFaithTracks().size());
        assertEquals(0, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
    }

    @Test
    void testOnePlayerTile1True() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        TurnLogic turnLogic = new TurnLogic(players,null);
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(0), 8)); // A tile is flipped up
        assertTrue(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).hasAchievedFirstReport()); // The first tile
        assertEquals(2, GameBoard.getGameBoard().getFaithTracks().size());
        assertEquals(2 + 2, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
    }

    @Test
    void testOnePlayerWithAllThePopeTileFlippedUp() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        TurnLogic turnLogic = new TurnLogic(players,null);

        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(0), 9)); // A tile is flipped up
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(0), 7)); // A tile is flipped up
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(0), 9)); // A tile is flipped up

        assertTrue(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).hasAchievedFirstReport()); // The first tile
        assertTrue(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).hasAchievedSecondReport()); // The secondo tile
        assertTrue(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).hasAchievedThirdReport()); // The third tile
        // Roronoa's progress: 9 + 7 + 9 = 25 --> Without controls, his Faith Marker goes out of the Faith Track
        assertEquals(endOfTheFaithTrack, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getFaithMarker());
        assertEquals(2, GameBoard.getGameBoard().getFaithTracks().size());
        assertEquals(20 + 2 + 3 + 4, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
    }

    @Test
    void testTwoTileTrueOfFourPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        players.add(new Player("Monkey"));
        players.add(new Player("Nico"));
        players.add(new Player("Vinsmoke"));
        TurnLogic turnLogic = new TurnLogic(players,null);

        GameBoard.getGameBoard().faithProgress(players.get(1), 5);
        GameBoard.getGameBoard().faithProgress(players.get(2), 3);
        GameBoard.getGameBoard().faithProgress(players.get(3), 0);
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(0), 8)); // A tile is flipped up

        assertTrue(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).hasAchievedFirstReport()); // The Roronoa's tile is flipped up
        assertTrue(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).hasAchievedFirstReport()); // The Monkey's tile is flipped up
        assertFalse(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(2)).hasAchievedFirstReport()); // The Nico's tile is not flipped up
        assertFalse(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(3)).hasAchievedFirstReport()); // The Vinsmoke's tile is not flipped up

        assertEquals(4, GameBoard.getGameBoard().getFaithTracks().size());

        assertEquals(2 + 2, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
        assertEquals(1 + 2, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).getVictoryPoints());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(2)).getVictoryPoints());
        assertEquals(0, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(3)).getVictoryPoints());
    }

    @Test
    void testOneTimeFlippedUpTheSameTile() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        players.add(new Player("Monkey"));
        TurnLogic turnLogic = new TurnLogic(players,null);

        GameBoard.getGameBoard().faithProgress(players.get(1), 4);
        assertTrue(GameBoard.getGameBoard().faithProgress(players.get(0), 8)); // A tile is flipped up

        assertTrue(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).hasAchievedFirstReport()); // The Roronoa's tile is flipped up
        assertFalse(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).hasAchievedFirstReport()); // The Monkey's tile is not flipped up
        // Progress of the Roronoa's Faith Marker that goes in the number eight position:
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(1), 4)); // The same tile is not flipped up
        assertFalse(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).hasAchievedFirstReport()); // The Monkey's tile is not flipped up

        assertEquals(2, GameBoard.getGameBoard().getFaithTracks().size());

        assertEquals(2 + 2, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
        assertEquals(2, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).getVictoryPoints());
    }

    @Test
    void testAllPlayersExceptOneProgress() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        players.add(new Player("Monkey"));
        players.add(new Player("Nico"));
        players.add(new Player("Vinsmoke"));
        TurnLogic turnLogic = new TurnLogic(players,null);

        GameBoard.getGameBoard().faithProgress(players.get(0), 4);
        GameBoard.getGameBoard().faithProgress(players.get(1), 3);
        GameBoard.getGameBoard().faithProgress(players.get(2), 4);
        GameBoard.getGameBoard().faithProgress(players.get(3), 7);
        // Roronoa takes Resources from the Market and discard one Resource that he cannot places in his Warehouse
        assertTrue(GameBoard.getGameBoard().faithProgressForOtherPlayers(players.get(0), 1)); // A tile is flipped up

        assertFalse(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).hasAchievedFirstReport()); // The Roronoa's tile is not flipped up
        assertFalse(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).hasAchievedFirstReport()); // The Monkey's tile is not flipped up
        assertTrue(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(2)).hasAchievedFirstReport()); // The Nico's tile is flipped up
        assertTrue(GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(3)).hasAchievedFirstReport()); // The Vinsmoke's tile is flipped up

        assertEquals(4, GameBoard.getGameBoard().getFaithTracks().size());

        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(1)).getVictoryPoints());
        assertEquals(1 + 2, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(2)).getVictoryPoints());
        assertEquals(2 + 2, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(3)).getVictoryPoints());
    }
}