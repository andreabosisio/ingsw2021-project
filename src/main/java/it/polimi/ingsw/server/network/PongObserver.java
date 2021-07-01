package it.polimi.ingsw.server.network;

public interface PongObserver {
    /**
     * This method is called to signal that a Pong response was received from the player
     */
    void pongUpdate();

    void disablePingPong();
}

