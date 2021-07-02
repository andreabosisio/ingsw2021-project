package it.polimi.ingsw.server.model;

import it.polimi.ingsw.TestGameGenerator;
import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.events.receive.*;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.gameBoard.DevelopmentCardsGrid;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.StorableResource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class ModelInterfaceTest {

    private final int strongBoxPositionsOffset = 14;

    @Test
    void buyDevelopmentCardTurnSimulation() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException, InvalidSetupException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        DevelopmentCard card = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.GREEN, 1);
        List<Integer> resourcePositions = new ArrayList<>();
        for (int i = 0; i < card.getPrice().size(); i++) {
            resourcePositions.add(i + strongBoxPositionsOffset);
        }
        //check that trying to buy a non existing card generate an exception
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("ball", 1, resourcePositions));
        assertThrows(InvalidEventException.class, () -> new BuyEventFromClient("first","ball",1,resourcePositions).doAction(modelInterface));
        game.preparePlayerForDevCard(modelInterface, 0, card);
        //check that player is in start turn state
        assertEquals(modelInterface.getStartTurn(), modelInterface.getCurrentState());
        //check that trying to buy a card you can't place generate an exception
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("green", 2, resourcePositions));
        assertThrows(InvalidEventException.class, () -> new BuyEventFromClient("first","green",2,resourcePositions).doAction(modelInterface));
        //check that trying to buy a card you can't afford generate an exception
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("blue", 1, resourcePositions));
        assertThrows(InvalidEventException.class, () -> new BuyEventFromClient("first","blue",1,resourcePositions).doAction(modelInterface));
        //perform buy action
        assertTrue(new BuyEventFromClient("first","green",1,resourcePositions).doAction(modelInterface));
        //check that you can't perform another buy action
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("green", 1, resourcePositions));
        assertThrows(InvalidEventException.class, () -> new BuyEventFromClient("first","green",1,resourcePositions).doAction(modelInterface));
        //check that player is now in waitDevCardPlacement state
        assertEquals(modelInterface.getWaitDevCardPlacement(), modelInterface.getCurrentState());
        //check that you can't perform a leader action in this State
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("m1", true));
        assertThrows(InvalidEventException.class, () -> new LeaderHandEventFromClient("first","m1",true).doAction(modelInterface));
        //check that a placement in an invalid position generate an exception(0 is only for basic)
        assertThrows(InvalidEventException.class, () -> modelInterface.placeDevelopmentCardAction(0));
        assertThrows(InvalidEventException.class, () -> new PlaceDevelopmentCardEventFromClient("first",0).doAction(modelInterface));
        //perform the placement action
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",1).doAction(modelInterface));
        //check that you can't place 2 times in a row
        assertThrows(InvalidEventException.class, () -> modelInterface.placeDevelopmentCardAction(1));
        assertThrows(InvalidEventException.class, () -> new PlaceDevelopmentCardEventFromClient("first",1).doAction(modelInterface));
        //check that player is now in endTurn state
        assertEquals(modelInterface.getEndTurn(), modelInterface.getCurrentState());
        //check that first player has devCard in his PersonalBoard
        assertEquals(card, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getProductionCard(1));
        //end first player turn
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        //check that current player is now second in both modelInterface and turnLogic
        assertEquals("second", modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        assertEquals("second", modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        //check that player is in start turn
        assertEquals(modelInterface.getStartTurn(), modelInterface.getCurrentState());
        //check that endTurn is not allowed in startTurn
        assertThrows(InvalidEventException.class, modelInterface::endTurn);
        assertThrows(InvalidEventException.class, () -> new EndTurnEventFromClient("second").doAction(modelInterface));
    }

    @Test
    void leaderActionActivateTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException, InvalidSetupException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        Player currentPlayer = modelInterface.getTurnLogic().getCurrentPlayer();
        //add a green DevCard with level 1 to buy the requiredCard (level 2)
        currentPlayer.getPersonalBoard().setNewProductionCard(2, GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.GREEN, 1));
        //check that player is in start turn state
        assertEquals(modelInterface.getStartTurn(), modelInterface.getCurrentState());
        //player cannot satisfy requirements
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("p1", false));
        assertThrows(InvalidEventException.class, () -> new LeaderHandEventFromClient("first","p1",false).doAction(modelInterface));
        //check that player is in start turn state
        assertEquals(modelInterface.getStartTurn(), modelInterface.getCurrentState());
        //prepare player for the required dev card
        DevelopmentCard requiredCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.GREEN, 2);
        game.preparePlayerForDevCard(modelInterface, 0, requiredCard);
        List<Integer> resourcePositions = new ArrayList<>();
        for (int i = 0; i < requiredCard.getPrice().size(); i++) {
            resourcePositions.add(i + strongBoxPositionsOffset);
        }
        //buy the card
        assertTrue(new BuyEventFromClient("first","green",2,resourcePositions).doAction(modelInterface));
        //check that player is now in waitDevCardPlacement state
        assertEquals(modelInterface.getWaitDevCardPlacement(), modelInterface.getCurrentState());
        //perform the placement action
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",2).doAction(modelInterface));
        //check that player is now in endTurn state
        assertEquals(modelInterface.getEndTurn(), modelInterface.getCurrentState());
        //now player has requirements. He's in endTurn state so he can make a leader action
        assertTrue(new LeaderHandEventFromClient("first","p1",false).doAction(modelInterface));
        //check if now the LeaderCard is activated
        assertEquals(currentPlayer.getPersonalBoard().getActiveLeaderCards().get(0).getID(), "p1");
        assertEquals(currentPlayer.getLeaderHand().stream().filter(card -> card.getID().equals("p1")).count(), 0);
        //check that current state is startTurn and current player is "second"
        assertEquals(modelInterface.getCurrentState(), modelInterface.getStartTurn());
        assertEquals("second", modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
    }

    @Test
    void leaderActionDiscardTest() throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        Player currentPlayer = modelInterface.getTurnLogic().getCurrentPlayer();

        //cannot discard a LeaderCard which is not in the hand of the player
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("d1", true));
        assertThrows(InvalidEventException.class, () -> new LeaderHandEventFromClient("first","d1",true).doAction(modelInterface));


        //discard first leader card by currentPlayer's hand
        assertTrue(new LeaderHandEventFromClient("first","p1",true).doAction(modelInterface));

        //check FaithProgress
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(currentPlayer).getFaithMarker());
        //cannot do another leaderAction
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("m1", true));
        assertThrows(InvalidEventException.class, () -> new LeaderHandEventFromClient("first","m1",true).doAction(modelInterface));

        //check that current state is startTurn because Player has to do an action
        assertEquals(modelInterface.getCurrentState(), modelInterface.getStartTurn());

        //market action
        assertTrue(new MarketEventFromClient("first",0).doAction(modelInterface));
        //check that turnLogic is waiting for the resources placement
        assertEquals(modelInterface.getCurrentState(), modelInterface.getWaitResourcePlacement());
        //modelInterface.placeResourceAction(new ArrayList<>(),true);
        assertTrue(new PlaceResourcesEventFromClient("first",new ArrayList<>(),true).doAction(modelInterface));

        //currentState: EndTurnState. Player can do another leaderAction
        assertEquals(modelInterface.getCurrentState(), modelInterface.getEndTurn());
        //cannot discard a LeaderCard which is not in the hand of the player
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("w1", true));
        assertThrows(InvalidEventException.class, () -> new LeaderHandEventFromClient("first","w1",true).doAction(modelInterface));

        //cannot satisfy requirements to activate "m1"
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("m1", false));
        assertThrows(InvalidEventException.class, () -> new LeaderHandEventFromClient("first","m1",false).doAction(modelInterface));

        //cannot do other action but leaderAction
        assertThrows(InvalidEventException.class, () -> modelInterface.marketAction(2));
        assertThrows(InvalidEventException.class, () -> new MarketEventFromClient("first",2).doAction(modelInterface));

        //discard "m1"
        assertTrue(new LeaderHandEventFromClient("first","m1",true).doAction(modelInterface));

        //currentState: StartTurnState.
        assertEquals(modelInterface.getCurrentState(), modelInterface.getStartTurn());
    }

    @Test
    void marketActionWithNoPlacementTurnSimulation() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        //check that market action with an invalid arrowId generate an exception
        assertThrows(InvalidIndexException.class, () -> modelInterface.marketAction(12));
        assertThrows(InvalidIndexException.class, () -> new MarketEventFromClient("first",12).doAction(modelInterface));

        //perform a market action
        assertTrue(new MarketEventFromClient("first",2).doAction(modelInterface));

        //check that invalid swap generate an exception
        List<Integer> swapPairs = new ArrayList<>() {{
            add(1);
            add(5);
        }};
        assertThrows(InvalidEventException.class, () -> modelInterface.placeResourceAction(swapPairs,true));
        assertThrows(InvalidEventException.class, () -> new PlaceResourcesEventFromClient("first",swapPairs,true).doAction(modelInterface));

        //expected res are white/white/white/red so a legal swap pair is an empty one
        assertTrue(new PlaceResourcesEventFromClient("first",new ArrayList<>(),true).doAction(modelInterface));

        //check that the red resource has increased the player's faith by one
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(0)).getFaithMarker());
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
    }

    @Test
    void marketActionTurnSimulation() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        //perform a market action
        assertTrue(new MarketEventFromClient("first",1).doAction(modelInterface));//generate yellow/yellow/purple/purple

        //check that invalid swap generate an exception
        List<Integer> swapPairs = new ArrayList<>() {{
            add(0);
            add(32);
        }};
        assertThrows(InvalidEventException.class, () -> modelInterface.placeResourceAction(swapPairs,true));
        assertThrows(InvalidEventException.class, () -> new PlaceResourcesEventFromClient("first",swapPairs,true).doAction(modelInterface));

        //check that giving odd number of swap generate an exception
        List<Integer> swapPairs2 = new ArrayList<>() {{
            add(0);
            add(4);
            add(1);
            add(6);
            add(2);
            add(7);
            add(8);
        }};
        assertThrows(InvalidEventException.class, () -> modelInterface.placeResourceAction(swapPairs2,true));
        assertThrows(InvalidEventException.class, () -> new PlaceResourcesEventFromClient("first",swapPairs2,true).doAction(modelInterface));

        //check that ending swap in an illegal configuration remains in waitResourcePlacement state
        swapPairs2.remove(6);
        assertThrows(InvalidEventException.class, () -> modelInterface.placeResourceAction(swapPairs2,true));
        assertThrows(InvalidEventException.class, () -> new PlaceResourcesEventFromClient("first",swapPairs2,true).doAction(modelInterface));

        assertEquals(modelInterface.getWaitResourcePlacement(), modelInterface.getCurrentState());
        //check that a series of swap that ends in a legal position moves state to endTurn
        swapPairs2.clear();
        swapPairs2.add(4);
        swapPairs2.add(5);
        assertTrue(new PlaceResourcesEventFromClient("first",swapPairs2,true).doAction(modelInterface));

        assertEquals(modelInterface.getEndTurn(), modelInterface.getCurrentState());
        //check that discarding one of the resources from market increased all other player's faith by 1
        assertEquals(0, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(0)).getFaithMarker());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(1)).getFaithMarker());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(2)).getFaithMarker());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(3)).getFaithMarker());
    }

    @Test
    void marketActionWithMultiTransformationSimulation() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException, NonStorableResourceException, InvalidSetupException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(false);
        game.setMarketTrayAuto();
        game.setDevelopmentCardsGrid();
        List<Integer> marketLeaderIndexes = new ArrayList<>() {{
            add(4);//market leaders:requires 2 yellow and 1 blue dev of any level----transform white to purple
            add(5);//market leaders:requires 2 green and 1 purple dev of any level----transform white to blue
            add(0);
            add(1);
            add(2);
            add(3);
            add(7);
            add(8);//random leaders for other players
        }};
        game.setLeaderInHand(modelInterface, marketLeaderIndexes);

        //prepare player for activation of first leader
        DevelopmentCard card = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.YELLOW, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card);
        List<Integer> resourcePositions = new ArrayList<>();
        for (int i = 0; i < card.getPrice().size(); i++) {
            resourcePositions.add(i + strongBoxPositionsOffset);
        }
        //place yellow lv1 in pos 1
        assertTrue(new BuyEventFromClient("first","yellow",1,resourcePositions).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",1).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //prepare for second devCard
        DevelopmentCard card2 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.YELLOW, 2);
        game.preparePlayerForDevCard(modelInterface, 0, card2);
        List<Integer> resourcePositions2 = new ArrayList<>();
        for (int i = 0; i < card2.getPrice().size(); i++) {
            resourcePositions2.add(i + strongBoxPositionsOffset);
        }
        //place yellow lv2 in pos 1
        assertTrue(new BuyEventFromClient("first","yellow",2,resourcePositions2).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",1).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //prepare for third devCard
        DevelopmentCard card3 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.BLUE, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card3);
        List<Integer> resourcePositions3 = new ArrayList<>();
        for (int i = 0; i < card3.getPrice().size(); i++) {
            resourcePositions3.add(i + strongBoxPositionsOffset);
        }
        //place blue lv1 in pos 2
        assertTrue(new BuyEventFromClient("first","blue",1,resourcePositions3).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",2).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //activate first Leader
        assertTrue(new LeaderHandEventFromClient("first","m1",false).doAction(modelInterface));

        //test transformation with one leader
        assertTrue(new MarketEventFromClient("first",2).doAction(modelInterface));//expected res are(3white and 1 red)

        //market action has produced 3 purple res because there is only one active marketLeader
        List<Integer> swap = new ArrayList<>() {{
            add(0);
            add(7);
            add(1);
            add(8);
            add(2);
            add(9);
        }};
        assertTrue(new PlaceResourcesEventFromClient("first",swap,true).doAction(modelInterface));

        //check that player has 3 resources and they are all purple
        assertEquals(3, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().size());
        assertEquals(ResourcesEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(0).getColor());
        assertEquals(ResourcesEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(1).getColor());
        assertEquals(ResourcesEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(2).getColor());
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //prepare fourth devCard
        DevelopmentCard card4 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.GREEN, 2);
        game.preparePlayerForDevCard(modelInterface, 0, card4);
        List<Integer> resourcePositions4 = new ArrayList<>();
        for (int i = 0; i < card4.getPrice().size(); i++) {
            resourcePositions4.add(i + strongBoxPositionsOffset);
        }
        //place green lv2 in pos 2
        assertTrue(new BuyEventFromClient("first","green",2,resourcePositions4).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",2).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //prepare for fifth devCard
        DevelopmentCard card5 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.GREEN, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card5);
        List<Integer> resourcePositions5 = new ArrayList<>();
        for (int i = 0; i < card5.getPrice().size(); i++) {
            resourcePositions5.add(i + strongBoxPositionsOffset);
        }
        //place green lv1 in pos 3
        assertTrue(new BuyEventFromClient("first","green",1,resourcePositions5).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",3).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //prepare for sixth devCard
        DevelopmentCard card6 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.PURPLE, 2);
        game.preparePlayerForDevCard(modelInterface, 0, card6);
        List<Integer> resourcePositions6 = new ArrayList<>();
        for (int i = 0; i < card6.getPrice().size(); i++) {
            resourcePositions6.add(i + strongBoxPositionsOffset);
        }
        //place purple lv2 in pos 3
        assertTrue(new BuyEventFromClient("first","purple",2,resourcePositions6).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",3).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //activate second leader
        assertTrue(new LeaderHandEventFromClient("first","m2",false).doAction(modelInterface));

        //try market action with 2 leaderCards
        assertTrue(new MarketEventFromClient("first",2).doAction(modelInterface));

        //check that giving less chosen color than needed (needed 3:given 2) throws and exception
        List<String> chosenColors = new ArrayList<>() {{
            add("purple");
            add("blue");
        }};
        assertThrows(InvalidEventException.class, () -> modelInterface.transformationAction(chosenColors));
        assertThrows(InvalidEventException.class, () -> new TransformationEventFromClient("first",chosenColors).doAction(modelInterface));

        //check that giving a wrong color generate an exception
        chosenColors.add("yellow");
        assertThrows(InvalidEventException.class, () -> modelInterface.transformationAction(chosenColors));
        assertThrows(InvalidEventException.class, () -> new TransformationEventFromClient("first",chosenColors).doAction(modelInterface));

        //check that giving a non existing color generate an exception
        chosenColors.remove(2);
        chosenColors.add("blu");
        assertThrows(InvalidEventException.class, () -> modelInterface.transformationAction(chosenColors));
        assertThrows(InvalidEventException.class, () -> new TransformationEventFromClient("first",chosenColors).doAction(modelInterface));

        //check with right input
        chosenColors.remove(2);
        chosenColors.add("blue");
        assertTrue(new TransformationEventFromClient("first",chosenColors).doAction(modelInterface));

        swap = new ArrayList<>() {{
            add(1);
            add(5);
            add(2);
            add(6);
        }};
        assertTrue(new PlaceResourcesEventFromClient("first",swap,true).doAction(modelInterface));


        //check that player has 5 resources and they are 3 purple and 2 blue
        assertEquals(5, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().size());
        assertEquals(ResourcesEnum.BLUE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(0).getColor());
        assertEquals(ResourcesEnum.BLUE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(1).getColor());
        assertEquals(ResourcesEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(2).getColor());
        assertEquals(ResourcesEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(3).getColor());
        assertEquals(ResourcesEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(4).getColor());
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));

        game.roundOfNothing(modelInterface);
        assertTrue(new MarketEventFromClient("first",3).doAction(modelInterface));

        chosenColors.clear();
        chosenColors.add("purple");
        assertTrue(new TransformationEventFromClient("first",chosenColors).doAction(modelInterface));

        swap.clear();
        swap.add(0);
        swap.add(4);
        assertTrue(new PlaceResourcesEventFromClient("first",swap,true).doAction(modelInterface));

        //check that player has 6 resources
        assertEquals(6, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().size());
        //check that they are 1 gray 2 blue and 3 purple
        assertEquals(ResourcesEnum.GRAY, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(0).getColor());
        assertEquals(ResourcesEnum.BLUE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(1).getColor());
        assertEquals(ResourcesEnum.BLUE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(2).getColor());
        assertEquals(ResourcesEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(3).getColor());
        assertEquals(ResourcesEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(4).getColor());
        assertEquals(ResourcesEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(5).getColor());
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);
        assertTrue(new MarketEventFromClient("first",3).doAction(modelInterface));
        //modelInterface.disconnectPlayer("first");
    }

    @Test
    void productionActionTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException, NonStorableResourceException, InvalidSetupException {
        //GameBoard.getGameBoard().reset();
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        List<Integer> numberOfDevCards = new ArrayList<>();
        List<Integer> positionOfResForProdSlot1 = new ArrayList<>();
        List<Integer> positionOfResForProdSlot2 = new ArrayList<>();
        List<Integer> positionOfResForProdSlot3 = new ArrayList<>();
        List<Integer> positionOfResForBasicSlot = new ArrayList<>();
        List<Integer> positionOfResForLeaderSlot = new ArrayList<>();
        List<DevelopmentCard> developmentCards = new ArrayList<>();
        Player firstPlayer = modelInterface.getTurnLogic().getPlayers().get(0);

        firstPlayer.getPersonalBoard().setNewProductionCard((ProductionCard) firstPlayer.getLeaderHand().get(0));

        numberOfDevCards.add(7);
        numberOfDevCards.add(0);
        numberOfDevCards.add(0);
        numberOfDevCards.add(0);
        // Set the Development card in the Player's Board
        game.setDevCardInHand(modelInterface, numberOfDevCards);
        // The player has the correct number of Development Card
        assertEquals(7, firstPlayer.getPersonalBoard().getAllDevelopmentCards().size());
        assertEquals(0, firstPlayer.getPersonalBoard().getWarehouse().getAllResources().size());

        developmentCards.add(firstPlayer.getPersonalBoard().getAllDevelopmentCards().get(0));
        developmentCards.add(firstPlayer.getPersonalBoard().getAllDevelopmentCards().get(3));
        developmentCards.add(firstPlayer.getPersonalBoard().getAllDevelopmentCards().get(5));

        int offset = strongBoxPositionsOffset;
        int constantOffset;
        // Set the necessary resources to do the first production
        game.preparePlayerForProductionDevCard(modelInterface, firstPlayer, developmentCards.get(0));
        for (int i = 0; i < developmentCards.get(0).getInResources().size(); i++) {
            positionOfResForProdSlot1.add(i + strongBoxPositionsOffset);
            offset++;
        }
        constantOffset = offset;
        // Set the necessary resources to do the second production
        game.preparePlayerForProductionDevCard(modelInterface, firstPlayer, developmentCards.get(1));
        for (int j = 0; j < developmentCards.get(1).getInResources().size(); j++) {
            positionOfResForProdSlot2.add(j + constantOffset);
            offset++;
        }
        constantOffset = offset;
        // Set the necessary resources to do the third production
        game.preparePlayerForProductionDevCard(modelInterface, firstPlayer, developmentCards.get(2));
        for (int t = 0; t < developmentCards.get(2).getInResources().size(); t++) {
            positionOfResForProdSlot3.add(t + constantOffset);
            offset++;
        }
        constantOffset = offset;
        // Set the necessary resources to do the basic production and the leader action
        positionOfResForBasicSlot.add(constantOffset);
        positionOfResForBasicSlot.add(constantOffset + 1);
        positionOfResForLeaderSlot.add(constantOffset + 2);
        List<Resource> resources = new ArrayList<>();
        resources.add(new StorableResource(ResourcesEnum.YELLOW));
        resources.add(new StorableResource(ResourcesEnum.GRAY));
        firstPlayer.getPersonalBoard().
                getWarehouse().addResourcesToStrongBox(resources);
        firstPlayer.getPersonalBoard().
                getWarehouse().addResourcesToStrongBox(resources.get(0));

        // Check that player is in start turn state
        assertEquals(modelInterface.getStartTurn(), modelInterface.getCurrentState());

        Map<Integer, List<Integer>> inResourcesForEachProductions = new HashMap<>();
        Map<Integer, String> outResourcesForEachProductions = new HashMap<>();
        inResourcesForEachProductions.put(0, positionOfResForBasicSlot);
        inResourcesForEachProductions.put(1, positionOfResForProdSlot1);
        inResourcesForEachProductions.put(2, positionOfResForProdSlot2);
        inResourcesForEachProductions.put(3, positionOfResForProdSlot3);
        inResourcesForEachProductions.put(4, positionOfResForLeaderSlot);

        outResourcesForEachProductions.put(0, "BLUE");
        outResourcesForEachProductions.put(4, "PURPLE");

        // Perform production action
        assertTrue(new ProductionEventFromClient("first",inResourcesForEachProductions,outResourcesForEachProductions).doAction(modelInterface));

        // Check that you can't perform another production action
        assertThrows(InvalidEventException.class, () -> new ProductionEventFromClient("first",inResourcesForEachProductions,outResourcesForEachProductions).doAction(modelInterface));

        // Check that the Player has the correct resources and the correct progress of the Faith Track
        List<Resource> newRes = new ArrayList<>();
        newRes.addAll(developmentCards.get(0).getOutResources());
        newRes.addAll(developmentCards.get(1).getOutResources());
        newRes.addAll(developmentCards.get(2).getOutResources());
        long newResources = newRes.stream().filter(resource -> resource instanceof StorableResource).count();
        long redResources = newRes.stream().filter(resource -> resource instanceof RedResource).count();
        assertEquals(newResources + 2, firstPlayer.getPersonalBoard().getWarehouse().getAllResources().size());
        assertEquals(redResources + 1, GameBoard.getGameBoard().getFaithTracks().get(0).getFaithMarker());
        List<Resource> playerRes = new ArrayList<>(firstPlayer.getPersonalBoard().getWarehouse().getAllResources());
        for (Resource resource : newRes) {
            if (resource instanceof StorableResource) {
                assertTrue(playerRes.contains(resource));
                playerRes.remove(resource);
            }
        }
        assertEquals(2, playerRes.size());
        assertEquals(ResourcesEnum.BLUE, playerRes.get(0).getColor());
        assertEquals(ResourcesEnum.PURPLE, playerRes.get(1).getColor());

        // Check that player is now in EndTurnState state
        assertEquals(modelInterface.getEndTurn(), modelInterface.getCurrentState());

        // End first player turn
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));

        assertEquals("second", modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        // Check that player is in start turn
        assertEquals(modelInterface.getStartTurn(), modelInterface.getCurrentState());
        // The second player do his turn
        game.preparePlayerForDevCard(modelInterface, 1, GameBoard.getGameBoard().
                getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.PURPLE, 1));
        positionOfResForProdSlot1.clear();
        for (int i = 0; i < GameBoard.getGameBoard().getDevelopmentCardsGrid().
                getCardByColorAndLevel(CardColorsEnum.PURPLE, 1).getPrice().size(); i++) {
            positionOfResForProdSlot1.add(i + strongBoxPositionsOffset);
        }
        assertTrue(new BuyEventFromClient("second","purple",1,positionOfResForProdSlot1).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("second",1).doAction(modelInterface));

        assertTrue(new EndTurnEventFromClient("second").doAction(modelInterface));

        // The third player do his turn
        game.preparePlayerForDevCard(modelInterface, 2, GameBoard.getGameBoard().
                getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.PURPLE, 1));
        positionOfResForProdSlot1.clear();
        for (int i = 0; i < GameBoard.getGameBoard().getDevelopmentCardsGrid().
                getCardByColorAndLevel(CardColorsEnum.PURPLE, 1).getPrice().size(); i++) {
            positionOfResForProdSlot1.add(i + strongBoxPositionsOffset);
        }
        assertTrue(new BuyEventFromClient("third","purple",1,positionOfResForProdSlot1).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("third",1).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("third").doAction(modelInterface));

        // The fourth player do his turn
        game.preparePlayerForDevCard(modelInterface, 3, GameBoard.getGameBoard().
                getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.PURPLE, 1));
        positionOfResForProdSlot1.clear();
        for (int i = 0; i < GameBoard.getGameBoard().getDevelopmentCardsGrid().
                getCardByColorAndLevel(CardColorsEnum.PURPLE, 1).getPrice().size(); i++) {
            positionOfResForProdSlot1.add(i + strongBoxPositionsOffset);
        }
        assertTrue(new BuyEventFromClient("fourth","purple",1,positionOfResForProdSlot1).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("fourth",1).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("fourth").doAction(modelInterface));

        // The first Player has seven Development Card and the last Player do his turn, so the game is over
        assertEquals(modelInterface.getEndGame(), modelInterface.getCurrentState());
    }

    @Test
    public void invalidProductionActionTest() {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        List<Integer> numberOfDevCards = new ArrayList<>();
        List<Integer> positionOfResForProdSlot1 = new ArrayList<>();
        List<Integer> positionOfResForBasicSlot = new ArrayList<>();
        List<DevelopmentCard> developmentCards = new ArrayList<>();
        Player firstPlayer = modelInterface.getTurnLogic().getPlayers().get(0);

        firstPlayer.getPersonalBoard().setNewProductionCard((ProductionCard) firstPlayer.getLeaderHand().get(0));

        numberOfDevCards.add(1);
        numberOfDevCards.add(0);
        numberOfDevCards.add(0);
        numberOfDevCards.add(0);
        // Set the Development card in the Player's Board
        game.setDevCardInHand(modelInterface, numberOfDevCards);
        developmentCards.add(firstPlayer.getPersonalBoard().getAllDevelopmentCards().get(0));

        // Set the necessary resources to do the first production
        game.preparePlayerForProductionDevCard(modelInterface, firstPlayer, developmentCards.get(0));
        int offset = strongBoxPositionsOffset;
        for (int i = 0; i < developmentCards.get(0).getInResources().size(); i++) {
            positionOfResForProdSlot1.add(i + strongBoxPositionsOffset);
            offset++;
        }
        positionOfResForBasicSlot.add(offset);
        positionOfResForBasicSlot.add(offset + 1);

        firstPlayer.getPersonalBoard().
                getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.PURPLE));

        // The Player can not do the Basic Production with only one resource
        Map<Integer, List<Integer>> inResourcesForEachProductions = new HashMap<>();
        Map<Integer, String> outResourcesForEachProductions = new HashMap<>();
        inResourcesForEachProductions.put(0, positionOfResForBasicSlot);
        inResourcesForEachProductions.put(1, positionOfResForProdSlot1);
        outResourcesForEachProductions.put(0, "purple");

        assertThrows(InvalidEventException.class, () -> new ProductionEventFromClient("first",inResourcesForEachProductions,outResourcesForEachProductions).doAction(modelInterface));

        // The Player can not do the Basic Production with two resources of the same color
        firstPlayer.getPersonalBoard().
                getWarehouse().addResourcesToStrongBox(new StorableResource(ResourcesEnum.PURPLE));

        assertThrows(InvalidEventException.class, () -> new ProductionEventFromClient("first",inResourcesForEachProductions,outResourcesForEachProductions).doAction(modelInterface));

        inResourcesForEachProductions.remove(0);
        outResourcesForEachProductions.remove(0);

        // The Player can not choose a white resource for a Basic Production
        inResourcesForEachProductions.put(0, positionOfResForProdSlot1);
        outResourcesForEachProductions.put(0, "white");

        assertThrows(InvalidEventException.class, () -> new ProductionEventFromClient("first",inResourcesForEachProductions,outResourcesForEachProductions).doAction(modelInterface));

        inResourcesForEachProductions.remove(0);
        outResourcesForEachProductions.remove(0);

        // The Player can not do a Production Action of a Development Card that is not placed
        inResourcesForEachProductions.put(2, positionOfResForProdSlot1);
        assertThrows(InvalidIndexException.class, () -> new ProductionEventFromClient("first",inResourcesForEachProductions,outResourcesForEachProductions).doAction(modelInterface));

    }

    @Test
    void invalidEventException() {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);

        //player is in the StartTurnState state so he cannot do the following midTurn actions:
        assertThrows(InvalidEventException.class, () -> modelInterface.placeDevelopmentCardAction(1));
        assertThrows(InvalidEventException.class, () -> modelInterface.placeResourceAction(new ArrayList<>(),true));
        assertThrows(InvalidEventException.class, () -> modelInterface.transformationAction(new ArrayList<>()));
        assertThrows(InvalidEventException.class, () -> modelInterface.placeDevelopmentCardAction(1));
        assertThrows(InvalidEventException.class, modelInterface::endTurn);
    }

    @Test
    void buyCardWithDoubleDiscount() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException, InvalidSetupException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(false);
        game.setMarketTrayAuto();
        game.setDevelopmentCardsGrid();
        List<Integer> marketLeaderIndexes = new ArrayList<>() {{
            add(12);//discount leaders:requires 1 green and 1 blue dev of any level----dicount gray
            add(13);//market leaders:requires 1 blue lv 1 and 1 purple dev of any level----discount blue
            add(0);
            add(1);
            add(2);
            add(3);
            add(7);
            add(8);//random leaders for other players
        }};
        game.setLeaderInHand(modelInterface, marketLeaderIndexes);
        //prepare for first leader

        //prepare for first devCard
        DevelopmentCard card1 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.BLUE, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card1);
        List<Integer> resourcePositions1 = new ArrayList<>();
        for (int i = 0; i < card1.getPrice().size(); i++) {
            resourcePositions1.add(i + strongBoxPositionsOffset);
        }
        //place blue lv1 in pos 1
        assertTrue(new BuyEventFromClient("first","blue",1,resourcePositions1).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",1).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //prepare for second devCard
        DevelopmentCard card2 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.GREEN, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card2);
        List<Integer> resourcePositions2 = new ArrayList<>();
        for (int i = 0; i < card2.getPrice().size(); i++) {
            resourcePositions2.add(i + strongBoxPositionsOffset);
        }
        //place green lv1 in pos 2
        assertTrue(new BuyEventFromClient("first","green",1,resourcePositions2).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",2).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //prepare for third devCard
        DevelopmentCard card3 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.PURPLE, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card3);
        List<Integer> resourcePositions3 = new ArrayList<>();
        for (int i = 0; i < card3.getPrice().size(); i++) {
            resourcePositions3.add(i + strongBoxPositionsOffset);
        }
        //place purple lv1 in pos 3
        assertTrue(new BuyEventFromClient("first","purple",1,resourcePositions3).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",3).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //activate leaders
        assertTrue(new LeaderHandEventFromClient("first","d1",false).doAction(modelInterface));
        assertTrue(new MarketEventFromClient("first",2).doAction(modelInterface));
        assertTrue(new PlaceResourcesEventFromClient("first",new ArrayList<>(),true).doAction(modelInterface));
        assertTrue(new LeaderHandEventFromClient("first","d2",false).doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        //prepare for discounted buy but with a card not discountable by this leaders
        DevelopmentCard card4 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.PURPLE, 2);
        game.preparePlayerForDevCard(modelInterface, 0, card4);
        List<Integer> resourcePositions4 = new ArrayList<>();
        for (int i = 0; i < card4.getPrice().size(); i++) {
            resourcePositions4.add(i + strongBoxPositionsOffset);
        }
        //place purple lv2 in pos 3
        assertTrue(new BuyEventFromClient("first","purple",2,resourcePositions4).doAction(modelInterface));
        assertTrue(new PlaceDevelopmentCardEventFromClient("first",3).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));
        game.roundOfNothing(modelInterface);

        DevelopmentCardsGrid dev = GameBoard.getGameBoard().getDevelopmentCardsGrid();

        //prepare for discounted buy but with a card discountable(price: x6 Gray res)
        DevelopmentCard card5 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorsEnum.YELLOW, 3);
        game.preparePlayerForDevCard(modelInterface, 0, card5);
        List<Integer> resourcePositions5 = new ArrayList<>();
        for (int i = 0; i < card5.getPrice().size(); i++) {
            resourcePositions5.add(i + strongBoxPositionsOffset);
        }
        //trying buying expecting no discount
        assertThrows(InvalidEventException.class,()->new BuyEventFromClient("first","yellow",3,resourcePositions5).doAction(modelInterface));

        //try to buy expecting too much discount
        resourcePositions5.remove(0);
        resourcePositions5.remove(0);
        assertThrows(InvalidEventException.class,()->new BuyEventFromClient("first","yellow",3,resourcePositions5).doAction(modelInterface));

        //buy with the right amount of discounted resources
        resourcePositions5.add(strongBoxPositionsOffset);
        assertTrue(new BuyEventFromClient("first","yellow",3,resourcePositions5).doAction(modelInterface));

        assertTrue(new PlaceDevelopmentCardEventFromClient("first",3).doAction(modelInterface));
        assertTrue(new EndTurnEventFromClient("first").doAction(modelInterface));

    }
    @Test
    void disconnectionTest() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        assertTrue(new MarketEventFromClient("first",5).canBeExecutedFor(modelInterface.getTurnLogic().getCurrentPlayer().getNickname()));
        assertFalse(new MarketEventFromClient("second",5).canBeExecutedFor(modelInterface.getTurnLogic().getCurrentPlayer().getNickname()));
        assertTrue(new MarketEventFromClient("first",5).doAction(modelInterface));
        //disconnect first in the middle of placing his resources
        modelInterface.disconnectPlayer("first");
        //check that the first player turn has been skipped
        assertEquals("second",modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        modelInterface.disconnectPlayer("second");
        modelInterface.disconnectPlayer("third");
        assertEquals("fourth",modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        //reconnect first and finish fourth turn
        modelInterface.reconnectPlayer("first");
        modelInterface.marketAction(2);
        modelInterface.placeResourceAction(new ArrayList<>(),true);
        modelInterface.endTurn();
        //check tha first is reconnected and his turn isn't skipped
        assertEquals("first",modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        //check that his turn restart form resource placement
        assertEquals(modelInterface.getWaitResourcePlacement(),modelInterface.getCurrentState());
    }
    @Test
    void disconnectionTestWithEvents() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        assertTrue(new MarketEventFromClient("first",5).doAction(modelInterface));
        //disconnect first in the middle of placing his resources
        assertTrue(new DisconnectEventFromClient("first").doAction(modelInterface));//return false as he is not the last one
        //check that the first player turn has been skipped
        assertEquals("second",modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        assertTrue(new DisconnectEventFromClient("second").doAction(modelInterface));
        assertTrue(new DisconnectEventFromClient("third").doAction(modelInterface));
        assertEquals("fourth",modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        //reconnect first and finish fourth turn
        assertTrue(new ReconnectEventFromClient("first").doAction(modelInterface));
        modelInterface.marketAction(2);
        modelInterface.placeResourceAction(new ArrayList<>(),true);
        modelInterface.endTurn();
        //check tha first is reconnected and his turn isn't skipped
        assertEquals("first",modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        //check that his turn restart form resource placement
        assertEquals(modelInterface.getWaitResourcePlacement(),modelInterface.getCurrentState());
    }

    @Test
    void cheatTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException, InvalidEventException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        assertTrue(new CheatEventFromClient("admin").doAction(modelInterface));
        for(Player player:modelInterface.getTurnLogic().getPlayers()){
            //check that every player now has 24 resources 6 of each storable color
            assertEquals(24,player.getPersonalBoard().getWarehouse().getAllResources().size());
            assertEquals(6,player.getPersonalBoard().getWarehouse().getAllResources().stream().filter(r->r.getColor()== ResourcesEnum.BLUE).count());
            assertEquals(6,player.getPersonalBoard().getWarehouse().getAllResources().stream().filter(r->r.getColor()== ResourcesEnum.GRAY).count());
            assertEquals(6,player.getPersonalBoard().getWarehouse().getAllResources().stream().filter(r->r.getColor()== ResourcesEnum.YELLOW).count());
            assertEquals(6,player.getPersonalBoard().getWarehouse().getAllResources().stream().filter(r->r.getColor()== ResourcesEnum.PURPLE).count());

        }
    }
}