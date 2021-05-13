package it.polimi.ingsw.client;


import com.google.gson.*;
import it.polimi.ingsw.client.events.send.SendEvent;
import it.polimi.ingsw.client.events.receive.*;
import it.polimi.ingsw.client.utils.CommandListenerObserver;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is Observer of the CLI/GUI CommandListener class.
 */
public class NetworkHandler implements Runnable, CommandListenerObserver {
    private final ConnectionToServer connectionToServer;
    private final Socket socket;
    private final View view;

    private final Map<String, Object> messageTypeMap = new HashMap<String, Object>() {{
        put("info", InfoMessageEvent.class);
        put("error", ErrorMessageEvent.class);
        put("login", ConnectionEvent.class);
        put("matchmaking", MatchMakingEvent.class);
        put("chooseSetup", ChooseSetupEvent.class);
        put("lobbyChoice", ChooseNumberPlayersEvent.class);
    }};

    //todo regex check
    public NetworkHandler(String ip, Integer port, View view) throws IOException {
        this.socket = new Socket(ip, port);
        this.view = view;
        this.connectionToServer = new ConnectionToServer(socket);
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        String message;
        Scanner in = new Scanner(System.in);
        Gson gson = new Gson();
        while (true) {
            message = connectionToServer.getMessage();
            //handle message

            //todo check for ping :)
            try {
                JsonElement jsonElement = JsonParser.parseString(message);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    ReceiveEvent event = gson.fromJson(message, (Type) messageTypeMap.get(jsonObject.get("type").getAsString()));
                    event.updateView(view);
                } else {
                    System.out.println("Malformed json");
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }


            //System.out.println(message);

        }
    }


    @Override
    public void update(SendEvent sendEvent) {
        connectionToServer.sendMessage(sendEvent.toJson());
    }
}