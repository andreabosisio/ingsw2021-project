package it.polimi.ingsw.client.network;

import it.polimi.ingsw.commons.Connection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Abstract class for the ClientConnection classes responsible to manage the connection between the Client and the Server.
 */
public abstract class ClientConnection implements Connection, Runnable {

    private final BlockingQueue<String> messagesFromServer = new LinkedBlockingQueue<>();

    /**
     * This method is used to return the plain text messages received from the Server:
     * it retrieves and removes the head of the queue, waiting if necessary until an element becomes available.
     *
     * @return the head of the queue
     */
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

    /**
     * Add a new message to the messages from client queue.
     *
     * @param message to add
     */
    @Override
    public void addMessageToQueue(String message) {
        messagesFromServer.add(message);
    }

}
