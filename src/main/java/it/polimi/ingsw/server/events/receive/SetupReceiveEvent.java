package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;

public class SetupReceiveEvent extends ReceiveEvent{
    private final List<Integer> chosenLeaderCardIndexes;
    private final List<String> chosenResources;

    public SetupReceiveEvent(String sender, List<Integer> chosenLeaderCardIndexes, List<String> chosenResources) {
        super(sender);
        this.chosenLeaderCardIndexes = chosenLeaderCardIndexes;
        this.chosenResources = chosenResources;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidEventException, NonStorableResourceException {
        return modelInterface.setupAction(getNickname(), chosenLeaderCardIndexes, chosenResources);
    }
}