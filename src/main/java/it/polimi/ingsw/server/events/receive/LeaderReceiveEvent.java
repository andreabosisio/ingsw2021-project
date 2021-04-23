package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

public class LeaderReceiveEvent extends ReceiveEvent {
    private final String leaderCardID;
    private final boolean discardCard;
    public LeaderReceiveEvent(String nickname, String leaderCardID, boolean discardCard) {
        super(nickname);
        this.leaderCardID = leaderCardID;
        this.discardCard = discardCard;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.leaderAction(leaderCardID, discardCard);
    }
}
