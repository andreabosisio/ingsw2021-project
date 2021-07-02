package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

/**
 * It's the implementation of an action request by a Player.
 */
public abstract class EventFromClient {
    private final String sender;
    private final String type;

    /**
     * Create a new Event by giving the sender of the action request.
     *
     * @param sender The nickname of the Player who wants to perform an action
     * @param type   The type of the action
     */
    public EventFromClient(String sender, String type) {
        this.sender = sender;
        this.type = type;
    }

    /**
     * @return the Nickname of the sender of the action request
     */
    public String getNickname() {
        return sender;
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
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException {
        return true;
    }

    /**
     * Check if this action can be executed by checking if the sender is the current Player.
     *
     * @param currentPlayerNickname The nickname of the current Player
     * @return true if the sender is the current Player
     */
    public boolean canBeExecutedFor(String currentPlayerNickname) {
        return sender.equals(currentPlayerNickname);
    }
}
