package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

public abstract class ReceiveEvent {
    private final String sender;

    public ReceiveEvent(String sender) {
        this.sender = sender;
    }

    public String getNickname() {
        return sender;
    }

    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return false;
    }
}
