package it.polimi.ingsw.client.network;

public class FakeConnection implements Connection{

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void close(boolean inform) {

    }

    @Override
    public void run() {

    }
}
