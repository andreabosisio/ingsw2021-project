package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.cards.*;
import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class PersonalBoardTest {

    @Test
    void setNewDevCardOfAllLvTest() {
        CardsGenerator generator = new CardsGenerator();
        List<DevelopmentCard> lvl1Cards = generator.generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList());
        List<DevelopmentCard> lvl2Cards = generator.generateDevelopmentCards().stream().filter(c->c.getLevel()==2).collect(Collectors.toList());
        List<DevelopmentCard> lvl3Cards = generator.generateDevelopmentCards().stream().filter(c->c.getLevel()==3).collect(Collectors.toList());
        //test that a lv1 devCard can be placed in all 3 normal spots(1,2,3) when personalBoard is empty, and only on them
        PersonalBoard pBoard = new PersonalBoard();
        assertEquals(3,pBoard.getAvailablePlacement(lvl1Cards.get(0)).size());
        //check that the available positions are 1-2-3
        assertEquals(1,pBoard.getAvailablePlacement(lvl1Cards.get(0)).get(0));
        assertEquals(2,pBoard.getAvailablePlacement(lvl1Cards.get(0)).get(1));
        assertEquals(3,pBoard.getAvailablePlacement(lvl1Cards.get(0)).get(2));
        assertFalse(pBoard.setNewProductionCard(0,lvl1Cards.get(0)));
        assertFalse(pBoard.setNewProductionCard(4,lvl1Cards.get(0)));
        assertFalse(pBoard.setNewProductionCard(5,lvl1Cards.get(0)));
        //test that a lv1 devCard can't be placed on top of another lv1 card
        pBoard = new PersonalBoard();
        assertTrue(pBoard.setNewProductionCard(1,lvl1Cards.get(0)));//place one lvl1 card in pos 1
        assertEquals(2,pBoard.getAvailablePlacement(lvl1Cards.get(1)).size());//can be placed in 2 columns
        assertEquals(2,pBoard.getAvailablePlacement(lvl1Cards.get(1)).get(0));//can be placed in pos 2
        assertEquals(3,pBoard.getAvailablePlacement(lvl1Cards.get(1)).get(1));//can be placed in pos 3
        assertFalse(pBoard.setNewProductionCard(1,lvl1Cards.get(1)));//can't be placed on top of pos1
        //test that a lv2 devCard can be placed only on top of a lv1 card
        assertEquals(1,pBoard.getAvailablePlacement(lvl2Cards.get(0)).size());//can be place on 1 spot
        assertEquals(1,pBoard.getAvailablePlacement(lvl2Cards.get(0)).get(0));//the spot is the one with one lvl1 on it
        assertTrue(pBoard.setNewProductionCard(1,lvl2Cards.get(0)));//place successfully
        assertFalse(pBoard.setNewProductionCard(1,lvl2Cards.get(1)));//can't place a lv2 on top of a lv2
        //test that a lv3 devCard can be placed only on top of a lv2 card
        assertEquals(1,pBoard.getAvailablePlacement(lvl3Cards.get(0)).size());//can be place on 1 spot
        assertEquals(1,pBoard.getAvailablePlacement(lvl3Cards.get(0)).get(0));//the spot is the one with one lvl2 on it
        assertTrue(pBoard.setNewProductionCard(1,lvl3Cards.get(0)));//place successfully
        assertFalse(pBoard.setNewProductionCard(1,lvl3Cards.get(1)));//can't place a lv3 on top of a lv1
    }

    @Test
    void setSameDevCardMultipleTimesTest() {
        CardsGenerator generator = new CardsGenerator();
        List<DevelopmentCard> lvl1Cards = generator.generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList());
        List<DevelopmentCard> lvl2Cards = generator.generateDevelopmentCards().stream().filter(c->c.getLevel()==2).collect(Collectors.toList());
        List<DevelopmentCard> lvl3Cards = generator.generateDevelopmentCards().stream().filter(c->c.getLevel()==3).collect(Collectors.toList());
        PersonalBoard pBoard = new PersonalBoard();
        //check that a card can't be placed 2 times for all levels
        assertTrue(pBoard.setNewProductionCard(1,lvl1Cards.get(0)));
        assertFalse(pBoard.setNewProductionCard(2,lvl1Cards.get(0)));
        assertTrue(pBoard.setNewProductionCard(2,lvl1Cards.get(1)));
        //lv2 check
        assertTrue(pBoard.setNewProductionCard(1,lvl2Cards.get(0)));
        assertFalse(pBoard.setNewProductionCard(2,lvl2Cards.get(0)));
        assertTrue(pBoard.setNewProductionCard(2,lvl2Cards.get(1)));
        //lvl3 check
        assertTrue(pBoard.setNewProductionCard(1,lvl3Cards.get(0)));
        assertFalse(pBoard.setNewProductionCard(2,lvl3Cards.get(0)));
        assertTrue(pBoard.setNewProductionCard(2,lvl3Cards.get(1)));
    }

    @Test
    void setLeaderCardTest() throws InvalidIndexException {
        CardsGenerator generator = new CardsGenerator();
        Player player = new Player("pepe");
        List<LeaderCard> leaders = generator.generateLeaderCards().stream().filter(l->l instanceof ProductionLeaderCard).collect(Collectors.toList());
        LeaderCard leader1 = leaders.get(0);
        LeaderCard leader2 = leaders.get(1);
        LeaderCard leader3 = leaders.get(2);
        assertTrue(player.setLeaderHand(leaders.subList(0,2)));

        //check that same leader can't be placed twice
        assertTrue(player.setActivateLeaderTest(leader1));
        assertFalse(player.setActivateLeaderTest(leader1));
        //check that the leader placed is the one expected and it is in column 4(first of leaders)
        assertEquals(leader1,player.getPersonalBoard().getProductionCard(4));
        //check that same leader can't be placed as active twice
        assertFalse(player.getPersonalBoard().addToActiveLeaders(leader1));
        //check placement of 2 leaders
        assertTrue(player.setActivateLeaderTest(leader2));
        assertFalse(player.setActivateLeaderTest(leader2));
        //check that the leader placed is the one expected and it is in column 5(second of leaders)
        assertEquals(leader2,player.getPersonalBoard().getProductionCard(5));
        //check that you can't place 3 leaders
        assertFalse(player.setActivateLeaderTest(leader3));
        assertFalse(player.getPersonalBoard().addToActiveLeaders(leader3));
        //set that same leader can't be placed twice
        assertFalse(player.getPersonalBoard().setNewProductionCard((ProductionCard) leader1));
    }

    @Test
    void getEmptyBoardPointsTest() {
        List<Player> players = new ArrayList<>();
        Player player = new Player("Adriano");
        players.add(player);
        assertTrue(GameBoard.getGameBoard().createFaithTracks(players));
        //check empty board has 0 points
        assertEquals(0,player.getPersonalBoard().getPoints(player));

    }

    @Test
    void getLeaderAndWarehousePointsTest() {
        List<Player> players = new ArrayList<>();
        Player player = new Player("Miguel");
        players.add(player);
        //check that only activated leaderCards have their points counted
        CardsGenerator cardsGenerator = new CardsGenerator();
        List<LeaderCard> leaders = cardsGenerator.generateLeaderCards().subList(0,2);
        List<LeaderCard> savedLeaders = new ArrayList<>(leaders);
        assertTrue(GameBoard.getGameBoard().createFaithTracks(players));
        assertTrue(player.setLeaderHand(leaders));
        assertTrue(player.setActivateLeaderTest(leaders.get(0)));
        //check activeLeader points = player points
        assertEquals(savedLeaders.get(0).getPoints(),player.getPersonalBoard().getPoints(player));
        assertTrue(player.setActivateLeaderTest(leaders.get(1)));
        //check activeLeaders points = player points
        int expectedPoints = savedLeaders.stream().mapToInt(LeaderCard::getPoints).sum();
        assertEquals(expectedPoints,player.getPersonalBoard().getPoints(player));
        //check that 5 resources equals 1 point
        for(int i= 0;i<4;i++) {
            assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.GRAY)));
            assertEquals(expectedPoints, player.getPersonalBoard().getPoints(player));
        }
        assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.GRAY)));
        assertEquals(expectedPoints+1, player.getPersonalBoard().getPoints(player));
        //check that 10 resources equals 2 points
        for(int i= 0;i<4;i++) {
            assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.PURPLE)));
            assertEquals(expectedPoints+1, player.getPersonalBoard().getPoints(player));
        }
        assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.PURPLE)));
        assertEquals(expectedPoints+2, player.getPersonalBoard().getPoints(player));
    }

    @Test
    void getPointsFromEverythingTest() {
        List<Player> players = new ArrayList<>();
        GameBoard.getGameBoard().reset();
        Player player = new Player("Mirko");
        players.add(player);
        TurnLogic turnLogic = new TurnLogic(players,null);

        GameBoard.getGameBoard().faithProgress(player, 3);
        assertEquals(1,player.getPersonalBoard().getPoints(player));

        //check with only faithTrack
        GameBoard.getGameBoard().faithProgress(player, 3);
        assertEquals(2,player.getPersonalBoard().getPoints(player));

        //check with faithTrack and popeTile
        GameBoard.getGameBoard().faithProgress(player, 2);
        assertEquals(4,player.getPersonalBoard().getPoints(player));

        //check with devCard added
        CardsGenerator generator = new CardsGenerator();
        DevelopmentCard dev = generator.generateDevelopmentCards().get(43);
        assertTrue(player.getPersonalBoard().setNewProductionCard(1,dev));
        assertEquals(4+dev.getPoints(),player.getPersonalBoard().getPoints(player));

        //check with leaderCard added
        List<LeaderCard> leaders = generator.generateLeaderCards().subList(0,2);
        List<LeaderCard> savedLeaders = new ArrayList<>(leaders);
        assertTrue(player.setLeaderHand(leaders));
        assertTrue(player.setActivateLeaderTest(leaders.get(0)));
        assertEquals(4+dev.getPoints()+savedLeaders.get(0).getPoints(),player.getPersonalBoard().getPoints(player));

        //check with warehouse added (4 from faith,1 from warehouse,4 from devCard,3 from leaderCard)
        for(int i= 0;i<5;i++) {
            assertTrue(player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.GRAY)));
        }
        assertEquals(4+1+dev.getPoints() + savedLeaders.get(0).getPoints(),player.getPersonalBoard().getPoints(player));
    }

    @Test
    void getProductionCardTest() throws InvalidIndexException {
        Player player = new Player("pepe");
        CardsGenerator generator = new CardsGenerator();
        List<DevelopmentCard> lvl1Cards = generator.generateDevelopmentCards().stream().filter(c->c.getLevel()==1).collect(Collectors.toList());
        List<DevelopmentCard> lvl2Cards = generator.generateDevelopmentCards().stream().filter(c->c.getLevel()==2).collect(Collectors.toList());
        //test that a lv1 devCard can be placed in all 3 normal spots(1,2,3) when personalBoard is empty, and only on them
        PersonalBoard pBoard = player.getPersonalBoard();
        assertTrue(pBoard.setNewProductionCard(1,lvl1Cards.get(0)));
        assertEquals(lvl1Cards.get(0),pBoard.getProductionCard(1));
        assertTrue(pBoard.setNewProductionCard(1,lvl2Cards.get(0)));
        assertTrue(pBoard.setNewProductionCard(2,lvl1Cards.get(1)));
        assertEquals(lvl2Cards.get(0),pBoard.getProductionCard(1));
        assertEquals(lvl1Cards.get(1),pBoard.getProductionCard(2));
        //check that trying to get a card from an empty column throws an exception
        assertThrows(InvalidIndexException.class,()->pBoard.getProductionCard(5));
    }
    @Test
    void getAllBoughtDevelopmentCardsIDsTest(){
        Player player = new Player("Pep");
        List<DevelopmentCard> devCards = new CardsGenerator().generateDevelopmentCards();
        List<DevelopmentCard> lvl1Cards = devCards.stream().filter(c->c.getLevel()==1).collect(Collectors.toList());
        List<DevelopmentCard> lvl2Cards = devCards.stream().filter(c->c.getLevel()==2).collect(Collectors.toList());
        PersonalBoard pBoard = player.getPersonalBoard();
        //check that the playerBoard has 4 slots for production cards and that at the start they are basic-empty-empty-empty
        assertEquals(4,pBoard.getAllBoughtDevelopmentCardsIDs().size());
        assertEquals("basicPowerCard",pBoard.getAllBoughtDevelopmentCardsIDs().get(0).get(0));
        for(int i = 1;i<4;i++){
            assertEquals("empty",pBoard.getAllBoughtDevelopmentCardsIDs().get(i).get(0));
        }
        assertTrue(pBoard.setNewProductionCard(1,lvl1Cards.get(0)));
        assertTrue(pBoard.setNewProductionCard(1,lvl2Cards.get(0)));
        //check that there are now 2 ids saved in pos 1 and they are equals to the placed cards
        assertEquals(lvl2Cards.get(0).getID(),pBoard.getAllBoughtDevelopmentCardsIDs().get(1).get(1));
        assertEquals(lvl1Cards.get(0).getID(),pBoard.getAllBoughtDevelopmentCardsIDs().get(1).get(0));
        assertTrue(pBoard.setNewProductionCard(2,lvl1Cards.get(1)));
        assertTrue(pBoard.setNewProductionCard(2,lvl2Cards.get(1)));
        //check that there are now 2 more ids saved in pos 2 and they are equals to the placed cards
        assertEquals(lvl2Cards.get(1).getID(),pBoard.getAllBoughtDevelopmentCardsIDs().get(2).get(1));
        assertEquals(lvl1Cards.get(1).getID(),pBoard.getAllBoughtDevelopmentCardsIDs().get(2).get(0));
        //check that the Ids saved are now 4
        assertEquals(4,pBoard.getAllBoughtDevelopmentCardsIDs().size());
    }
}