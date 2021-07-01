package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

/**
 * Represent the request of the choice of the Number of the Players of the Game by a Player
 */
public class SelectNumberPlayersEvent extends EventToServer {

    private final Integer size;

    /**
     * This event is sent after a Player chooses the size of the Lobby
     *
     * @param size is the Integer that represents the number of the Players
     */
    public SelectNumberPlayersEvent(Integer size) {
        super(ClientParser.LOBBY_CHOICE_TYPE);
        this.size = size;
    }
}
