package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public interface EventFromServer {
    /**
     * This method update the View of the arrive of this new event
     *
     * @param view the View that has to be update (GUI or CLI)
     */
    void updateView(View view);
}
