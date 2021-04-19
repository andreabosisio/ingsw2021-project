package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

public abstract class ReceiveEvent {
    private final String nickName;

    public ReceiveEvent(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return false;
    }
}
