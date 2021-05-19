package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.client.events.send.SendEvent;
import it.polimi.ingsw.client.events.receive.*;
import it.polimi.ingsw.client.utils.CommandListenerObserver;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is Observer of the CLI/GUI CommandListener class.
 */
public class NetworkHandler implements CommandListenerObserver {
    private final ConnectionToServer connectionToServer;
    private final Socket socket;
    private final View view;
    private String nickname;

    private final Map<String, Object> messageTypeMap = new HashMap<String, Object>() {{
        put("info", InfoMessageEvent.class);
        put("error", ErrorMessageEvent.class);
        put("login", ConnectionEvent.class);
        put("matchmaking", MatchMakingEvent.class);
        put("setup", ChooseSetupEvent.class);
        put("lobbyChoice", ChooseNumberPlayersEvent.class);
        put("graphicUpdate", GraphicUpdateEvent.class);
        put("placeDevCard", PlaceDevCardReceiveEvent.class);
        put("transformation", TransformationReceiveEvent.class);
        put("startTurn",StartTurnUpdateEvent.class);
    }};

    public void setNickname(String nickname) {
        this.nickname = nickname;
        view.setNickname(nickname);
    }

    //todo regex check
    public NetworkHandler(String ip, Integer port, View view) throws IOException {
        this.socket = new Socket(ip, port);
        this.view = view;
        this.connectionToServer = new ConnectionToServer(socket);
    }

    /**
     * Read event from the Queue and handle it
     */
    public void startNetwork() {

        String message;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(connectionToServer);

        while (true) {

            //todo: metti synch all the updateView

            /*
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

             */

            message = connectionToServer.getMessage();
            //message is null = IOException in getMessage
            if (message == null) {
                break;
            } else {
                handleAction(message);
            }
        }
        //todo close clientApp
        System.out.println("Socket generated an IOException");
    }

    private void handleAction(String message) {
        Gson gson = new Gson();
        try {
            JsonElement jsonElement = JsonParser.parseString(message);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                ReceiveEvent event;
                //todo remove this try(event is null only if we forgot a possible event from server)
                try {
                    event = gson.fromJson(message, (Type) messageTypeMap.get(jsonObject.get("type").getAsString()));
                    //System.out.println("Active threads before event: " + Thread.activeCount());
                    event.updateView(view);
                    //System.out.println("Active threads: " + Thread.activeCount());
                } catch (NullPointerException e) {
                    System.out.println("server sent an event not defined in client: " + jsonObject.get("type"));
                    System.out.println(message);
                }
                //event.updateView(view);
            } else {
                System.out.println("Malformed json");
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(SendEvent sendEvent) {
        connectionToServer.sendMessage(sendEvent.toJson(nickname));
    }
}