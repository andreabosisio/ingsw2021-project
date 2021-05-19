package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;

public class StartTurnEvent implements SendEvent{
    private final String type = "startTurn";
    private final boolean reSend;
    private final String nextPlayer;

    public StartTurnEvent(String nextPlayer,boolean reSend) {
        this.reSend = reSend;
        this.nextPlayer = nextPlayer;
    }

    @Override
    public boolean isForYou(String nickname) {
        if(reSend){
            return nickname.equals(this.nextPlayer);
        }
        return true;
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
