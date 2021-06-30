package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public interface EventFromServer {
    void updateView(View view);
}
