package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.OtherResource;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LeaderCardTest {

    @Test
    void canBeActivatedTest() throws NonStorableResourceException {
        List<LeaderCard> leaders = new CardsGenerator().generateLeaderCards();
        LeaderCard leaderWithResRequirement = leaders.get(11);//req 5 purple
        LeaderCard leaderWithDevRequirement = leaders.get(0);// req lv2 green
        leaders.clear();
        leaders.add(leaderWithDevRequirement);
        leaders.add(leaderWithResRequirement);
        Player player = new Player("Bunny");
        player.setLeaderHand(leaders);
        //check that both can't be activated
        assertFalse(leaderWithResRequirement.canBeActivated(player));
        assertFalse(leaderWithDevRequirement.canBeActivated(player));
        //prepare requirements for first leader
        for(int i =0;i<5;i++) {
            assertTrue(player.getPersonalBoard().getWarehouse().addResourceToStrongBox(new OtherResource(ResourceEnum.PURPLE)));
        }
        //check that res requirements have been met but not dev requirements
        assertTrue(leaderWithResRequirement.canBeActivated(player));
        assertFalse(leaderWithDevRequirement.canBeActivated(player));
        //prepare req for second leader
        assertTrue(player.getPersonalBoard().setNewDevCard(1, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList()).get(0)));
        assertTrue(player.getPersonalBoard().setNewDevCard(1, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==2).filter(c->c.getColor()== CardColorEnum.GREEN).collect(Collectors.toList()).get(0)));
        //check that dev requirements have been met and res requirements remain met
        assertTrue(leaderWithDevRequirement.canBeActivated(player));
        assertTrue(leaderWithResRequirement.canBeActivated(player));
        //check that both requirements remain met even with more stuff added
        assertTrue(player.getPersonalBoard().setNewDevCard(2, new CardsGenerator().generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList()).get(0)));
        assertTrue(player.getPersonalBoard().getWarehouse().addResourceToStrongBox(new OtherResource(ResourceEnum.GRAY)));
        assertTrue(player.getPersonalBoard().getWarehouse().addResourceToStrongBox(new OtherResource(ResourceEnum.BLUE)));
        assertTrue(leaderWithDevRequirement.canBeActivated(player));
        assertTrue(leaderWithResRequirement.canBeActivated(player));
    }
}