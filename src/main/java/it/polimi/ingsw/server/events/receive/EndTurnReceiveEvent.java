package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.server.model.ModelInterface;

public class EndTurnReceiveEvent extends ReceiveEvent{

    public EndTurnReceiveEvent(String nickname) {
        super(nickname);
    }
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidEventException {
        return modelInterface.endTurn();
    }
}
