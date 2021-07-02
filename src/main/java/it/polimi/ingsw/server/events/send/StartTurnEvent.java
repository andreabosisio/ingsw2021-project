package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.utils.ServerParser;

import java.util.Arrays;

/**
 * Represent an Event that inform the Start Turn of the Player
 */
public class StartTurnEvent extends EventToClient {
    private final String type = ServerParser.START_TURN_TYPE;
    private transient final String[] receivers;
    private final String nextPlayer;

    public StartTurnEvent(String nextPlayer, String... receivers) {
        this.receivers = receivers;
        this.nextPlayer = nextPlayer;
    }

    @Override
    public boolean isForYou(String nickname) {
        if (receivers.length == 0) {
            return super.isForYou(nickname);
        }
        return Arrays.asList(receivers).contains(nickname);
    }
}
