package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.gameMode.*;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LorenzoTest {

    @Test
    void testCorrectSetOfTheFaithTracks() {
        GameBoard.getGameBoard().reset();
        List<Player> players = new ArrayList<>();
        players.add(new Player("Roronoa"));
        TurnLogic turnLogic = new TurnLogic(players);

        assertEquals(2, GameBoard.getGameBoard().getFaithTracks().size());
        assertEquals(1, GameBoard.getGameBoard().getFaithTracks().stream().
                filter(faithTrack -> faithTrack.getOwner() instanceof Lorenzo).count());
        assertEquals(1, GameBoard.getGameBoard().getFaithTracks().stream().
                filter(faithTrack -> faithTrack.getOwner() instanceof Player).count());
    }

    @Test
    void testProgress1BlackCrossToken() {
        GameBoard.getGameBoard().reset();
        List<Player> players = new ArrayList<>();
        players.add(new Player("Trafalgar"));
        TurnLogic turnLogic = new TurnLogic(players);
        SoloActionToken soloActionToken = new SingleFaithTrackProgressToken();
        GameBoard.getGameBoard().faithProgress(players.get(0), 1);
        // Lorenzo's Black Cross reaches the eight position of the FaithTrack
        //for (int i = 0; i < 8; i++)
        //    soloActionToken.doAction(GameBoard.getGameBoard().getGameMode().getLorenzo());

       // assertEquals(1, GameBoard.getGameBoard().getFaithTrackPlayer(GameBoard.getGameMode().getLorenzo()).getFaithMarker());
    }

    @Test
    void testProgress2BlackCrossToken() {
        GameBoard.getGameBoard().reset();
        List<Player> players = new ArrayList<>();
        players.add(new Player("Trafalgar"));
        TurnLogic turnLogic = new TurnLogic(players);

    }

    @Test
    void testDiscardDevCardsToken() {
        GameBoard.getGameBoard().reset();
        List<Player> players = new ArrayList<>();
        players.add(new Player("Trafalgar"));
        TurnLogic turnLogic = new TurnLogic(players);

    }
}