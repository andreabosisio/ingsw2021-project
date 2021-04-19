package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;
import java.util.Map;

public class ProductionReceiveEvent extends ReceiveEvent {
    private final Map<Integer, List<Integer>> productionMap;
    public ProductionReceiveEvent(String nickName, Map<Integer, List<Integer>> productionMap) {
        super(nickName);
        this.productionMap=productionMap;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.productionAction(productionMap);
    }
}
