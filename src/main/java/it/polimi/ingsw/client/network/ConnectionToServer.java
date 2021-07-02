package it.polimi.ingsw.client.network;

import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.Connection;
import it.polimi.ingsw.commons.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is used by the NetworkHandler to read the messages from the Server and to send messages via Socket:
 * all the messages received from the Server are added in a Queue.
 * It's aim is also to manage all of that concern the Socket.
 */
public class ConnectionToServer extends ClientConnection {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean receivedPing;
    private final Timer timer;
    //must be higher than the ping period
    private final static int TIMER_DELAY = 8000;//in milliseconds
    private boolean run = true;

    /**
     * Instantiates a connection through the Socket.
     *
     * @param socket The Socket connected with the Server
     */
    public ConnectionToServer(Socket socket) {
        this.socket = socket;
        startConnection();
        this.receivedPing = false;
        this.timer = new Timer();
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
            System.err.println("Failed to start connection with server");
            e.printStackTrace();
            close(false);
        }
    }

    /**
     * This method is used to send a plain text message through the Socket.
     *
     * @param message message to send
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * This method is used to safely close the connection with the server
     * It can do so by either informing the server of the quitting or not
     *
     * @param inform true if the server should be informed of the socket closing
     */
    public void close(boolean inform) {
        try {
            if (inform) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(Parser.TYPE_ID, Connection.QUIT_MSG);
                sendMessage(jsonObject.toString());
                return;
            }
            run = false;
            socket.close();
            in.close();
            out.close();
            System.exit(0);
        } catch (IOException e) {
            System.err.println("failed to close socket");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Sends a message that inform the Server that the Client is still alive.
     */
    @Override
    public void sendStillAliveMsg() {
        out.println(Connection.PONG_MSG);
    }

    /**
     * In this method a Thread remain in listen of the messages from the Network
     * and add them in the Queue
     */
    @Override
    public void run() {
        String message;
        while (run) {
            try {
                message = in.readLine();
                if (message.equals(Connection.PING_MSG)) {
                    handlePing();
                } else if (message.equals(Connection.QUIT_MSG)) {
                    //sleep to give time to read the quit message
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    close(false);
                } else {
                    addMessageToQueue(message);
                }
            } catch (IOException e) {
                System.err.println("Connection with server failed");
                //e.printStackTrace();
                close(false);
            }
        }
    }

    /**
     * Immediately respond to the server with a pong message and start a timer to recognize if server is down
     * It does so by setting up a timer with a delay bigger than the expected ping pong system period
     * When finished the timer checks that a new ping message was received and if it is missing the client is closed
     */
    private void handlePing() {
        receivedPing = true;
        this.sendStillAliveMsg();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (receivedPing) {
                    receivedPing = false;
                } else {
                    System.err.println("No Ping received from Server.\nClosing...");
                    close(false);
                }
            }
        }, TIMER_DELAY);
    }
}
