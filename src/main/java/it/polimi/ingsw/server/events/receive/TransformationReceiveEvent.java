package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;

public class TransformationReceiveEvent extends ReceiveEvent {
    private final List<String> chosenResources;

    public TransformationReceiveEvent(String nickname, List<String> chosenResources) {
        super(nickname);
        this.chosenResources = chosenResources;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidEventException, NonStorableResourceException {
        return modelInterface.transformationAction(chosenResources);
    }
}
