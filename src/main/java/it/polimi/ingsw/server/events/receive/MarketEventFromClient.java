package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.InvalidEventException;
import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Represent the request of a Market Action by a Player (from the Client).
 */
public class MarketEventFromClient extends EventFromClient {
    private final int arrowID;

    /**
     * Create a new Market Action request by specifying the arrow's ID of the selected Market line.
     *
     * @param nickname of the Player who wants to perform aa Market Action
     * @param arrowID  The arrow's ID of the selected Market line
     */
    public MarketEventFromClient(String nickname, int arrowID) {
        super(nickname, ServerParser.MARKET_ACTION_TYPE);
        this.arrowID = arrowID;
    }

    /**
     * Executes the action.
     *
     * @param modelInterface The reference of the game model
     * @return true if the action has been correctly executed
     * @throws InvalidEventException        if it's an invalid action
     * @throws InvalidIndexException        if an index it's out of range
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException {
        return modelInterface.marketAction(arrowID);
    }
}
