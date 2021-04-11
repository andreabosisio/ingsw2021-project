package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.cards.CardsGenerator;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getNickNameTest() {
        String name = "Atalanta";
        Player player = new Player(name);
        assertEquals(name,player.getNickName());
    }

    @Test
    void setLeaderHandTest() {
        Player player = new Player("Gino");
        CardsGenerator generator = new CardsGenerator();
        List<LeaderCard> leaders = generator.generateLeaderCards();
        assertTrue(player.setLeaderHand(leaders.subList(0,2)));
        //check that leaderHand is correct
        assertEquals(leaders.subList(0,2),player.getLeaderHand());
    }

    @Test
    void setActivateLeaderTest() {
        Player player = new Player("Gino");
        CardsGenerator generator = new CardsGenerator();
        List<LeaderCard> leaders = generator.generateLeaderCards();
        LeaderCard leader1 = leaders.get(0);
        LeaderCard leader2 = leaders.get(1);
        assertTrue(player.setLeaderHand(leaders.subList(0,2)));
        assertTrue(player.setActivateLeader(leader1));
        //check that you can't activate the same leader 2 times
        assertFalse(player.setActivateLeader(leader1));
        //check that in hand remains only the second leader
        assertEquals(1,player.getLeaderHand().size());
        assertEquals(leader2,player.getLeaderHand().get(0));
        //check that the first leader is active
        assertEquals(leader1,player.getPersonalBoard().getActiveLeaderCards().get(0));
        //activate second leader and various checks
        assertTrue(player.setActivateLeader(leader2));
        assertEquals(0,player.getLeaderHand().size());
        assertEquals(2,player.getPersonalBoard().getActiveLeaderCards().size());
        assertEquals(leader1,player.getPersonalBoard().getActiveLeaderCards().get(0));
        assertEquals(leader2,player.getPersonalBoard().getActiveLeaderCards().get(1));
    }
}