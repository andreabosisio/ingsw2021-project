package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.WhiteResource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LeaderCardTest {
    final List<LeaderCard> leaders = new CardsGenerator().generateLeaderCards();
    @Test
    void canBeActivatedTest() {
        LeaderCard leaderWithResRequirement = leaders.get(11);//req 5 purple
        LeaderCard leaderWithDevRequirement = leaders.get(0);// req lv2 green
        leaders.clear();
        leaders.add(leaderWithDevRequirement);
        leaders.add(leaderWithResRequirement);
        Player player = new Player("Bunny");
        player.setLeaderHand(leaders);
        //check that both can't be activated
        assertFalse(leaderWithResRequirement.canBeActivatedBy(player));
        assertFalse(leaderWithDevRequirement.canBeActivatedBy(player));
        //prepare requirements for first leader
        for(int i =0;i<5;i++) {
            assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.PURPLE)));
        }
        //check that res requirements have been met but not dev requirements
        assertTrue(leaderWithResRequirement.canBeActivatedBy(player));
        assertFalse(leaderWithDevRequirement.canBeActivatedBy(player));
        //prepare req for second leader
        assertTrue(player.getPersonalBoard().setNewProductionCard(1, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList()).get(0)));
        assertTrue(player.getPersonalBoard().setNewProductionCard(1, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==2).filter(c->c.getColor()== CardColorsEnum.GREEN).collect(Collectors.toList()).get(0)));
        //check that dev requirements have been met and res requirements remain met
        assertTrue(leaderWithDevRequirement.canBeActivatedBy(player));
        assertTrue(leaderWithResRequirement.canBeActivatedBy(player));
        //check that both requirements remain met even with more stuff added
        assertTrue(player.getPersonalBoard().setNewProductionCard(2, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList()).get(0)));
        assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.GRAY)));
        assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.BLUE)));
        assertTrue(leaderWithDevRequirement.canBeActivatedBy(player));
        assertTrue(leaderWithResRequirement.canBeActivatedBy(player));
    }

    @Test
    void activateAllTypesOfLeaderCardTest() {
        Player player1 = new Player("Aldo");
        Player player2 = new Player("Giovanni");
        List<LeaderCard> leaderHand1 = new ArrayList<>();
        leaderHand1.add(leaders.get(0));//req lev 2 Green
        leaderHand1.add(leaders.get(5));//req 1Green&1Purple
        List<LeaderCard> leaderHand2 = new ArrayList<>();
        leaderHand2.add(leaders.get(9));//req 5 blue res
        leaderHand2.add(leaders.get(13));//req 1Blue&1Purple
        player1.setLeaderHand(leaderHand1);
        player2.setLeaderHand(leaderHand2);
        //check that no card can be activated
        assertFalse(player1.getLeaderHand().get(0).canBeActivatedBy(player1));
        assertFalse(player1.getLeaderHand().get(1).canBeActivatedBy(player1));
        assertFalse(player2.getLeaderHand().get(0).canBeActivatedBy(player1));
        assertFalse(player2.getLeaderHand().get(1).canBeActivatedBy(player1));
        //prepare all requirements
        assertTrue(player1.getPersonalBoard().setNewProductionCard(1,new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).filter(c->c.getColor()== CardColorsEnum.GREEN).collect(Collectors.toList()).get(0)));
        assertTrue(player1.getPersonalBoard().setNewProductionCard(2,new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).filter(c->c.getColor()== CardColorsEnum.PURPLE).collect(Collectors.toList()).get(0)));
        assertTrue(player1.getPersonalBoard().setNewProductionCard(1,new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==2).filter(c->c.getColor()== CardColorsEnum.GREEN).collect(Collectors.toList()).get(0)));
        assertTrue(player2.getPersonalBoard().setNewProductionCard(1,new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).filter(c->c.getColor()== CardColorsEnum.BLUE).collect(Collectors.toList()).get(0)));
        assertTrue(player2.getPersonalBoard().setNewProductionCard(2,new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).filter(c->c.getColor()== CardColorsEnum.PURPLE).collect(Collectors.toList()).get(0)));
        for(int i = 0;i<5;i++){
            assertTrue(player2.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.BLUE)));
        }
        //check that all cards can now be activated
        assertTrue(player1.getLeaderHand().get(0).canBeActivatedBy(player1));
        assertTrue(player1.getLeaderHand().get(1).canBeActivatedBy(player1));
        assertTrue(player2.getLeaderHand().get(0).canBeActivatedBy(player2));
        assertTrue(player2.getLeaderHand().get(1).canBeActivatedBy(player2));
        assertTrue(player1.setActivateLeaderTest(leaderHand1.get(0)));
        assertTrue(player1.setActivateLeaderTest(leaderHand1.get(1)));
        assertTrue(player2.setActivateLeaderTest(leaderHand2.get(0)));
        assertTrue(player2.setActivateLeaderTest(leaderHand2.get(1)));
        assertEquals(2,player1.getPersonalBoard().getActiveLeaderCards().size());
        assertEquals(2,player2.getPersonalBoard().getActiveLeaderCards().size());
    }

    @Test
    void basicLeaderCardTest(){
        LeaderCard leaderCard = leaders.get(8); //warehouse leader card, ID: w1
        assertEquals(leaderCard.getID(), "w1");
        assertFalse(leaderCard.applyDiscount(new ArrayList<>())); //no discount
        assertFalse(leaderCard.doTransformation(new WhiteResource())); //no transformation
    }

}