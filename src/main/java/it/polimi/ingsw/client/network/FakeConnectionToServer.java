package it.polimi.ingsw.client.network;

import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.personal.FakeConnectionToClient;

public class FakeConnectionToServer extends ClientConnection {
    private FakeConnectionToClient fakeServerConnection;

    @Override
    public void sendMessage(String message) {
        fakeServerConnection.addMessageToQueue(message);
    }

    @Override
    public void sendStillAliveMsg() {}

    @Override
    public void close(boolean inform) {
        System.exit(0);
    }

    @Override
    public void run() {
        Lobby.getLobby().setNumberOfPlayers(1,null);
        ClientHandler clientHandler = new ClientHandler(this);
        clientHandler.run();
    }

    public void setFakeConnectionToClient(FakeConnectionToClient fakeConnectionToClient){
        this.fakeServerConnection = fakeConnectionToClient;
    }
}
