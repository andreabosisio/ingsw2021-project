package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.List;

/**
 * Implementation of the first setup choice of the game when the Player should choose between 2 Leader Cards and
 * 0 or 1 or 2 Resources.
 */
public class SetupEventFromClient extends EventFromClient {
    private final List<Integer> chosenLeaderCardIndexes;
    private final List<String> chosenResources;

    /**
     * Create a Setup Event by specifying the choices.
     *
     * @param sender The nickname of the Player who performed the Setup Choice
     * @param chosenLeaderCardIndexes The indexes of the Chosen Leader Cards
     * @param chosenResources The color of the Chosen Resources
     */
    public SetupEventFromClient(String sender, List<Integer> chosenLeaderCardIndexes, List<String> chosenResources) {
        super(sender, ServerParser.setupActionType);
        this.chosenLeaderCardIndexes = chosenLeaderCardIndexes;
        this.chosenResources = chosenResources;
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
        return modelInterface.setupAction(getNickname(), chosenLeaderCardIndexes, chosenResources);
    }

    /**
     * A setup event can always be executed.
     *
     * @param currentPlayerNickname The nickname of the current Player
     * @return true
     */
    @Override
    public boolean canBeExecutedFor(String currentPlayerNickname) {
        return true;
    }
}