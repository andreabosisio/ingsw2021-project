package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.gameMode.*;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LorenzoTest {

    @Test
    @BeforeEach
    void resetOfTheGameBoard() {
        GameBoard.getGameBoard().reset();
    }

    @Test
    void testCorrectSetOfTheFaithTracks() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        TurnLogic turnLogic = new TurnLogic(players,null);

        assertEquals("Lorenzo il Magnifico", turnLogic.getGameMode().getLorenzo().getNickname());
        assertTrue(turnLogic.getGameMode().getLorenzo() instanceof LorenzoAI);
        assertEquals(2, GameBoard.getGameBoard().getFaithTracks().size());
        assertEquals(1, GameBoard.getGameBoard().getFaithTracks().stream().
                filter(faithTrack -> faithTrack.getOwner() instanceof Lorenzo).count());
        assertEquals(1, GameBoard.getGameBoard().getFaithTracks().stream().
                filter(faithTrack -> faithTrack.getOwner() instanceof Player).count());
    }

    @Test
    void testProgress1BlackCrossToken() {
        List<String> playersNickname = new ArrayList<>();
        playersNickname.add("Trafalgar");
        ModelInterface modelInterface = new ModelInterface(playersNickname);
        TurnLogic turnLogic = modelInterface.getTurnLogic();
        List<Player> players = turnLogic.getPlayers();
        SoloActionToken soloActionToken = new SingleFaithTrackProgressToken();

        GameBoard.getGameBoard().faithProgress(players.get(0), 4); // TrafalgarProgress = 4
        // Lorenzo's Black Cross reaches the eight position of the FaithTrack
        for (int i = 0; i < 8; i++)
            assertTrue(soloActionToken.doAction(turnLogic.getGameMode().getLorenzo(), turnLogic)); // LorenzoProgress = 8
        assertEquals(8, GameBoard.getGameBoard().getFaithTrackOfPlayer(turnLogic.getGameMode().getLorenzo()).getFaithMarker());
        assertEquals(4, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getFaithMarker());

        GameBoard.getGameBoard().faithProgress(players.get(0), 11); // TrafalgarProgress = 15
        for (int i = 0; i < 7; i++)
            assertTrue(soloActionToken.doAction(turnLogic.getGameMode().getLorenzo(), turnLogic)); // LorenzoProgress = 15
        assertEquals(15, GameBoard.getGameBoard().getFaithTrackOfPlayer(turnLogic.getGameMode().getLorenzo()).getFaithMarker());
        assertEquals(15, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getFaithMarker());

        //change to the no shuffle one which will return false
        soloActionToken = new SingleFaithTrackProgressTokenNoShuffle();
        assertFalse(soloActionToken.doAction(turnLogic.getGameMode().getLorenzo(), turnLogic)); // LorenzoProgress = 16
        // Trafalgar flip the second card
        assertEquals(12, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());

        GameBoard.getGameBoard().faithProgress(players.get(0), 9); // TrafalgarProgress = 24
        // The Game is over, Trafalgar reaches the last position
        assertTrue(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        // Pandaman is the winner with 27 points
        assertEquals(players.get(0), turnLogic.getGameMode().getICheckWinner().getWinner());
        assertEquals(27, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
    }

    @Test
    void testProgress2BlackCrossToken() {
        List<String> playersNickname = new ArrayList<>();
        playersNickname.add("Pandaman");
        ModelInterface modelInterface = new ModelInterface(playersNickname);
        TurnLogic turnLogic = modelInterface.getTurnLogic();
        List<Player> players = turnLogic.getPlayers();
        SoloActionToken soloActionToken = new DoubleFaithTrackProgressToken();

        GameBoard.getGameBoard().faithProgress(players.get(0), 5); // PandamanProgress = 5
        // Lorenzo's Black Cross reaches the eight position of the FaithTrack
        for (int i = 0; i < 4; i++)
            assertFalse(soloActionToken.doAction(turnLogic.getGameMode().getLorenzo(), turnLogic)); // LorenzoProgress = 8
        assertEquals(8, GameBoard.getGameBoard().getFaithTrackOfPlayer(turnLogic.getGameMode().getLorenzo()).getFaithMarker());
        // Pandaman flip the first card
        assertEquals(3, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());

        for (int i = 0; i < 7; i++)
            assertFalse(soloActionToken.doAction(turnLogic.getGameMode().getLorenzo(), turnLogic)); // LorenzoProgress = 22
        assertEquals(22, GameBoard.getGameBoard().getFaithTrackOfPlayer(turnLogic.getGameMode().getLorenzo()).getFaithMarker());
        GameBoard.getGameBoard().faithProgress(players.get(0), 16); // PandamanProgress = 21
        assertEquals(21, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getFaithMarker());
        // The Game is not over
        assertFalse(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        assertFalse(soloActionToken.doAction(turnLogic.getGameMode().getLorenzo(), turnLogic)); // LorenzoProgress = 24
        // Pandaman flip the third card
        assertEquals(22, GameBoard.getGameBoard().getFaithTrackOfPlayer(players.get(0)).getVictoryPoints());
        // The Game is over, Lorenzo reaches the last position
        assertTrue(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        // Lorenzo is the winner
        assertEquals(turnLogic.getGameMode().getLorenzo(), turnLogic.getGameMode().getICheckWinner().getWinner());
    }

    @Test
    void testDiscardDevCardsToken() {
        List<String> playersNickname = new ArrayList<>();
        playersNickname.add("Mihawk");
        ModelInterface modelInterface = new ModelInterface(playersNickname);
        TurnLogic turnLogic = modelInterface.getTurnLogic();
        SoloActionToken soloActionToken = new DiscardDevCardsToken(CardColorsEnum.PURPLE);

        for (int i = 0; i < 5; i++) {
            assertFalse(soloActionToken.doAction(turnLogic.getGameMode().getLorenzo(), turnLogic));
            assertFalse(GameBoard.getGameBoard().getDevelopmentCardsGrid().hasEmptyColumn());
        }
        assertFalse(soloActionToken.doAction(turnLogic.getGameMode().getLorenzo(), turnLogic)); // Discard the last two Cards
        assertTrue(GameBoard.getGameBoard().getDevelopmentCardsGrid().hasEmptyColumn()); // The column is empty
        assertEquals(9, GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards().size());
        // The Game is over, a column is empty
        assertTrue(turnLogic.getGameMode().getICheckWinner().isTheGameOver());
        // Lorenzo is the winner
        assertEquals(turnLogic.getGameMode().getLorenzo(), turnLogic.getGameMode().getICheckWinner().getWinner());
    }

    @Test
    void testLorenzoPlay() {
        List<String> playersNickname = new ArrayList<>();
        playersNickname.add("Ace");
        ModelInterface modelInterface = new ModelInterface(playersNickname);
        TurnLogic turnLogic = modelInterface.getTurnLogic();
        List<Player> players = turnLogic.getPlayers();
        Lorenzo lorenzo = turnLogic.getGameMode().getLorenzo();
        int FaithProgress = 0;

        for (int i = 0; i < 11; i++) {
            if (lorenzo.extractToken() instanceof SingleFaithTrackProgressToken) {
                FaithProgress++;
                lorenzo.play(turnLogic);
            } else if (turnLogic.getGameMode().getLorenzo().extractToken() instanceof DoubleFaithTrackProgressToken) {
                FaithProgress = FaithProgress + 2;
                lorenzo.play(turnLogic);
            } else if (lorenzo.extractToken() instanceof DiscardDevCardsToken) {
                lorenzo.play(turnLogic);
                assertFalse(GameBoard.getGameBoard().getDevelopmentCardsGrid().hasEmptyColumn());
            }
        }
        assertEquals(FaithProgress, GameBoard.getGameBoard().getFaithTrackOfPlayer(lorenzo).getFaithMarker());
    }

    @Test
    void lorenzoDoingNothingTest() {
        TurnLogic turnLogic = new TurnLogic(new ArrayList<>() {{
            add(new Player("player1"));
            add(new Player("player2"));
        }},null);
        Lorenzo lorenzo = turnLogic.getGameMode().getLorenzo();

        //lorenzo isn't playing
        assertNull(lorenzo.extractToken());
    }
}