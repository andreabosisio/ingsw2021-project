package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public interface ReceiveEvent extends Runnable{
    void updateView(View view);
}
