package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;

public class SetupReceiveEvent extends ReceiveEvent{
    private final List<Integer> chosenLeaderCardIndexes;
    private final List<String> chosenResources;

    public SetupReceiveEvent(String nickname, List<Integer> chosenLeaderCardIndexes, List<String> chosenResources) {
        super(nickname);
        this.chosenLeaderCardIndexes = chosenLeaderCardIndexes;
        this.chosenResources = chosenResources;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.setupAction(getNickname(), chosenLeaderCardIndexes, chosenResources);
    }
}