package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.events.send.SendEvent;

public abstract class GraphicsUpdateEvent implements SendEvent {
    private final String type;

    public GraphicsUpdateEvent(String type) {
        this.type = type;
    }

    @Override
    public boolean isForYou(String nickname) {
        return true;
    }

    @Override
    public String toJson() {
        return null;
    }
}
