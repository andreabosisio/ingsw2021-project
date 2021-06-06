package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;
import java.util.Map;

public class ProductionEvent extends ReceiveEvent {
    private final Map<Integer, List<Integer>> inResourcesForEachProductions;
    private final Map<Integer, String> outResourcesForEachProductions;
    public ProductionEvent(String nickname, Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) {
        super(nickname);
        this.inResourcesForEachProductions = inResourcesForEachProductions;
        this.outResourcesForEachProductions = outResourcesForEachProductions;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.productionAction(inResourcesForEachProductions, outResourcesForEachProductions);
    }
}
