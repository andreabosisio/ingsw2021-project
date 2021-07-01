package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentRequirementTest {
    @Test
    void isSatisfiedTest() {
        //setup of all elements for test (Leader 1 require 2 blue dev,Leader 2 require 1 lv3 blue dev)
        Player player = new Player("Alphonse");
        List<LeaderCard> leaderCardList = new ArrayList<>();
        List<Requirement> requirementsLeader1 = new ArrayList<>();
        List<Requirement> requirementsLeader2 = new ArrayList<>();
        requirementsLeader1.add(new DevelopmentRequirement(0, CardColorsEnum.BLUE,2));
        LeaderCard leader1 = new ProductionLeaderCard("1",0,requirementsLeader1,new StorableResource(ResourcesEnum.BLUE));
        requirementsLeader2.add(new DevelopmentRequirement(3, CardColorsEnum.BLUE,1));
        LeaderCard leader2 = new ProductionLeaderCard("2",0,requirementsLeader2,new StorableResource(ResourcesEnum.BLUE));
        leaderCardList.add(leader1);
        leaderCardList.add(leader2);
        player.setLeaderHand(leaderCardList);
        List<Resource> placeHolder = new ArrayList<>();
        DevelopmentCard devLv1Blue = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.BLUE,0,1);
        DevelopmentCard devLv1Blue2 = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.BLUE,0,1);
        DevelopmentCard devLv2Blue = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.BLUE,0,2);
        DevelopmentCard devLv3Blue = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.BLUE,0,3);
        //check bothLeaders can't be activated when player has 0 devCards
        assertFalse(player.getLeaderHand().get(0).canBeActivatedBy(player));
        assertFalse(player.getLeaderHand().get(1).canBeActivatedBy(player));
        //check that a leader that require 2 devCards can't be activated with only 1 of the present
        assertTrue(player.getPersonalBoard().setNewProductionCard(1,devLv1Blue));
        assertFalse(player.getLeaderHand().get(0).canBeActivatedBy(player));
        //check that now leader1 can be activated with 2 blue lv1 devCards
        assertTrue(player.getPersonalBoard().setNewProductionCard(2,devLv1Blue2));
        assertTrue(player.getLeaderHand().get(0).canBeActivatedBy(player));
        //check that leader2 remain impossible to activate
        assertFalse(player.getLeaderHand().get(1).canBeActivatedBy(player));
        assertTrue(player.getPersonalBoard().setNewProductionCard(1,devLv2Blue));
        assertFalse(player.getLeaderHand().get(1).canBeActivatedBy(player));
        assertTrue(player.getPersonalBoard().setNewProductionCard(1,devLv3Blue));
        //check that leader2 can now be activated with one blue lv3 devCard
        assertTrue(player.getLeaderHand().get(1).canBeActivatedBy(player));

    }

    @Test
    void isSatisfiedTest2() {
        //setup for elements
        Player player = new Player("Edward");
        List<LeaderCard> leaderCardList = new ArrayList<>();
        List<Requirement> requirementsLeader1 = new ArrayList<>();
        List<Requirement> requirementsLeader2 = new ArrayList<>();
        requirementsLeader1.add(new DevelopmentRequirement(0, CardColorsEnum.BLUE,1));
        requirementsLeader1.add(new DevelopmentRequirement(0, CardColorsEnum.GREEN,1));
        LeaderCard leader1 = new ProductionLeaderCard("1",0,requirementsLeader1,new StorableResource(ResourcesEnum.BLUE));
        requirementsLeader2.add(new DevelopmentRequirement(2, CardColorsEnum.YELLOW,2));
        LeaderCard leader2 = new ProductionLeaderCard("2",0,requirementsLeader2,new StorableResource(ResourcesEnum.BLUE));
        leaderCardList.add(leader1);
        leaderCardList.add(leader2);
        player.setLeaderHand(leaderCardList);
        List<Resource> placeHolder = new ArrayList<>();
        DevelopmentCard devLv1Blue = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.BLUE,0,1);
        DevelopmentCard devLv1Purple = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.PURPLE,0,1);
        DevelopmentCard devLv1Green = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.GREEN,0,1);
        DevelopmentCard devLv2Yellow = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.YELLOW,0,2);
        DevelopmentCard devLv2Yellow2 = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.YELLOW,0,2);
        DevelopmentCard devLv2Blue = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.BLUE,0,2);
        //check bothLeaders can't be activated when player has 0 devCards
        assertFalse(player.getLeaderHand().get(0).canBeActivatedBy(player));
        assertFalse(player.getLeaderHand().get(1).canBeActivatedBy(player));
        //activate leader1 with 2 colors
        assertTrue(player.getPersonalBoard().setNewProductionCard(1,devLv1Blue));
        assertFalse(player.getLeaderHand().get(0).canBeActivatedBy(player));
        assertTrue(player.getPersonalBoard().setNewProductionCard(2,devLv1Green));
        assertTrue(player.getLeaderHand().get(0).canBeActivatedBy(player));
        //check that leader2 can't be activated with 2 dev cards of lever 2
        assertTrue(player.getPersonalBoard().setNewProductionCard(1,devLv2Blue));
        assertTrue(player.getPersonalBoard().setNewProductionCard(2,devLv2Yellow));
        assertFalse(player.getLeaderHand().get(1).canBeActivatedBy(player));
        //check that leader2 can be activated with 2 lv2 yellow cards
        assertTrue(player.getPersonalBoard().setNewProductionCard(3,devLv1Purple));
        assertTrue(player.getPersonalBoard().setNewProductionCard(3,devLv2Yellow2));
        assertTrue(player.getLeaderHand().get(1).canBeActivatedBy(player));

    }

    @Test
    void isSatisfiedTest3() {
        //setup for elements
        Player player = new Player("Winry");
        List<LeaderCard> leaderCardList = new ArrayList<>();
        List<Requirement> requirementsLeader = new ArrayList<>();
        requirementsLeader.add(new DevelopmentRequirement(0, CardColorsEnum.BLUE,1));
        requirementsLeader.add(new DevelopmentRequirement(0, CardColorsEnum.GREEN,1));
        requirementsLeader.add(new DevelopmentRequirement(2, CardColorsEnum.YELLOW,2));
        LeaderCard leader1 = new ProductionLeaderCard("1",0,requirementsLeader,new StorableResource(ResourcesEnum.BLUE));
        LeaderCard leader2 = new ProductionLeaderCard("2",0,requirementsLeader,new StorableResource(ResourcesEnum.BLUE));
        leaderCardList.add(leader1);
        leaderCardList.add(leader2);
        player.setLeaderHand(leaderCardList);
        List<Resource> placeHolder = new ArrayList<>();
        DevelopmentCard devLv1Blue = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.BLUE,0,1);
        DevelopmentCard devLv1Purple = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.PURPLE,0,1);
        DevelopmentCard devLv1Green = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.GREEN,0,1);
        DevelopmentCard devLv2Yellow = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.YELLOW,0,2);
        DevelopmentCard devLv2Yellow2 = new DevelopmentCard("empty",placeHolder,placeHolder,placeHolder, CardColorsEnum.YELLOW,0,2);
        //check for requirements of 2 types together
        assertFalse(player.getLeaderHand().get(0).canBeActivatedBy(player));
        assertTrue(player.getPersonalBoard().setNewProductionCard(1,devLv1Blue));
        assertFalse(player.getLeaderHand().get(0).canBeActivatedBy(player));
        assertTrue(player.getPersonalBoard().setNewProductionCard(2,devLv1Purple));
        assertFalse(player.getLeaderHand().get(0).canBeActivatedBy(player));
        assertTrue(player.getPersonalBoard().setNewProductionCard(3,devLv1Green));
        assertFalse(player.getLeaderHand().get(0).canBeActivatedBy(player));
        assertTrue(player.getPersonalBoard().setNewProductionCard(1,devLv2Yellow));
        assertFalse(player.getLeaderHand().get(0).canBeActivatedBy(player));
        //requirements are finally met
        assertTrue(player.getPersonalBoard().setNewProductionCard(2,devLv2Yellow2));
        assertTrue(player.getLeaderHand().get(0).canBeActivatedBy(player));
        assertTrue(player.getLeaderHand().get(1).canBeActivatedBy(player));
    }
}