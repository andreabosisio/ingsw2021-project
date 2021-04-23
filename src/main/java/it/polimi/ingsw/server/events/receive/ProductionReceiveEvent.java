package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;
import java.util.Map;

public class ProductionReceiveEvent extends ReceiveEvent {
    private final Map<Integer, List<Integer>> productionMapIN;
    private final Map<Integer, String> productionMapOUT;
    public ProductionReceiveEvent(String nickName, Map<Integer, List<Integer>> productionMapIN, Map<Integer, String> productionMapOUT) {
        super(nickName);
        this.productionMapIN = productionMapIN;
        this.productionMapOUT = productionMapOUT;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.productionAction(productionMapIN, productionMapOUT);
    }
}
