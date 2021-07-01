package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.events.send.EventToServer;
import it.polimi.ingsw.client.utils.ClientParser;
import it.polimi.ingsw.client.utils.CommandListenerObserver;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is Observer of the CLI/GUI CommandListener class:
 * from them it receives the Events to send to the Server,
 * it creates the specific Json File and through the ClientConnection class it sends the message just created.
 * In addition, it picks up the messages from the Server (saved in a Queue located in the ClientConnection class)
 * and it creates the correct Events.
 */
public class NetworkHandler implements CommandListenerObserver {
    private final ClientConnection connection;
    private final View view;
    private String nickname;

    /**
     * Create a new NetworkHandler for a remote Server.
     *
     * @param ip   IP Address of the remote Server
     * @param port Port of of the remote Server
     * @param view The current UI
     * @throws IOException if Socket creation failed
     */
    public NetworkHandler(String ip, int port, View view) throws IOException {
        Socket socket = new Socket(ip, port);
        this.view = view;
        this.connection = new ConnectionToServer(socket);
    }

    /**
     * Create a new NetworkHandler for a local instantiated Server.
     *
     * @param view The current UI
     */
    public NetworkHandler(View view) {
        this.view = view;
        this.connection = new FakeConnectionToServer();
    }

    /**
     * Method that set the Nickname of the Player
     *
     * @param nickname of the Player
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
        view.setNickname(nickname);
    }

    /**
     * Method that reads events from the Queue and handles it
     */
    public void startNetwork() {

        String message;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(connection);

        while (true) {
            message = connection.getMessage();
            //message is null = IOException in getMessage
            if (message == null) {
                break;
            } else {
                handleAction(message);
            }
        }
        System.out.println("Socket generated an IOException");
        connection.close(false);
    }

    /**
     * Method that creates a specific Event from a message serialized as a Json message and
     * it calls his method updateView(View view)
     *
     * @param message is the String message received from the Server
     */
    private void handleAction(String message) {
        Objects.requireNonNull(ClientParser.getEventFromServer(message)).updateView(view);
    }

    /**
     * This method creates the Json File from an Object of type EventToServer and
     * it sends the message through the class ClientConnection.
     *
     * @param eventToServer Event to send to the Server
     */
    @Override
    public void update(EventToServer eventToServer) {
        connection.sendMessage(eventToServer.toJson(nickname));
    }

    /**
     * Calls the method close of the class ClientConnection
     * to close the connection with the Server.
     */
    public void close() {
        connection.close(true);
    }
}