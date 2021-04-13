package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.cards.CardsGenerator;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.cards.ProductionLeaderCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.OtherResource;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;

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

    @Test
    void getAvailableLeaderActivationTest() throws NonStorableResourceException {
        List<LeaderCard> leaders = new CardsGenerator().generateLeaderCards();
        LeaderCard leaderWithResRequirement = leaders.get(11);//req 5 purple
        LeaderCard leaderWithDevRequirement = leaders.get(0);// req lv2 green
        leaders.clear();
        leaders.add(leaderWithDevRequirement);
        leaders.add(leaderWithResRequirement);
        Player player = new Player("Bunny");
        player.setLeaderHand(leaders);
        //check that both can't be activated
        assertEquals(0,player.getAvailableLeaderActivation().size());
        for(int i =0;i<5;i++) {
            assertTrue(player.getPersonalBoard().getWarehouse().addResourceToStrongBox(new OtherResource(ResourceEnum.PURPLE)));
        }
        //check that res requirements have been met
        assertEquals(1,player.getAvailableLeaderActivation().size());
        assertTrue(player.getPersonalBoard().setNewDevCard(1, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList()).get(0)));
        assertTrue(player.getPersonalBoard().setNewDevCard(1, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==2).filter(c->c.getColor()== CardColorEnum.GREEN).collect(Collectors.toList()).get(0)));
        //check that dev requirements have been met
        assertEquals(2,player.getAvailableLeaderActivation().size());
        //check that both requirements remain met even with more stuff added
        assertTrue(player.getPersonalBoard().setNewDevCard(2, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList()).get(0)));
        assertTrue(player.getPersonalBoard().getWarehouse().addResourceToStrongBox(new OtherResource(ResourceEnum.GRAY)));
        assertTrue(player.getPersonalBoard().getWarehouse().addResourceToStrongBox(new OtherResource(ResourceEnum.BLUE)));
        assertEquals(2,player.getAvailableLeaderActivation().size());
        assertEquals(2,player.getAvailableLeaderActivation().size());
        //check that number of leader that can be activated is reduced when one is activated
        assertTrue(player.setActivateLeader(leaderWithDevRequirement));
        assertEquals(1,player.getAvailableLeaderActivation().size());//only resReq leader remain
        assertTrue(player.setActivateLeader(leaderWithResRequirement));
        assertEquals(0,player.getAvailableLeaderActivation().size());//no leader remain to activate


    }
}