package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.InvalidEventException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Represent the request of a Leader Action by a Player (from the Client).
 */
public class LeaderHandEventFromClient extends EventFromClient {
    private final String leaderCardID;
    private final boolean discardCard;

    /**
     * Create a new Leader Action request by specifying the Leader Card's ID and the action.
     *
     * @param nickname     of the Player who wants to perform aa Leader Action
     * @param leaderCardID The chosen Leader Card's ID
     * @param discardCard  true to discard the chosen Leader Card or false to activate it
     */
    public LeaderHandEventFromClient(String nickname, String leaderCardID, boolean discardCard) {
        super(nickname, ServerParser.LEADER_ACTION_TYPE);
        this.leaderCardID = leaderCardID;
        this.discardCard = discardCard;
    }

    /**
     * Executes the action.
     *
     * @param modelInterface The reference of the game model
     * @return true if the action has been correctly executed
     * @throws InvalidEventException        if it's an invalid action
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidEventException {
        return modelInterface.leaderAction(leaderCardID, discardCard);
    }
}
