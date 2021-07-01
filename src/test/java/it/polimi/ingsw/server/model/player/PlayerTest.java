package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.cards.CardsGenerator;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getNickNameTest() {
        String name = "Atalanta";
        Player player = new Player(name);
        assertEquals(name,player.getNickname());
    }

    @Test
    void setLeaderHandTest() {
        Player player = new Player("Gino");
        CardsGenerator generator = new CardsGenerator();
        List<LeaderCard> leaders = generator.generateLeaderCards();
        assertTrue(player.setLeaderHand(leaders.subList(0,2)));
        //check that leaderHand is correct
        assertEquals(leaders.subList(0,2),player.getLeaderHand());
        //check that you can't set more than 2 leaders
        assertFalse(player.setLeaderHand(leaders.subList(0,3)));
        //check that you can't set less than 2 leaders
        assertFalse(player.setLeaderHand(leaders.subList(0,1)));
    }

    @Test
    void setActivateLeaderTest() {
        Player player = new Player("Gino");
        CardsGenerator generator = new CardsGenerator();
        List<LeaderCard> leaders = generator.generateLeaderCards();
        LeaderCard leader1 = leaders.get(0);
        LeaderCard leader2 = leaders.get(1);
        assertTrue(player.setLeaderHand(leaders.subList(0,2)));
        assertTrue(player.setActivateLeaderTest(leader1));
        //check that you can't activate the same leader 2 times
        assertFalse(player.setActivateLeaderTest(leader1));
        //check that in hand remains only the second leader
        assertEquals(1,player.getLeaderHand().size());
        assertEquals(leader2,player.getLeaderHand().get(0));
        //check that the first leader is active
        assertEquals(leader1,player.getPersonalBoard().getActiveLeaderCards().get(0));
        //activate second leader and various checks
        assertTrue(player.setActivateLeaderTest(leader2));
        assertEquals(0,player.getLeaderHand().size());
        assertEquals(2,player.getPersonalBoard().getActiveLeaderCards().size());
        assertEquals(leader1,player.getPersonalBoard().getActiveLeaderCards().get(0));
        assertEquals(leader2,player.getPersonalBoard().getActiveLeaderCards().get(1));
    }

    @Test
    void getAvailableLeaderActivationTest() {
        List<LeaderCard> leaders = new CardsGenerator().generateLeaderCards();
        LeaderCard leaderWithResRequirement = leaders.get(11);//req 5 purple
        LeaderCard leaderWithDevRequirement = leaders.get(0);// req lv2 green
        leaders.clear();
        leaders.add(leaderWithDevRequirement);
        leaders.add(leaderWithResRequirement);
        Player player = new Player("Bunny");
        player.setLeaderHand(leaders);
        //check that both can't be activated
        assertEquals(0,player.getAvailableLeaderActivationTest().size());
        assertFalse(player.activateLeaderCard(leaderWithDevRequirement));
        assertFalse(player.activateLeaderCard(leaderWithResRequirement));
        //setup requirements
        for(int i =0;i<5;i++) {
            assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.PURPLE)));
        }
        //check that res requirements have been met
        assertEquals(1,player.getAvailableLeaderActivationTest().size());
        assertTrue(player.getPersonalBoard().setNewProductionCard(1, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList()).get(0)));
        assertTrue(player.getPersonalBoard().setNewProductionCard(1, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==2).filter(c->c.getColor()== CardColorsEnum.GREEN).collect(Collectors.toList()).get(0)));
        //check that dev requirements have been met
        assertEquals(2,player.getAvailableLeaderActivationTest().size());
        //check that both requirements remain met even with more stuff added
        assertTrue(player.getPersonalBoard().setNewProductionCard(2, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList()).get(0)));
        assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.GRAY)));
        assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.BLUE)));
        assertEquals(2,player.getAvailableLeaderActivationTest().size());
        assertEquals(2,player.getAvailableLeaderActivationTest().size());
        //check that number of leader that can be activated is reduced when one is activated
        assertTrue(player.activateLeaderCard(leaderWithDevRequirement));
        assertEquals(1,player.getAvailableLeaderActivationTest().size());//only resReq leader remain
        assertTrue(player.activateLeaderCard(leaderWithResRequirement));
        assertEquals(0,player.getAvailableLeaderActivationTest().size());//no leader remain to activate


    }

    @Test
    void discardLeaderTest() {
        List<LeaderCard> leaders = new CardsGenerator().generateLeaderCards();
        LeaderCard leader1 = leaders.get(0);//req 5 purple
        LeaderCard leader2 = leaders.get(1);// req lv2 green
        List<Player> players = new ArrayList<>();
        Player player = new Player("Bunny");
        players.add(player);
        TurnLogic turnLogic = new TurnLogic(players,null);//setup every faithTrack and gameBoard
        //give player 1 faith to put him at 3 total with 2 leaderCard discarded (no popeTile flipped)
        assertFalse(GameBoard.getGameBoard().faithProgress(players.get(0), 1));
        leaders.clear();
        leaders.add(leader1);
        leaders.add(leader2);
        player.setLeaderHand(leaders);
        //check that player start with 0 points
        assertEquals(0,player.getPersonalBoard().getPoints(player));
        //check that same leader can't be discarded 2 times
        assertTrue(player.discardLeader(leader1));
        assertFalse(player.discardLeader(leader1));
        //check that in hand remains only one leader and it's the non discarded one
        assertEquals(1,player.getLeaderHand().size());
        assertEquals(leader2,player.getLeaderHand().get(0));
        assertEquals(0,player.getPersonalBoard().getPoints(player));
        assertTrue(player.discardLeader(leader2));
        //check that the player has now reached the third tile in his faith track(2 from leaders +1 given at the start)
        assertEquals(1,player.getPersonalBoard().getPoints(player));
    }
}