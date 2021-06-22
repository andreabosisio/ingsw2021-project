package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.client.network.FakeConnection;
import it.polimi.ingsw.server.network.PongObserver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FakeConnectionToClient implements Connection{

    private final FakeConnection fakeClientConnection;
    private PongObserver pongObserver;
    private final BlockingQueue<String> messagesFromClient = new LinkedBlockingQueue<>();

    public FakeConnectionToClient(FakeConnection fakeClientConnection) {
        this.fakeClientConnection = fakeClientConnection;
    }

    @Override
    public void sendMessage(String message) {
        this.fakeClientConnection.addMessageToQueue(message);
    }

    @Override
    public String getMessage() {
        String message = null;
        try {
            message = messagesFromClient.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            close();
        }
        return message;
    }

    @Override
    public void clearStack() {

    }

    @Override
    public void setPongObserver(PongObserver pongObserver) {
        this.pongObserver = pongObserver;
    }

    @Override
    public void close() {
    }

    @Override
    public void sendPing() {
        pongObserver.disablePingPong();
    }

    public void addMessageToQueue(String message){
        messagesFromClient.add(message);
    }
}
