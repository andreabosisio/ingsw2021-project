package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.*;

import java.util.List;
import java.util.Map;

public abstract class State {
    TurnLogic turnLogic;
    public State(TurnLogic turnLogic){
        this.turnLogic=turnLogic;
    }

    public boolean marketAction(int arrowID) throws InvalidEventException, InvalidIndexException, NonStorableResourceException {throw new InvalidEventException();}
    public boolean productionAction(Map<Integer,List<Integer>> productionMap) throws InvalidEventException, InvalidIndexException, NonStorableResourceException {
        throw new InvalidEventException();
    }
    public boolean buyAction(int cardGridIndex,List<Integer> positions) throws InvalidEventException {throw new InvalidEventException();}
    public boolean leaderAction(String ID,boolean discard) throws InvalidEventException {throw new InvalidEventException();}
    public boolean placeResourceAction(List<Integer> swapPairs) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {throw new InvalidEventException();}
    public boolean transformationAction(List<String> chosenColors) throws InvalidEventException, NonStorableResourceException {throw new InvalidEventException();}
    public boolean placeDevCardAction(int slotPosition) throws InvalidEventException {throw new InvalidEventException();}
    public boolean endTurn() throws InvalidEventException {throw new InvalidEventException();}
}
