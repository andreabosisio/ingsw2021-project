package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

/**
 * Represent the request of a Leader Action by a Player (from the Client).
 */
public class LeaderHandEvent extends ReceiveEvent {
    private final String leaderCardID;
    private final boolean discardCard;

    /**
     * Create a new Leader Action request by specifying the Leader Card's ID and the action.
     *
     * @param nickname of the Player who wants to perform aa Leader Action
     * @param leaderCardID The chosen Leader Card's ID
     * @param discardCard true to discard the chosen Leader Card or false to activate it
     */
    public LeaderHandEvent(String nickname, String leaderCardID, boolean discardCard) {
        super(nickname);
        this.leaderCardID = leaderCardID;
        this.discardCard = discardCard;
    }

    /**
     * Executes the action.
     *
     * @param modelInterface The reference of the game model
     * @return true if the action has been correctly executed
     * @throws EmptySlotException           if an empty resource slot has been accessed
     * @throws NonAccessibleSlotException   if a non accessible resource slot has been accessed
     * @throws InvalidEventException        if it's an invalid action
     * @throws InvalidIndexException        if an index it's out of range
     * @throws InvalidSetupException        if it's an invalid setup event
     * @throws NonStorableResourceException if a Non Storable Resource has been requested
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException {
        return super.doAction(modelInterface);
    }
}
