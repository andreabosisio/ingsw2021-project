package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.cards.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckLeaderTest {

    @Test
    void draw() {
        DeckLeader deckLeaderTest = new DeckLeader();
        deckLeaderTest.shuffle();
        List<LeaderCard> leadersDrawn;
        List<LeaderCard> totLeader = new ArrayList<>();
        //check that drawing gives you 4 cards
        for(int i = 0;i<4;i++) {
            leadersDrawn = deckLeaderTest.draw4();
            totLeader.addAll(leadersDrawn);
            assertEquals(4,leadersDrawn.size());
        }
        assertEquals(16,totLeader.size());
        //check that after the last card is drawn an exception is thrown
        assertThrows(IndexOutOfBoundsException.class, deckLeaderTest::draw4);
    }
}