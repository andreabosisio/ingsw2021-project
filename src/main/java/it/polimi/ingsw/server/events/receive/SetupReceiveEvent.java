package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;

public class SetupReceiveEvent extends ReceiveEvent{
    private final List<Integer> leaderCardIndexes;
    private final List<String> resources;

    public SetupReceiveEvent(String nickName, List<Integer> leaderCardIndexes, List<String> resources) {
        super(nickName);
        this.leaderCardIndexes = leaderCardIndexes;
        this.resources = resources;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.setupAction(getNickName(),leaderCardIndexes,resources);
    }
}