package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.gameMode.ICheckWinner;
import it.polimi.ingsw.server.model.gameMode.MultiPlayerCheckWinner;
import it.polimi.ingsw.server.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Various tests for the Faith Track:
 * Critical players progress and check of the correct number of Victory Points.
 */
public class FaithTrackTest {
    List<Player> players = new ArrayList<>();
    ICheckWinner iCheckWinner = new MultiPlayerCheckWinner();
    private final int endOfTheFaithTrack = 24;

    @Test
    void testOnePlayerTile1False() {
        players.add(new Player("Roronoa"));
        GameBoard gameBoard = new GameBoard(players, iCheckWinner);
        assertFalse(gameBoard.faithProgress(players.get(0), 2)); // No tiles are flipped up
        assertEquals(0, gameBoard.getFaithTrackPlayer(players.get(0)).getVictoryPoints());
    }

    @Test
    void testOnePlayerTile1True() {
        players.add(new Player("Roronoa"));
        GameBoard gameBoard = new GameBoard(players, iCheckWinner);
        assertTrue(gameBoard.faithProgress(players.get(0), 8)); // A tile is flipped up
        assertTrue(gameBoard.getFaithTrackPlayer(players.get(0)).isPopeTile1()); // The first tile
        assertEquals(2 + 2, gameBoard.getFaithTrackPlayer(players.get(0)).getVictoryPoints());
    }

    @Test
    void testOnePlayerWithAllThePopeTileFlippedUp() {
        players.add(new Player("Roronoa"));
        GameBoard gameBoard = new GameBoard(players, iCheckWinner);

        assertTrue(gameBoard.faithProgress(players.get(0), 9)); // A tile is flipped up
        assertTrue(gameBoard.faithProgress(players.get(0), 7)); // A tile is flipped up
        assertTrue(gameBoard.faithProgress(players.get(0), 9)); // A tile is flipped up

        assertTrue(gameBoard.getFaithTrackPlayer(players.get(0)).isPopeTile1()); // The first tile
        assertTrue(gameBoard.getFaithTrackPlayer(players.get(0)).isPopeTile2()); // The secondo tile
        assertTrue(gameBoard.getFaithTrackPlayer(players.get(0)).isPopeTile3()); // The third tile
        // Roronoa's progress: 9 + 7 + 9 = 25 --> Without controls, his Faith Marker goes out of the Faith Track
        assertEquals(endOfTheFaithTrack, gameBoard.getFaithTrackPlayer(players.get(0)).getFaithMarker());
        assertEquals(20 + 2 + 3 + 4, gameBoard.getFaithTrackPlayer(players.get(0)).getVictoryPoints());
    }

    @Test
    void testTwoTileTrueOfFourPlayers() {
        players.add(new Player("Roronoa"));
        players.add(new Player("Monkey"));
        players.add(new Player("Nico"));
        players.add(new Player("Vinsmoke"));
        GameBoard gameBoard = new GameBoard(players, iCheckWinner);

        gameBoard.faithProgress(players.get(1), 5);
        gameBoard.faithProgress(players.get(2), 3);
        gameBoard.faithProgress(players.get(3), 0);
        assertTrue(gameBoard.faithProgress(players.get(0), 8)); // A tile is flipped up

        assertTrue(gameBoard.getFaithTrackPlayer(players.get(0)).isPopeTile1()); // The Roronoa's tile is flipped up
        assertTrue(gameBoard.getFaithTrackPlayer(players.get(1)).isPopeTile1()); // The Monkey's tile is flipped up
        assertFalse(gameBoard.getFaithTrackPlayer(players.get(2)).isPopeTile1()); // The Nico's tile is not flipped up
        assertFalse(gameBoard.getFaithTrackPlayer(players.get(3)).isPopeTile1()); // The Vinsmoke's tile is not flipped up

        assertEquals(2 + 2, gameBoard.getFaithTrackPlayer(players.get(0)).getVictoryPoints());
        assertEquals(1 + 2, gameBoard.getFaithTrackPlayer(players.get(1)).getVictoryPoints());
        assertEquals(1, gameBoard.getFaithTrackPlayer(players.get(2)).getVictoryPoints());
        assertEquals(0, gameBoard.getFaithTrackPlayer(players.get(3)).getVictoryPoints());
    }

    @Test
    void testOneTimeFlippedUpTheSameTile() {
        players.add(new Player("Roronoa"));
        players.add(new Player("Monkey"));
        GameBoard gameBoard = new GameBoard(players, iCheckWinner);

        gameBoard.faithProgress(players.get(1), 4);
        assertTrue(gameBoard.faithProgress(players.get(0), 8)); // A tile is flipped up

        assertTrue(gameBoard.getFaithTrackPlayer(players.get(0)).isPopeTile1()); // The Roronoa's tile is flipped up
        assertFalse(gameBoard.getFaithTrackPlayer(players.get(1)).isPopeTile1()); // The Monkey's tile is not flipped up
        // Progress of the Roronoa's Faith Marker that goes in the number eight position:
        assertFalse(gameBoard.faithProgress(players.get(1), 4)); // The same tile is not flipped up
        assertFalse(gameBoard.getFaithTrackPlayer(players.get(1)).isPopeTile1()); // The Monkey's tile is not flipped up

        assertEquals(2 + 2, gameBoard.getFaithTrackPlayer(players.get(0)).getVictoryPoints());
        assertEquals(2, gameBoard.getFaithTrackPlayer(players.get(1)).getVictoryPoints());
    }

    @Test
    void testAllPlayersExceptOneProgress() {
        players.add(new Player("Roronoa"));
        players.add(new Player("Monkey"));
        players.add(new Player("Nico"));
        players.add(new Player("Vinsmoke"));
        GameBoard gameBoard = new GameBoard(players, iCheckWinner);

        gameBoard.faithProgress(players.get(0), 4);
        gameBoard.faithProgress(players.get(1), 3);
        gameBoard.faithProgress(players.get(2), 4);
        gameBoard.faithProgress(players.get(3), 7);
        // Roronoa takes Resources from the Market and discard one Resource that he cannot places in his Warehouse
        assertTrue(gameBoard.faithProgressOfRestOfPlayers(players.get(0), 1)); // A tile is flipped up

        assertFalse(gameBoard.getFaithTrackPlayer(players.get(0)).isPopeTile1()); // The Roronoa's tile is not flipped up
        assertFalse(gameBoard.getFaithTrackPlayer(players.get(1)).isPopeTile1()); // The Monkey's tile is not flipped up
        assertTrue(gameBoard.getFaithTrackPlayer(players.get(2)).isPopeTile1()); // The Nico's tile is flipped up
        assertTrue(gameBoard.getFaithTrackPlayer(players.get(3)).isPopeTile1()); // The Vinsmoke's tile is flipped up

        assertEquals(1, gameBoard.getFaithTrackPlayer(players.get(0)).getVictoryPoints());
        assertEquals(1, gameBoard.getFaithTrackPlayer(players.get(1)).getVictoryPoints());
        assertEquals(1 + 2, gameBoard.getFaithTrackPlayer(players.get(2)).getVictoryPoints());
        assertEquals(2 + 2, gameBoard.getFaithTrackPlayer(players.get(3)).getVictoryPoints());
    }
}