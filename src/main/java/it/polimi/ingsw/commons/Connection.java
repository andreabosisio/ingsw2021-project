package it.polimi.ingsw.commons;

/**
 * Interface for the ServerConnection classes responsible to manage the connection between the Client and the Server.
 */
public interface Connection {

    String QUIT_MSG = "quit";
    String PING_MSG = "ping";
    String PONG_MSG = "pong";

    /**
     * Gets a message from the ServerConnection.
     *
     * @return A new String containing the message from the ServerConnection
     */
    String getMessage();

    /**
     * Sends a new message through the ServerConnection.
     *
     * @param message String containing the message
     */
    void sendMessage(String message);

    /**
     * Sends a message that inform that this ServerConnection side is still alive.
     */
    void sendStillAliveMsg();

    /**
     * Closes the ServerConnection between the Client and the Server.
     *
     * @param inform true if the connected subjects should be informed of the ClientConnection closure
     */
    void close(boolean inform);

    /**
     * Adds a new message to the Connection messages queue.
     *
     * @param message A String containing the message to add.
     */
    void addMessageToQueue(String message);
}