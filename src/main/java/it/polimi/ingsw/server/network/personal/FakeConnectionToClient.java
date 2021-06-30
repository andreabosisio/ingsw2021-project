package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.client.network.FakeConnectionToServer;
import it.polimi.ingsw.server.network.PongObserver;

public class FakeConnectionToClient extends ServerConnection {

    private final FakeConnectionToServer fakeClientConnection;
    private PongObserver pongObserver;

    public FakeConnectionToClient(FakeConnectionToServer fakeClientConnection) {
        this.fakeClientConnection = fakeClientConnection;
    }

    @Override
    public void sendMessage(String message) {
        this.fakeClientConnection.addMessageToQueue(message);
    }

    @Override
    public void setPongObserver(PongObserver pongObserver) {
        this.pongObserver = pongObserver;
    }

    @Override
    public void close(boolean inform) { }

    @Override
    public void sendStillAliveMsg() {
        pongObserver.disablePingPong();
    }

}
