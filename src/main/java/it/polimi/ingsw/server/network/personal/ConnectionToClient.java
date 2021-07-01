package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.commons.Connection;
import it.polimi.ingsw.server.network.PongObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class is used by the ClientHandler to read the messages from the Client and
 * to send messages via Socket.
 * It's aim is also to manage all of that concerne the Socket.
 */
public class ConnectionToClient extends ServerConnection {
    private BufferedReader in;
    private PrintWriter out;
    private final Socket socket;
    private PongObserver pongObserver;

    /**
     * Instantiates and starts a new Connection with the Client.
     *
     * @param socket The Socket that connects this Server to the Client
     */
    public ConnectionToClient(Socket socket) {
        this.socket = socket;
        startConnection();
    }

    /**
     * This method saves the BufferedReader and the PrintWriter
     * They will be used to send and receive messages through the socket
     */
    private void startConnection() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to send a plain text message through the socket
     *
     * @param message message to send
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Sends a message that inform that this ServerConnection is still alive.
     */
    @Override
    public void sendStillAliveMsg() {
        out.println(Connection.PING_MSG);
    }

    /**
     * This method is used to return the plain text messages received from the client through the socket
     * If the message is a pong message it also notifies a pongObserver of the received message
     *
     * @return the received message
     */
    public String getMessage() {
        String message;
        try {
            message = in.readLine();
            //start of PingPong code
            if (message.equals(Connection.PONG_MSG)) {
                pongObserver.pongUpdate();
            }
        } catch (IOException e) {
            message = null;
        }
        return message;
    }

    /**
     * This method is used to safely close the connection with the Client.
     * Before closing the socket a plain text message is sent to inform the client of the decision
     */
    @Override
    public void close(boolean inform) {
        try {
            /*
            System.out.println("Closing socket connection with one player");

             */
            out.println(Connection.QUIT_MSG);
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to delete all messages received while the server was unresponsive
     * The server is unresponsive only while another player is choosing the lobby size
     */
    public void clearStack() {
        String str;
        while (true) {
            try {
                if (!in.ready()) break;
                //used to empty the buffer
                str = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to set the pong observer
     *
     * @param pongObserver observer to notify when a pong is received
     */
    public void setPongObserver(PongObserver pongObserver) {
        this.pongObserver = pongObserver;
    }
}