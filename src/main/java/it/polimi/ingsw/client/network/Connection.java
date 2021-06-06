package it.polimi.ingsw.client.network;

public interface Connection extends Runnable {

    String getMessage();

    void sendMessage(String message);

    void close(boolean inform);
}
