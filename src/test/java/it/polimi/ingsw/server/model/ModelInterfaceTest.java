package it.polimi.ingsw.server.model;

import it.polimi.ingsw.TestGameGenerator;
import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelInterfaceTest {
    @Test
    void buyDevelopmentCardTurnSimulation() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException, InvalidEventException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(true);
        DevelopmentCard card = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(CardColorEnum.GREEN,1);
        List<Integer> positions = new ArrayList<>();
        for(int i = 0;i<card.getPrice().size();i++){
            positions.add(i+14);
        }
        game.preparePlayerForDevCard(modelInterface,0,card);
        //check that player is in start turn state
        assertEquals(modelInterface.getTurnLogic().getStartTurn(),modelInterface.getTurnLogic().getCurrentState());
        //perform buy action
        modelInterface.buyAction("green",1,positions);
        //check that you can't perform another buy action
        assertThrows(InvalidEventException.class,()->modelInterface.buyAction("green",1,positions));
        //check that player is now in waitDevCardPlacement state
        assertEquals(modelInterface.getTurnLogic().getWaitDevCardPlacement(),modelInterface.getTurnLogic().getCurrentState());
        //check that a placement in an invalid position generate an exception
        assertThrows(InvalidEventException.class,()->modelInterface.placeDevCardAction(0));
        //perform the placement action
        modelInterface.placeDevCardAction(1);
        //check that you can't place 2 times in a row
        assertThrows(InvalidEventException.class,()->modelInterface.placeDevCardAction(1));
        //check that player is now in endTurn state
        assertEquals(modelInterface.getTurnLogic().getEndTurn(),modelInterface.getTurnLogic().getCurrentState());
        //check that first player has devCard in his PersonalBoard
        assertEquals(card,modelInterface.getTurnLogic().getCurrentPlayer().getPersonalBoard().getProductionCard(1));
        //end first player turn
        modelInterface.endTurn();
        //check that current player is now secondo in both modelInterface and turnLogic
        assertEquals("second",modelInterface.getCurrentPlayer());
        assertEquals("second",modelInterface.getTurnLogic().getCurrentPlayer().getNickName());
        //check that player is in start turn
        assertEquals(modelInterface.getTurnLogic().getStartTurn(),modelInterface.getTurnLogic().getCurrentState());
        //check that endTurn is not allowed in startTurn
        assertThrows(InvalidEventException.class, modelInterface::endTurn);
    }
}