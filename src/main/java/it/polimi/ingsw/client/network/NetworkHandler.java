package it.polimi.ingsw.client.network;

import com.google.gson.*;
import it.polimi.ingsw.client.events.receive.*;
import it.polimi.ingsw.client.events.send.SendEvent;
import it.polimi.ingsw.client.utils.CommandListenerObserver;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is Observer of the CLI/GUI CommandListener class:
 * from them it receives the Events to send to the Server,
 * it creates the specific Json File and through the Connection class it sends the message just created.
 * In addition, it picks up the messages from the Server (saved in a Queue located in the Connection class)
 * and it creates the correct Events.
 */
public class NetworkHandler implements CommandListenerObserver {
    private final Connection connection;
    private final View view;
    private String nickname;

    private final Map<String, Object> messageTypeMap = new HashMap<String,Object>() {{
        put("info", InfoMessageEvent.class);
        put("error", ErrorMessageEvent.class);
        put("login", LoginEvent.class);
        put("matchmaking", MatchMakingEvent.class);
        put("setup", ChooseSetupEvent.class);
        put("lobbyChoice", ChooseNumberPlayersEvent.class);
        put("graphicUpdate", GraphicUpdateEvent.class);
        put("placeDevCard", PlaceDevCardReceiveEvent.class);
        put("transformation", TransformationReceiveEvent.class);
        put("startTurn", StartTurnUpdateEvent.class);
        put("placeResources", PlaceResourcesReceiveEvent.class);
        put("endTurnChoice", EndTurnReceiveEvent.class);
        put("gameStarted", GameStartedEvent.class);
        put("endGame", EndGameEvent.class);
        put("reconnect", ReconnectEvent.class);
    }};

    public NetworkHandler(String ip, int port, View view) throws IOException {
        Socket socket = new Socket(ip, port);
        this.view = view;
        this.connection = new ConnectionToServer(socket);
    }

    public NetworkHandler(View view) {
        this.view = view;
        this.connection = new FakeConnection();
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
        Gson gson = new Gson();
        try {
            JsonElement jsonElement = JsonParser.parseString(message);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                ReceiveEvent event;
                try {
                    event = gson.fromJson(message, (Type) messageTypeMap.get(jsonObject.get("type").getAsString()));
                    event.updateView(view);
                } catch (NullPointerException e) {
                    //malformed message
                    System.out.println("FAILED: " + message);
                    e.printStackTrace();
                }
            } else {
                System.out.println("Malformed json from Server");
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method creates the Json File from an Object of type SendEvent and
     * it sends the message through the class Connection.
     *
     * @param sendEvent Event to send to the Server
     */
    @Override
    public void update(SendEvent sendEvent) {
        connection.sendMessage(sendEvent.toJson(nickname));
    }

    /**
     * Calls the method close of the class Connection
     * to close the connection with the Server.
     */
    public void close() {
        connection.close(true);
    }
}