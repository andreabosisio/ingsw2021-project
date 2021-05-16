package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class GraphicUpdateEvent implements ReceiveEvent{

    private View view;

    @Override
    public void updateView(View view) {
        this.view = view;
        run();
    }

    @Override
    public void run() {
        view.graphicUpdate();
    }
}
