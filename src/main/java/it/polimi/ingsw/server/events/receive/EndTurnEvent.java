package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

public class EndTurnEvent extends ReceiveEvent{

    public EndTurnEvent(String nickname) {
        super(nickname);
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidEventException {
        return modelInterface.endTurn();
    }
}
