package it.polimi.ingsw.server.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelInterface {
    List<Player> players = new ArrayList<Player>(){{
       add(new Player("Andrea"));
       add(new Player("Matteo"));
       add(new Player("Marco"));
    }};
    private TurnLogic turnLogic = new TurnLogic(players);


    public TurnLogic getTurnLogic() {
        return turnLogic;
    }

    public boolean marketAction(int arrowID) throws InvalidEventException, InvalidIndexException {
        return turnLogic.marketAction(arrowID);
    }
    public boolean productionAction(Map<Integer, List<Integer>> productionMap) throws InvalidEventException, InvalidIndexException, NonStorableResourceException {
        return turnLogic.productionAction(productionMap);
    }
    public boolean buyAction(String cardColor, int cardLevel, List<Integer> resourcesPositions) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        return turnLogic.buyAction(cardColor, cardLevel, resourcesPositions);
    }
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {
        return turnLogic.leaderAction(ID, discard);
    }
    public boolean placeResourceAction(List<Integer> swapPairs) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        return turnLogic.placeResourceAction(swapPairs);
    }
    public boolean placeDevCardAction(int slotPosition) throws InvalidEventException {
        return turnLogic.placeDevCardAction(slotPosition);
    }
    public boolean transformationAction(List<String> chosenColors) throws InvalidEventException, NonStorableResourceException {
        return turnLogic.transformationAction(chosenColors);
    }
    public boolean endTurn() throws InvalidEventException {
        return turnLogic.endTurn();
    }
}
