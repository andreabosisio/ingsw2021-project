package it.polimi.ingsw.server.model;

import it.polimi.ingsw.TestGameGenerator;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameBoard.DevelopmentCardsGrid;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;
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
    void buyDevelopmentCardTurnSimulation() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        DevelopmentCard card = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN, 1);
        List<Integer> resourcePositions = new ArrayList<>();
        for (int i = 0; i < card.getPrice().size(); i++) {
            resourcePositions.add(i + strongBoxPositionsOffset);
        }
        //check that trying to buy a non existing card generate an exception
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("ball", 1, resourcePositions));
        game.preparePlayerForDevCard(modelInterface, 0, card);
        //check that player is in start turn state
        assertEquals(modelInterface.getTurnLogic().getStartTurn(), modelInterface.getTurnLogic().getCurrentState());
        //check that trying to buy a card you can't place generate an exception
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("green", 2, resourcePositions));
        //check that trying to buy a card you can't afford generate an exception
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("blue", 1, resourcePositions));
        //perform buy action
        assertTrue(modelInterface.buyAction("green", 1, resourcePositions));
        //check that you can't perform another buy action
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("green", 1, resourcePositions));
        //check that player is now in waitDevCardPlacement state
        assertEquals(modelInterface.getTurnLogic().getWaitDevCardPlacement(), modelInterface.getTurnLogic().getCurrentState());
        //check that you can't perform a leader action in this State
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("m1", true));
        //check that a placement in an invalid position generate an exception
        assertThrows(InvalidEventException.class, () -> modelInterface.placeDevelopmentCardAction(0));
        //perform the placement action
        assertTrue(modelInterface.placeDevelopmentCardAction(1));
        //check that you can't place 2 times in a row
        assertThrows(InvalidEventException.class, () -> modelInterface.placeDevelopmentCardAction(1));
        //check that player is now in endTurn state
        assertEquals(modelInterface.getTurnLogic().getEndTurn(), modelInterface.getTurnLogic().getCurrentState());
        //check that first player has devCard in his PersonalBoard
        assertEquals(card, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getProductionCard(1));
        //end first player turn
        modelInterface.endTurn();
        //check that current player is now second in both modelInterface and turnLogic
        assertEquals("second", modelInterface.getCurrentPlayerNickname());
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
        currentPlayer.getPersonalBoard().setNewProductionCard(2, GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN, 1));

        //check that player is in start turn state
        assertEquals(modelInterface.getTurnLogic().getStartTurn(), modelInterface.getTurnLogic().getCurrentState());
        //player cannot satisfy requirements
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("p1", false));
        //check that player is in start turn state
        assertEquals(modelInterface.getTurnLogic().getStartTurn(), modelInterface.getTurnLogic().getCurrentState());

        DevelopmentCard requiredCard = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN, 2);
        game.preparePlayerForDevCard(modelInterface, 0, requiredCard);
        List<Integer> resourcePositions = new ArrayList<>();
        for (int i = 0; i < requiredCard.getPrice().size(); i++) {
            resourcePositions.add(i + strongBoxPositionsOffset);
        }
        assertTrue(modelInterface.buyAction("green", 2, resourcePositions));
        //check that player is now in waitDevCardPlacement state
        assertEquals(modelInterface.getTurnLogic().getWaitDevCardPlacement(), modelInterface.getTurnLogic().getCurrentState());
        //perform the placement action
        assertTrue(modelInterface.placeDevelopmentCardAction(2));

        //check that player is now in endTurn state
        assertEquals(modelInterface.getTurnLogic().getEndTurn(), modelInterface.getTurnLogic().getCurrentState());

        //now player has requirements. He's in endTurn state so he can make a leader action
        modelInterface.leaderAction("p1", false);
        //check if now the LeaderCard is activated
        assertEquals(currentPlayer.getPersonalBoard().getActiveLeaderCards().get(0).getID(), "p1");
        assertEquals(currentPlayer.getLeaderHand().stream().filter(card -> card.getID().equals("p1")).count(), 0);

        //check that current state is startTurn and current player is "second"
        assertEquals(modelInterface.getTurnLogic().getCurrentState(), modelInterface.getTurnLogic().getStartTurn());
        assertEquals(modelInterface.getCurrentPlayerNickname(), "second");
        assertEquals(modelInterface.getTurnLogic().getCurrentPlayer().getNickname(), "second");

    }

    @Test
    void leaderActionDiscardTest() throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        Player currentPlayer = modelInterface.getTurnLogic().getCurrentPlayer();

        //cannot discard a LeaderCard which is not in the hand of the player
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("d1", true));

        //discard first leader card by currentPlayer's hand
        modelInterface.leaderAction("p1", true);
        //check FaithProgress
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(currentPlayer).getFaithMarker());
        //cannot do another leaderAction
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("m1", true));
        //check that current state is startTurn because Player has to do an action
        assertEquals(modelInterface.getTurnLogic().getCurrentState(), modelInterface.getTurnLogic().getStartTurn());

        //market action
        modelInterface.marketAction(0);
        assertEquals(modelInterface.getTurnLogic().getCurrentState(), modelInterface.getTurnLogic().getWaitResourcePlacement());
        modelInterface.placeResourceAction(new ArrayList<>(),true);

        //currentState: EndTurnState. Player can do another leaderAction
        assertEquals(modelInterface.getTurnLogic().getCurrentState(), modelInterface.getTurnLogic().getEndTurn());
        //cannot discard a LeaderCard which is not in the hand of the player
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("w1", true));
        //cannot satisfy requirements to activate "m1"
        assertThrows(InvalidEventException.class, () -> modelInterface.leaderAction("m1", false));

        //cannot do other action but leaderAction
        assertThrows(InvalidEventException.class, () -> modelInterface.marketAction(2));

        //discard "m1"
        modelInterface.leaderAction("m1", true);

        //currentState: StartTurnState.
        assertEquals(modelInterface.getTurnLogic().getCurrentState(), modelInterface.getTurnLogic().getStartTurn());
    }

    @Test
    void marketActionWithNoPlacementTurnSimulation() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        //check that market action with an invalid arrowId generate an exception
        assertThrows(InvalidIndexException.class, () -> modelInterface.marketAction(12));
        //perform a market action
        modelInterface.marketAction(2);
        //check that invalid swap generate an exception
        List<Integer> swapPairs = new ArrayList<Integer>() {{
            add(1);
            add(5);
        }};
        assertThrows(InvalidEventException.class, () -> modelInterface.placeResourceAction(swapPairs,true));
        //expected res are white/white/white/red so a legal swap pair is an empty one
        modelInterface.placeResourceAction(new ArrayList<>(),true);
        //check that the red resource has increased the player's faith by one
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(0)).getFaithMarker());
        modelInterface.endTurn();
    }

    @Test
    void marketActionTurnSimulation() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        //perform a market action
        modelInterface.marketAction(1);//generate yellow/yellow/purple/purple
        //check that invalid swap generate an exception
        List<Integer> swapPairs = new ArrayList<Integer>() {{
            add(0);
            add(32);
        }};
        assertThrows(InvalidEventException.class, () -> modelInterface.placeResourceAction(swapPairs,true));
        //check that giving odd number of swap generate an exception
        List<Integer> swapPairs2 = new ArrayList<Integer>() {{
            add(0);
            add(4);
            add(1);
            add(6);
            add(2);
            add(7);
            add(8);
        }};
        assertThrows(InvalidEventException.class, () -> modelInterface.placeResourceAction(swapPairs2,true));
        //check that ending swap in an illegal configuration remains in waitResourcePlacement state
        swapPairs2.remove(6);
        assertThrows(InvalidEventException.class, () -> modelInterface.placeResourceAction(swapPairs2,true));
        assertEquals(modelInterface.getTurnLogic().getWaitResourcePlacement(), modelInterface.getTurnLogic().getCurrentState());
        //check that a series of swap that ends in a legal position moves state to endTurn
        swapPairs2.clear();
        swapPairs2.add(4);
        swapPairs2.add(5);
        assertTrue(modelInterface.placeResourceAction(swapPairs2,true));
        assertEquals(modelInterface.getTurnLogic().getEndTurn(), modelInterface.getTurnLogic().getCurrentState());
        //check that discarding one of the resources from market increased all other player's faith by 1
        assertEquals(0, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(0)).getFaithMarker());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(1)).getFaithMarker());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(2)).getFaithMarker());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getTurnLogic().getPlayers().get(3)).getFaithMarker());
    }

    @Test
    void marketActionWithMultiTransformationSimulation() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException, NonStorableResourceException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(false);
        game.setMarketTrayAuto(modelInterface);
        game.setDevelopmentCardsGrid(modelInterface);
        List<Integer> marketLeaderIndexes = new ArrayList<Integer>() {{
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
        DevelopmentCard card = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.YELLOW, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card);
        List<Integer> resourcePositions = new ArrayList<>();
        for (int i = 0; i < card.getPrice().size(); i++) {
            resourcePositions.add(i + strongBoxPositionsOffset);
        }
        //place yellow lv1 in pos 1
        modelInterface.buyAction("yellow", 1, resourcePositions);
        modelInterface.placeDevelopmentCardAction(1);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare for second devCard
        DevelopmentCard card2 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.YELLOW, 2);
        game.preparePlayerForDevCard(modelInterface, 0, card2);
        List<Integer> resourcePositions2 = new ArrayList<>();
        for (int i = 0; i < card2.getPrice().size(); i++) {
            resourcePositions2.add(i + strongBoxPositionsOffset);
        }
        //place yellow lv2 in pos 1
        modelInterface.buyAction("yellow", 2, resourcePositions2);
        modelInterface.placeDevelopmentCardAction(1);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare for third devCard
        DevelopmentCard card3 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.BLUE, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card3);
        List<Integer> resourcePositions3 = new ArrayList<>();
        for (int i = 0; i < card3.getPrice().size(); i++) {
            resourcePositions3.add(i + strongBoxPositionsOffset);
        }
        //place blue lv1 in pos 2
        modelInterface.buyAction("blue", 1, resourcePositions3);
        modelInterface.placeDevelopmentCardAction(2);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);
        //activate first Leader
        modelInterface.leaderAction("m1", false);
        //test transformation with one leader
        modelInterface.marketAction(2);//expected res are(3white and 1 red)
        //market action has produced 3 purple res because there is only one active marketLeader
        List<Integer> swap = new ArrayList<Integer>() {{
            add(0);
            add(7);
            add(1);
            add(8);
            add(2);
            add(9);
        }};
        modelInterface.placeResourceAction(swap,true);
        //check that player has 3 resources and they are all purple
        assertEquals(3, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().size());
        assertEquals(ResourceEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(0).getColor());
        assertEquals(ResourceEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(1).getColor());
        assertEquals(ResourceEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(2).getColor());
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare fourth devCard
        DevelopmentCard card4 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN, 2);
        game.preparePlayerForDevCard(modelInterface, 0, card4);
        List<Integer> resourcePositions4 = new ArrayList<>();
        for (int i = 0; i < card4.getPrice().size(); i++) {
            resourcePositions4.add(i + strongBoxPositionsOffset);
        }
        //place green lv2 in pos 2
        modelInterface.buyAction("green", 2, resourcePositions4);
        modelInterface.placeDevelopmentCardAction(2);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare for fifth devCard
        DevelopmentCard card5 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card5);
        List<Integer> resourcePositions5 = new ArrayList<>();
        for (int i = 0; i < card5.getPrice().size(); i++) {
            resourcePositions5.add(i + strongBoxPositionsOffset);
        }
        //place green lv1 in pos 3
        modelInterface.buyAction("green", 1, resourcePositions5);
        modelInterface.placeDevelopmentCardAction(3);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare for sixth devCard
        DevelopmentCard card6 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.PURPLE, 2);
        game.preparePlayerForDevCard(modelInterface, 0, card6);
        List<Integer> resourcePositions6 = new ArrayList<>();
        for (int i = 0; i < card6.getPrice().size(); i++) {
            resourcePositions6.add(i + strongBoxPositionsOffset);
        }
        //place purple lv2 in pos 3
        modelInterface.buyAction("purple", 2, resourcePositions6);
        modelInterface.placeDevelopmentCardAction(3);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);
        //activate second leader
        modelInterface.leaderAction("m2", false);
        //try market action with 2 leaderCards
        modelInterface.marketAction(2);
        //check that giving less chosen color than needed (needed 3:given 2) throws and exception
        List<String> chosenColors = new ArrayList<String>() {{
            add("purple");
            add("blue");
        }};
        assertThrows(InvalidEventException.class, () -> modelInterface.transformationAction(chosenColors));
        //check that giving a wrong color generate an exception
        chosenColors.add("yellow");
        assertThrows(InvalidEventException.class, () -> modelInterface.transformationAction(chosenColors));
        //check that giving a non existing color generate an exception
        chosenColors.remove(2);
        chosenColors.add("blu");
        assertThrows(InvalidEventException.class, () -> modelInterface.transformationAction(chosenColors));
        //check with right input
        chosenColors.remove(2);
        chosenColors.add("blue");
        modelInterface.transformationAction(chosenColors);
        swap = new ArrayList<Integer>() {{
            add(1);
            add(5);
            add(2);
            add(6);
        }};
        modelInterface.placeResourceAction(swap,true);

        //check that player has 5 resources and they are 3 purple and 2 blue
        assertEquals(5, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().size());
        assertEquals(ResourceEnum.BLUE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(0).getColor());
        assertEquals(ResourceEnum.BLUE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(1).getColor());
        assertEquals(ResourceEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(2).getColor());
        assertEquals(ResourceEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(3).getColor());
        assertEquals(ResourceEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(4).getColor());
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);
        modelInterface.marketAction(3);
        chosenColors.clear();
        chosenColors.add("purple");
        modelInterface.transformationAction(chosenColors);
        swap.clear();
        swap.add(0);
        swap.add(4);
        modelInterface.placeResourceAction(swap,true);
        //check that player has 6 resources
        assertEquals(6, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().size());
        //check that they are 1 gray 2 blue and 3 purple
        assertEquals(ResourceEnum.GRAY, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(0).getColor());
        assertEquals(ResourceEnum.BLUE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(1).getColor());
        assertEquals(ResourceEnum.BLUE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(2).getColor());
        assertEquals(ResourceEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(3).getColor());
        assertEquals(ResourceEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(4).getColor());
        assertEquals(ResourceEnum.PURPLE, modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getWarehouse().getAllResources().get(5).getColor());

        modelInterface.endTurn();

    }

    @Test
    void productionActionTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException, NonStorableResourceException {
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
        resources.add(new StorableResource(ResourceEnum.YELLOW));
        resources.add(new StorableResource(ResourceEnum.GRAY));
        firstPlayer.getPersonalBoard().
                getWarehouse().addResourcesToStrongBox(resources);
        firstPlayer.getPersonalBoard().
                getWarehouse().addResourcesToStrongBox(resources.get(0));

        // Check that player is in start turn state
        assertEquals(modelInterface.getTurnLogic().getStartTurn(), modelInterface.getTurnLogic().getCurrentState());

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
        assertTrue(modelInterface.productionAction(inResourcesForEachProductions, outResourcesForEachProductions));
        // Check that you can't perform another production action
        assertThrows(InvalidEventException.class, () -> modelInterface.
                productionAction(inResourcesForEachProductions, outResourcesForEachProductions));

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
        assertEquals(ResourceEnum.BLUE, playerRes.get(0).getColor());
        assertEquals(ResourceEnum.PURPLE, playerRes.get(1).getColor());

        // Check that player is now in EndTurnState state
        assertEquals(modelInterface.getTurnLogic().getEndTurn(), modelInterface.getTurnLogic().getCurrentState());

        // End first player turn
        modelInterface.endTurn();
        // Check that current player is now second in both modelInterface and turnLogic
        assertEquals("second", modelInterface.getCurrentPlayerNickname());
        assertEquals("second", modelInterface.getTurnLogic().getCurrentPlayer().getNickname());
        // Check that player is in start turn
        assertEquals(modelInterface.getTurnLogic().getStartTurn(), modelInterface.getTurnLogic().getCurrentState());
        // The second player do his turn
        game.preparePlayerForDevCard(modelInterface, 1, GameBoard.getGameBoard().
                getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.PURPLE, 1));
        positionOfResForProdSlot1.clear();
        for (int i = 0; i < GameBoard.getGameBoard().getDevelopmentCardsGrid().
                getCardByColorAndLevel(CardColorEnum.PURPLE, 1).getPrice().size(); i++) {
            positionOfResForProdSlot1.add(i + strongBoxPositionsOffset);
        }
        assertTrue(modelInterface.buyAction("purple", 1, positionOfResForProdSlot1));
        assertTrue(modelInterface.placeDevelopmentCardAction(1));
        modelInterface.endTurn();
        // The third player do his turn
        game.preparePlayerForDevCard(modelInterface, 2, GameBoard.getGameBoard().
                getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.PURPLE, 1));
        positionOfResForProdSlot1.clear();
        for (int i = 0; i < GameBoard.getGameBoard().getDevelopmentCardsGrid().
                getCardByColorAndLevel(CardColorEnum.PURPLE, 1).getPrice().size(); i++) {
            positionOfResForProdSlot1.add(i + strongBoxPositionsOffset);
        }
        assertTrue(modelInterface.buyAction("purple", 1, positionOfResForProdSlot1));
        assertTrue(modelInterface.placeDevelopmentCardAction(1));
        modelInterface.endTurn();
        // The fourth player do his turn
        game.preparePlayerForDevCard(modelInterface, 3, GameBoard.getGameBoard().
                getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.PURPLE, 1));
        positionOfResForProdSlot1.clear();
        for (int i = 0; i < GameBoard.getGameBoard().getDevelopmentCardsGrid().
                getCardByColorAndLevel(CardColorEnum.PURPLE, 1).getPrice().size(); i++) {
            positionOfResForProdSlot1.add(i + strongBoxPositionsOffset);
        }
        assertTrue(modelInterface.buyAction("purple", 1, positionOfResForProdSlot1));
        assertTrue(modelInterface.placeDevelopmentCardAction(1));
        modelInterface.endTurn();
        // The first Player has seven Development Card and the last Player do his turn, so the game is over
        assertEquals(modelInterface.getTurnLogic().getEndGame(), modelInterface.getTurnLogic().getCurrentState());
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
                getWarehouse().addResourcesToStrongBox(new StorableResource(ResourceEnum.PURPLE));

        // The Player can not do the Basic Production with only one resource
        Map<Integer, List<Integer>> inResourcesForEachProductions = new HashMap<>();
        Map<Integer, String> outResourcesForEachProductions = new HashMap<>();
        inResourcesForEachProductions.put(0, positionOfResForBasicSlot);
        inResourcesForEachProductions.put(1, positionOfResForProdSlot1);
        outResourcesForEachProductions.put(0, "purple");

        assertThrows(NullPointerException.class,
                () -> modelInterface.productionAction(inResourcesForEachProductions, outResourcesForEachProductions));

        // The Player can not do the Basic Production with two resources of the same color
        firstPlayer.getPersonalBoard().
                getWarehouse().addResourcesToStrongBox(new StorableResource(ResourceEnum.PURPLE));

        assertThrows(InvalidEventException.class,
                () -> modelInterface.productionAction(inResourcesForEachProductions, outResourcesForEachProductions));

        inResourcesForEachProductions.remove(0);
        outResourcesForEachProductions.remove(0);

        // The Player can not choose a white resource for a Basic Production
        inResourcesForEachProductions.put(0, positionOfResForProdSlot1);
        outResourcesForEachProductions.put(0, "white");

        assertThrows(InvalidEventException.class,
                () -> modelInterface.productionAction(inResourcesForEachProductions, outResourcesForEachProductions));

        inResourcesForEachProductions.remove(0);
        outResourcesForEachProductions.remove(0);

        // The Player can not do a Production Action of a Development Card that is not placed
        inResourcesForEachProductions.put(2, positionOfResForProdSlot1);
        assertThrows(InvalidIndexException.class,
                () -> modelInterface.productionAction(inResourcesForEachProductions, outResourcesForEachProductions));
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
    void buyCardWithDoubleDiscount() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(false);
        game.setMarketTrayAuto(modelInterface);
        game.setDevelopmentCardsGrid(modelInterface);
        List<Integer> marketLeaderIndexes = new ArrayList<Integer>() {{
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
        DevelopmentCard card1 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.BLUE, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card1);
        List<Integer> resourcePositions1 = new ArrayList<>();
        for (int i = 0; i < card1.getPrice().size(); i++) {
            resourcePositions1.add(i + strongBoxPositionsOffset);
        }
        //place blue lv1 in pos 1
        modelInterface.buyAction("blue", 1, resourcePositions1);
        modelInterface.placeDevelopmentCardAction(1);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare for second devCard
        DevelopmentCard card2 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card2);
        List<Integer> resourcePositions2 = new ArrayList<>();
        for (int i = 0; i < card2.getPrice().size(); i++) {
            resourcePositions2.add(i + strongBoxPositionsOffset);
        }
        //place green lv1 in pos 2
        modelInterface.buyAction("green", 1, resourcePositions2);
        modelInterface.placeDevelopmentCardAction(2);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //prepare for third devCard
        DevelopmentCard card3 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.PURPLE, 1);
        game.preparePlayerForDevCard(modelInterface, 0, card3);
        List<Integer> resourcePositions3 = new ArrayList<>();
        for (int i = 0; i < card3.getPrice().size(); i++) {
            resourcePositions3.add(i + strongBoxPositionsOffset);
        }
        //place purple lv1 in pos 3
        modelInterface.buyAction("purple", 1, resourcePositions3);
        modelInterface.placeDevelopmentCardAction(3);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        //activate leaders
        modelInterface.leaderAction("d1",false);
        modelInterface.marketAction(2);
        modelInterface.placeResourceAction(new ArrayList<>(),true);
        modelInterface.leaderAction("d2",false);
        game.roundOfNothing(modelInterface);

        //prepare for discounted buy but with a card not discountable by this leaders
        DevelopmentCard card4 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.PURPLE, 2);
        game.preparePlayerForDevCard(modelInterface, 0, card4);
        List<Integer> resourcePositions4 = new ArrayList<>();
        for (int i = 0; i < card4.getPrice().size(); i++) {
            resourcePositions4.add(i + strongBoxPositionsOffset);
        }
        //place purple lv2 in pos 3
        modelInterface.buyAction("purple", 2, resourcePositions4);
        modelInterface.placeDevelopmentCardAction(3);
        modelInterface.endTurn();
        game.roundOfNothing(modelInterface);

        DevelopmentCardsGrid dev = GameBoard.getGameBoard().getDevelopmentCardsGrid();

        //prepare for discounted buy but with a card discountable(price: x6 Gray res)
        DevelopmentCard card5 = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.YELLOW, 3);
        game.preparePlayerForDevCard(modelInterface, 0, card5);
        List<Integer> resourcePositions5 = new ArrayList<>();
        for (int i = 0; i < card5.getPrice().size(); i++) {
            resourcePositions5.add(i + strongBoxPositionsOffset);
        }
        //trying buying expecting no discount
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("yellow", 3, resourcePositions5));

        //try to buy expecting too much discount
        resourcePositions5.remove(0);
        resourcePositions5.remove(0);
        assertThrows(InvalidEventException.class, () -> modelInterface.buyAction("yellow", 3, resourcePositions5));

        //buy with the right amount of discounted resources
        resourcePositions5.add(strongBoxPositionsOffset);
        assertTrue(modelInterface.buyAction("yellow", 3, resourcePositions5));
        assertTrue(modelInterface.placeDevelopmentCardAction(3));
        modelInterface.endTurn();
    }
    @Test
    void disconnectionTest() throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        modelInterface.marketAction(5);
        //disconnect first in the middle of placing his resources
        modelInterface.disconnectPlayer("first");
        //check that the first player turn has been skipped
        assertEquals("second",modelInterface.getCurrentPlayerNickname());
        modelInterface.disconnectPlayer("second");
        modelInterface.disconnectPlayer("third");
        assertEquals("fourth",modelInterface.getCurrentPlayerNickname());
        //reconnect first and finish fourth turn
        modelInterface.reconnectPlayer("first");
        modelInterface.marketAction(2);
        modelInterface.placeResourceAction(new ArrayList<>(),true);
        modelInterface.endTurn();
        //check tha first is reconnected and his turn isn't skipped
        assertEquals("first",modelInterface.getCurrentPlayerNickname());
        //check that his turn restart form resource placement
        assertEquals(modelInterface.getTurnLogic().getWaitResourcePlacement(),modelInterface.getTurnLogic().getCurrentState());
    }
}