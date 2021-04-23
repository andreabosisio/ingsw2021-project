package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.ModelInterface;

public class MarketReceiveEvent extends ReceiveEvent {
    private final int arrowID;
    public MarketReceiveEvent(String nickname, int arrowID) {
        super(nickname);
        this.arrowID=arrowID;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException {
        return modelInterface.marketAction(arrowID);
    }
}
