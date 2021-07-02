package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.commons.Connection;
import it.polimi.ingsw.server.network.PongObserver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This abstract class is extended from the classes that have a connection with the Client
 */
public abstract class ServerConnection implements Connection {

    private final BlockingQueue<String> messagesFromClient = new LinkedBlockingQueue<>();

    @Override
    public String getMessage() {
        String message = null;
        try {
            message = messagesFromClient.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            close(true);
        }
        return message;
    }

    /**
     * Add a new message to the messages from client queue.
     *
     * @param message to add
     */
    public void addMessageToQueue(String message) {
        messagesFromClient.add(message);
    }

    /**
     * Clear the unnecessary messages from client
     */
    public void clearStack() {
    }

    /**
     * Set the observer interested in the arrival of a pong message
     *
     * @param pongObserver observer of the pong
     */
    void setPongObserver(PongObserver pongObserver) {
    }

}
