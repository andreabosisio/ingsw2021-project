package it.polimi.ingsw.client.network;

import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.personal.FakeConnectionToClient;

/**
 * This class implements a Fake Connection with the Server,
 * instead of send Messages through the Socket it calls directly the method of the FakeConnectionToClient
 */
public class FakeConnectionToServer extends ClientConnection {
    private FakeConnectionToClient fakeServerConnection;

    /**
     * Calls the method of the FakeConnectionToClient to add a message to his queue
     *
     * @param message String containing the message
     */
    @Override
    public void sendMessage(String message) {
        fakeServerConnection.addMessageToQueue(message);
    }

    @Override
    public void sendStillAliveMsg() {
    }

    /**
     * This method closes the Application
     *
     * @param inform not used
     */
    @Override
    public void close(boolean inform) {
        System.exit(0);
    }

    /**
     * This method set directly the number of Players to 1 and
     * creates a new ClientHandler for a local Client.
     */
    @Override
    public void run() {
        Lobby.getLobby().setNumberOfPlayers(1, null);
        ClientHandler clientHandler = new ClientHandler(this);
        clientHandler.run();
    }

    /**
     * This method set the FakeConnectionToClient
     *
     * @param fakeConnectionToClient is the Object to set
     */
    public void setFakeConnectionToClient(FakeConnectionToClient fakeConnectionToClient) {
        this.fakeServerConnection = fakeConnectionToClient;
    }
}
