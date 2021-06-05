package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.server.network.PongObserver;

import java.io.*;
import java.net.Socket;

/**
 * This class is used by the ClientHandler to send messages via Socket
 * It's aim is to manage all of that concerne the Socket of the Client.
 */
public class ConnectionToClient {
    private BufferedReader in;
    private PrintWriter out;
    private final Socket socket;
    private PongObserver pongObserver;

    public ConnectionToClient(Socket socket) {
        this.socket = socket;
        startConnection();
    }

    /**
     * This method saves the BufferedReader and the PrintWriter
     * They will be used to send messages through the socket
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
            if (message.equals("pong")) {
                pongObserver.pongUpdate();
            }
        } catch (IOException e) {
            message = null;
        }
        return message;
    }

    /**
     * This method is used to safely close the connection with the client
     * Before closing the socket a plain text message is sent to inform the client of the decision
     */
    public void close() {
        try {
            System.out.println("closing socket connection with one player");
            out.println("quit");
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
                str = in.readLine();
                //if ((str = in.readLine()) == null) break;
                System.out.println(str + " was ignored during synchronization");
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