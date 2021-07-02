package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.InvalidEventException;
import it.polimi.ingsw.server.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.List;

/**
 * Represent the choice of a White Resource Transformation by a Player (from the Client).
 */
public class TransformationEventFromClient extends EventFromClient {
    private final List<String> chosenResources;

    /**
     * Set the White Resource Transformation information.
     *
     * @param nickname        of the Player who did the White Resource Transformation
     * @param chosenResources The color of the chosen Resources as result of the Transformation
     */
    public TransformationEventFromClient(String nickname, List<String> chosenResources) {
        super(nickname, ServerParser.TRANSFORMATION_ACTION_TYPE);
        this.chosenResources = chosenResources;
    }

    /**
     * Executes the action.
     *
     * @param modelInterface The reference of the game model
     * @return true if the action has been correctly executed
     * @throws InvalidEventException        if it's an invalid action
     * @throws NonStorableResourceException if a Non Storable Resource has been requested
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidEventException, NonStorableResourceException {
        return modelInterface.transformationAction(chosenResources);
    }
}
