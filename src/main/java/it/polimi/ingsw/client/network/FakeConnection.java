package it.polimi.ingsw.client.network;

import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.personal.FakeConnectionToClient;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FakeConnection implements Connection{
    private final BlockingQueue<String> messagesFromServer = new LinkedBlockingQueue<>();
    private FakeConnectionToClient fakeServerConnection;

    @Override
    public String getMessage() {
        String message = null;
        try {
            message = messagesFromServer.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            close(false);
        }
        return message;
    }

    @Override
    public void sendMessage(String message) {
        fakeServerConnection.addMessageToQueue(message);
    }

    @Override
    public void close(boolean inform) {
        System.exit(0);
    }

    @Override
    public void run() {
        Lobby.getLobby().setNumberOfPlayers(1,null);
        ClientHandler c = new ClientHandler(this);
        c.run();
    }

    public void addMessageToQueue(String message){
        messagesFromServer.add(message);
    }

    public void setFakeConnectionToClient(FakeConnectionToClient fakeConnectionToClient){
        this.fakeServerConnection = fakeConnectionToClient;
    }
}
