package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class StartTurnUpdateEvent implements ReceiveEvent{
    private String nextPlayer;
    @Override
    public void updateView(View view) {
        if(view.getNickname().equals(nextPlayer)){
            view.setOnYourTurn();
        }
        else {
            view.setOnNotYourTurn(nextPlayer);
        }
    }
}
