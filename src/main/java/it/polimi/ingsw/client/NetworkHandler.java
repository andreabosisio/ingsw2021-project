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
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is Observer of the CLI/GUI CommandListener class.
 */
public class NetworkHandler implements Runnable, CommandListenerObserver {
    //must be higher than the ping period
    private final static int TIMER_DELAY = 6000;//in milliseconds
    private final static String PING_MESSAGE = "ping";

    private final ConnectionToServer connectionToServer;
    private final Socket socket;
    private final View view;
    public String nickname;
    private boolean missingPing;
    private boolean receivedPing;
    Timer timer;

    private final Map<String, Object> messageTypeMap = new HashMap<String, Object>() {{
        put("info", InfoMessageEvent.class);
        put("error", ErrorMessageEvent.class);
        put("login", ConnectionEvent.class);
        put("matchmaking", MatchMakingEvent.class);
        put("setup", ChooseSetupEvent.class);
        put("lobbyChoice", ChooseNumberPlayersEvent.class);
        //put("graphicUpdate", GraphicUpdateEvent.class);
    }};

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    //todo regex check
    public NetworkHandler(String ip, Integer port, View view) throws IOException {
        this.socket = new Socket(ip, port);
        this.view = view;
        this.connectionToServer = new ConnectionToServer(socket);
        this.receivedPing =false;
        this.timer = new Timer();
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
        //Scanner in = new Scanner(System.in);
        Gson gson = new Gson();
        while (true) {
            message = connectionToServer.getMessage();
            //message is null = IOException in getMessage
            if(message==null){
                break;
            }
            if(message.equals(PING_MESSAGE)){
                handlePing();
                continue;
            }
            try {
                JsonElement jsonElement = JsonParser.parseString(message);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    ReceiveEvent event;
                    //todo remove this try(event is null only if we forgot a possible event from server)
                    try {
                        event = gson.fromJson(message, (Type) messageTypeMap.get(jsonObject.get("type").getAsString()));
                        System.out.println("Active threads before event: " + Thread.activeCount());
                        event.updateView(view);
                        System.out.println("Active threads: " + Thread.activeCount());
                    }catch (NullPointerException e){
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
        //todo close clientApp
        System.out.println("Socket generated an IOException");
    }

    /**
     * Immediately respond to the server with a pong message and start a timer to recognize if server is down
     * It does so by setting up a timer with a delay bigger than the expected ping pong system period
     * When finished the timer checks that a new ping message was received and if it is missing the client is closed
     */
    private void handlePing(){
        //todo ponder better solutions
        receivedPing = true;
        connectionToServer.sendPong();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(receivedPing){
                    receivedPing=false;
                }
                else{
                    //todo add client closing code
                    System.out.println("missing ping from server");
                }
            }
        },TIMER_DELAY);
    }


    @Override
    public void update(SendEvent sendEvent) {
        connectionToServer.sendMessage(sendEvent.toJson(nickname));
    }
}