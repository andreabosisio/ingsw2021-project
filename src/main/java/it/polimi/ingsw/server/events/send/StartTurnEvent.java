package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;
import java.util.Arrays;


public class StartTurnEvent extends SendEvent{
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
            return super.isForYou(nickname);
        }
        return Arrays.asList(receivers).contains(nickname);
    }
}
