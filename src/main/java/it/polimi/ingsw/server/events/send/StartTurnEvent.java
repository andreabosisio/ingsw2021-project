package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;
import java.util.Arrays;


public class StartTurnEvent implements SendEvent{
    private final String type = "startTurn";
    private final String[] receivers;
    private final String nextPlayer;

    public StartTurnEvent(String nextPlayer,String... receivers) {
        this.receivers = receivers;
        this.nextPlayer = nextPlayer;
    }

    @Override
    public boolean isForYou(String nickname) {
        if(receivers.length==0){
            return true;
        }
        return Arrays.asList(receivers).contains(nickname);
    }

    /**
     * Transform this SendEvent to a String containing a JSON message.
     *
     * @return the produced String
     */
    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }
}
