package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.client.network.FakeConnectionToServer;
import it.polimi.ingsw.server.network.PongObserver;

/**
 * This class implements a Fake Connection with the Client,
 * instead of send Messages through the Socket it calls directly the method of the FakeConnectionToServer
 */
public class FakeConnectionToClient extends ServerConnection {

    private final FakeConnectionToServer fakeClientConnection;
    private PongObserver pongObserver;

    public FakeConnectionToClient(FakeConnectionToServer fakeClientConnection) {
        this.fakeClientConnection = fakeClientConnection;
    }

    /**
     * Calls the method of the FakeConnectionToServer to add a message to his queue
     *
     * @param message String containing the message
     */
    @Override
    public void sendMessage(String message) {
        this.fakeClientConnection.addMessageToQueue(message);
    }

    /**
     * Set the observer interested in the arrival of a pong message
     *
     * @param pongObserver observer of the pong
     */
    @Override
    public void setPongObserver(PongObserver pongObserver) {
        this.pongObserver = pongObserver;
    }

    @Override
    public void close(boolean inform) {
    }

    /**
     * This method calls directly the method of the Virtual View that disable the Ping Pong messages.
     */
    @Override
    public void sendStillAliveMsg() {
        pongObserver.disablePingPong();
    }

}
