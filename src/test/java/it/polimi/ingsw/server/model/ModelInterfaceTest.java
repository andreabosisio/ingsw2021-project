package it.polimi.ingsw.server.model;

import it.polimi.ingsw.TestGameGenerator;
import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
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
        game.preparePlayerForDevCard(modelInterface,0, card);
        //check that player is in start turn state
        assertEquals(modelInterface.getTurnLogic().getStartTurn(),modelInterface.getTurnLogic().getCurrentState());
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
}