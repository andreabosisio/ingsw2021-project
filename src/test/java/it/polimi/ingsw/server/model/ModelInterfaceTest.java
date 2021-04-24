package it.polimi.ingsw.server.model;

import it.polimi.ingsw.TestGameGenerator;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelInterfaceTest {

    private final int strongBoxPositionsOffset = 14;

    @Test
    void buyDevelopmentCardTurnSimulation() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        DevelopmentCard card = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN,1);
        List<Integer> resourcePositions = new ArrayList<>();
        for(int i = 0; i < card.getPrice().size(); i++){
            resourcePositions.add(i + strongBoxPositionsOffset);
        }
        //check that trying to buy a non existing card generate an exception
        assertThrows(InvalidEventException.class,()->modelInterface.buyAction("blallo",1,resourcePositions));
        game.preparePlayerForDevCard(modelInterface,0, card);
        //check that player is in start turn state
        assertEquals(modelInterface.getTurnLogic().getStartTurn(),modelInterface.getTurnLogic().getCurrentState());
        //check that trying to buy a card you can't place generate an exception
        assertThrows(InvalidEventException.class,()->modelInterface.buyAction("green",2,resourcePositions));
        //check that trying to buy a card you can't afford generate an exception
        assertThrows(InvalidEventException.class,()->modelInterface.buyAction("blue",1,resourcePositions));
        //perform buy action
        assertTrue(modelInterface.buyAction("green",1, resourcePositions));
        //check that you can't perform another buy action
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("green",1, resourcePositions));
        //check that player is now in waitDevCardPlacement state
        assertEquals(modelInterface.getTurnLogic().getWaitDevCardPlacement(), modelInterface.getTurnLogic().getCurrentState());
        //check that a placement in an invalid position generate an exception
        assertThrows(InvalidEventException.class, () -> modelInterface.placeDevCardAction(0));
        //perform the placement action
        assertTrue(modelInterface.placeDevCardAction(1));
        //check that you can't place 2 times in a row
        assertThrows(InvalidEventException.class,() -> modelInterface.placeDevCardAction(1));
        //check that player is now in endTurn state
        assertEquals(modelInterface.getTurnLogic().getEndTurn(), modelInterface.getTurnLogic().getCurrentState());
        //check that first player has devCard in his PersonalBoard
        assertEquals(card, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getProductionCard(1));
        //end first player turn
        modelInterface.endTurn();
        //check that current player is now second in both modelInterface and turnLogic
        assertEquals("second", modelInterface.getCurrentPlayer());
        assertEquals("second", modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        //check that player is in start turn
        assertEquals(modelInterface.getTurnLogic().getStartTurn(), modelInterface.getTurnLogic().getCurrentState());
        //check that endTurn is not allowed in startTurn
        assertThrows(InvalidEventException.class, modelInterface::endTurn);
    }

    @Test
    void leaderActionActivateTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        Player currentPlayer = modelInterface.getTurnLogic().getCurrentPlayer();
        //add a green DevCard with level 1 to buy the requiredCard (level 2)
        currentPlayer.getPersonalBoard().setNewDevelopmentCard(2, GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN,1));

        //check that player is in start turn state
        assertEquals(modelInterface.getTurnLogic().getStartTurn(),modelInterface.getTurnLogic().getCurrentState());
        //player cannot satisfy requirements
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("p1", false));
        //check that player is in start turn state
        assertEquals(modelInterface.getTurnLogic().getStartTurn(), modelInterface.getTurnLogic().getCurrentState());

        DevelopmentCard requiredCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN,2);
        game.preparePlayerForDevCard(modelInterface,0, requiredCard);
        List<Integer> resourcePositions = new ArrayList<>();
        for(int i = 0; i < requiredCard.getPrice().size(); i++){
            resourcePositions.add(i + strongBoxPositionsOffset);
        }
        assertTrue(modelInterface.buyAction("green", 2, resourcePositions));
        //check that player is now in waitDevCardPlacement state
        assertEquals(modelInterface.getTurnLogic().getWaitDevCardPlacement(), modelInterface.getTurnLogic().getCurrentState());
        //perform the placement action
        assertTrue(modelInterface.placeDevCardAction(2));

        //check that player is now in endTurn state
        assertEquals(modelInterface.getTurnLogic().getEndTurn(), modelInterface.getTurnLogic().getCurrentState());

        //now player has requirements. He's in endTurn state so he can make a leader action
        modelInterface.leaderAction("p1", false);
        //check if now the LeaderCard is activated
        assertEquals(currentPlayer.getPersonalBoard().getActiveLeaderCards().get(0).getID(), "p1");
        assertEquals(currentPlayer.getLeaderHand().stream().filter(card -> card.getID().equals("p1")).count(), 0);

        //check that current state is startTurn and current player is "second"
        assertEquals(modelInterface.getTurnLogic().getCurrentState(), modelInterface.getTurnLogic().getStartTurn());
        assertEquals(modelInterface.getCurrentPlayer(), "second");
        assertEquals(modelInterface.getTurnLogic().getCurrentPlayer().getNickname(), "second");

    }

    @Test
    void leaderActionDiscardTest() throws InvalidEventException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        Player currentPlayer = modelInterface.getTurnLogic().getCurrentPlayer();

        //discard first leader card by currentPlayer's hand
        modelInterface.leaderAction("p1", true);
        //check FaithProgress
        assertEquals( 1, GameBoard.getGameBoard().getFaithTrackOfPlayer(currentPlayer).getFaithMarker());
        //cannot do another leaderAction
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("m1", true));
        //check that current state is startTurn because Player has to do an action
        assertEquals(modelInterface.getTurnLogic().getCurrentState(), modelInterface.getTurnLogic().getStartTurn());

    }



    @Test
    void marketActionWithNoPlacementTurnSimulation() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        //check that market action with an invalid arrowId generate an axception
        assertThrows(InvalidIndexException.class,()->modelInterface.marketAction(12));
        //perform a market action
        modelInterface.marketAction(2);
        //check that invalid swap generate an exception
        List<Integer> swapPairs = new ArrayList<Integer>(){{
            add(1);
            add(5);
        }};
        assertThrows(EmptySlotException.class,()->modelInterface.placeResourceAction(swapPairs));
        //expected res are white/white/white/red so a legal swap pair is an empty one
        modelInterface.placeResourceAction(new ArrayList<>());
        //check that the red resource has increased the player's faith by one
        assertEquals(1,GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(0)).getFaithMarker());
        modelInterface.endTurn();
        GameBoard.getGameBoard().reset();
    }
    @Test
    void marketActionTurnSimulation() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        //perform a market action
        modelInterface.marketAction(1);//generate yellow/yellow/purple/purple
        //check that invalid swap generate an exception
        List<Integer> swapPairs = new ArrayList<Integer>(){{
            add(0);add(32);
        }};
        assertThrows(InvalidEventException.class,()->modelInterface.placeResourceAction(swapPairs));

        //check that giving odd number of swap generate an exception
        List<Integer> swapPairs2 = new ArrayList<Integer>(){{
            add(0);add(4);
            add(1);add(6);
            add(2);add(7);
            add(8);
        }};
        assertThrows(InvalidEventException.class,()->modelInterface.placeResourceAction(swapPairs2));
        //check that ending swap in an illegal configuration remains in the same state
        swapPairs2.remove(6);
        assertFalse(modelInterface.placeResourceAction(swapPairs2));
        assertEquals(modelInterface.getTurnLogic().getWaitResourcePlacement(),modelInterface.getTurnLogic().getCurrentState());
        //check that a series of swap that ends in a legal position moves state to endTurn
        swapPairs2.clear();
        swapPairs2.add(4);
        swapPairs2.add(5);
        assertTrue(modelInterface.placeResourceAction(swapPairs2));
        assertEquals(modelInterface.getTurnLogic().getEndTurn(),modelInterface.getTurnLogic().getCurrentState());
        //check that discarding one of the resources from market increased all other player's faith by 1
        assertEquals(0,GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(0)).getFaithMarker());
        assertEquals(1,GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(1)).getFaithMarker());
        assertEquals(1,GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(2)).getFaithMarker());
        assertEquals(1,GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(3)).getFaithMarker());
        GameBoard.getGameBoard().reset();
    }

    @Test
    void marketActionWithMultiTransformationSimulation() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(false);
        game.setMarketTrayAuto(modelInterface);
        game.setDevelopmentCardsGrid(modelInterface);
        List<Integer> marketLeaderIndexes = new ArrayList<Integer>(){{
            add(4);//market leaders:requires 2 yellow and 1 blue dev of any level----transform white to purple
            add(5);//market leaders:requires 2 green and 1 purple dev of any level----transform white to blue
            add(0);add(1);add(2);add(3);add(7);add(8);//random leaders for other players
        }};
        game.setLeaderInHand(modelInterface,marketLeaderIndexes);
        //prepare player for activation of first leader
        DevelopmentCard card = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.YELLOW,1);
        game.preparePlayerForDevCard(modelInterface,0,card);
        List<Integer> resourcePositions = new ArrayList<>();
        for(int i = 0; i < card.getPrice().size(); i++){
            resourcePositions.add(i + strongBoxPositionsOffset);
        }
        //place yellow lv1 in pos 1
        modelInterface.buyAction("yellow",1,resourcePositions);
        modelInterface.placeDevCardAction(1);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);
        //prepare for second devCard
        DevelopmentCard card2 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.YELLOW,2);
        game.preparePlayerForDevCard(modelInterface,0,card2);
        List<Integer> resourcePositions2 = new ArrayList<>();
        for(int i = 0; i < card2.getPrice().size(); i++){
            resourcePositions2.add(i + strongBoxPositionsOffset);
        }
        //place yellow lv2 in pos 1
        modelInterface.buyAction("yellow",2,resourcePositions2);
        modelInterface.placeDevCardAction(1);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare for third devCard
        DevelopmentCard card3 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.BLUE,1);
        game.preparePlayerForDevCard(modelInterface,0,card3);
        List<Integer> resourcePositions3 = new ArrayList<>();
        for(int i = 0; i < card3.getPrice().size(); i++){
            resourcePositions3.add(i + strongBoxPositionsOffset);
        }
        //place blue lv1 in pos 2
        modelInterface.buyAction("blue",1,resourcePositions3);
        modelInterface.placeDevCardAction(2);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);
        //activate first Leader
        modelInterface.leaderAction("m1",false);
        //test transformation with one leader
        modelInterface.marketAction(2);//expected res are(3white and 1 red)
        //market action has produced 3 purple res because there is only one active marketLeader
        List<Integer> swap =new ArrayList<Integer>(){{
            add(0);add(7);
            add(1);add(8);
            add(2);add(9);
        }};
        modelInterface.placeResourceAction(swap);
        //check that player has 3 resources and they are all purple
        assertEquals(3,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().size());
        assertEquals(ResourceEnum.PURPLE,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(0).getColor());
        assertEquals(ResourceEnum.PURPLE,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(1).getColor());
        assertEquals(ResourceEnum.PURPLE,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(2).getColor());
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare fourth devCard
        DevelopmentCard card4 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN,2);
        game.preparePlayerForDevCard(modelInterface,0,card4);
        List<Integer> resourcePositions4 = new ArrayList<>();
        for(int i = 0; i < card4.getPrice().size(); i++){
            resourcePositions4.add(i + strongBoxPositionsOffset);
        }
        //place green lv2 in pos 2
        modelInterface.buyAction("green",2,resourcePositions4);
        modelInterface.placeDevCardAction(2);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare for fifth devCard
        DevelopmentCard card5 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN,1);
        game.preparePlayerForDevCard(modelInterface,0,card5);
        List<Integer> resourcePositions5 = new ArrayList<>();
        for(int i = 0; i < card5.getPrice().size(); i++){
            resourcePositions5.add(i + strongBoxPositionsOffset);
        }
        //place green lv1 in pos 3
        modelInterface.buyAction("green",1,resourcePositions5);
        modelInterface.placeDevCardAction(3);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare for sixth devCard
        DevelopmentCard card6 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.PURPLE,2);
        game.preparePlayerForDevCard(modelInterface,0,card6);
        List<Integer> resourcePositions6 = new ArrayList<>();
        for(int i = 0; i < card6.getPrice().size(); i++){
            resourcePositions6.add(i + strongBoxPositionsOffset);
        }
        //place purple lv2 in pos 3
        modelInterface.buyAction("purple",2,resourcePositions6);
        modelInterface.placeDevCardAction(3);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);
        //activate second leader
        modelInterface.leaderAction("m2",false);
        //try market action with 2 leaderCards
        modelInterface.marketAction(2);
        //check that giving less chosen color than needed (needed 3:given 2) throws and exception
        List<String> chosenColors = new ArrayList<String>(){{
            add("purple");
            add("blue");
        }};
        assertThrows(InvalidEventException.class,()->modelInterface.transformationAction(chosenColors));
        //check that giving a wrong color generate an exception
        chosenColors.add("yellow");
        assertThrows(InvalidEventException.class,()->modelInterface.transformationAction(chosenColors));
        //check that giving a non existing color generate an exception
        chosenColors.remove(2);
        chosenColors.add("blu");
        assertThrows(InvalidEventException.class,()->modelInterface.transformationAction(chosenColors));
        //check with right input
        chosenColors.remove(2);
        chosenColors.add("blue");
        modelInterface.transformationAction(chosenColors);
        swap =new ArrayList<Integer>(){{
            add(1);add(5);
            add(2);add(6);
        }};
        modelInterface.placeResourceAction(swap);
        //check that player has 5 resources and they are 3 purple and 2 blue
        assertEquals(5,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().size());
        assertEquals(ResourceEnum.PURPLE,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(3).getColor());
        assertEquals(ResourceEnum.PURPLE,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(4).getColor());
        assertEquals(ResourceEnum.BLUE,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(0).getColor());
        assertEquals(ResourceEnum.BLUE,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(1).getColor());
        assertEquals(ResourceEnum.PURPLE,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(2).getColor());
        GameBoard.getGameBoard().reset();
    }
}