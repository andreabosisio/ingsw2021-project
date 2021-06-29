package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.exceptions.*;

import java.util.List;
import java.util.Map;

public abstract class State {
    final TurnLogic turnLogic;
    public State(TurnLogic turnLogic){this.turnLogic = turnLogic;}

    public boolean marketAction(int arrowID) throws InvalidEventException, InvalidIndexException {throw new InvalidEventException("Invalid action right now");}
    public boolean productionAction(Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) throws InvalidEventException, InvalidIndexException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException { throw new InvalidEventException("Invalid action right now"); }
    public boolean buyAction(String cardColor, int cardLevel, List<Integer> resourcePositions) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {throw new InvalidEventException("Invalid action right now");}
    public boolean leaderAction(String ID,boolean discard) throws InvalidEventException {throw new InvalidEventException("Invalid action right now");}
    public boolean placeResourceAction(List<Integer> swapPairs,boolean isFinal) throws InvalidEventException {throw new InvalidEventException("Invalid action right now");}
    public boolean transformationAction(List<String> chosenColors) throws InvalidEventException, NonStorableResourceException {throw new InvalidEventException("Invalid action right now");}
    public boolean placeDevelopmentCardAction(int slotPosition) throws InvalidEventException {throw new InvalidEventException("Invalid action right now");}
    public boolean endTurn() throws InvalidEventException {throw new InvalidEventException("Invalid action right now");}
}
