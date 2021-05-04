package it.polimi.ingsw.server.network;

import com.google.gson.*;
import it.polimi.ingsw.server.events.receive.BuyReceiveEvent;
import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.receive.SetupReceiveEvent;
import it.polimi.ingsw.server.virtualView.VirtualView;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ClientHandler implements Runnable {
    private Socket socket;
    private String nickname;
    private StatusEnum status;
    private boolean waitingForFullLobby;
    private Connection connection;
    private final Gson gson;
    private VirtualView virtualView;

    private final Map<String, Object> receiveEventByJsonType = new HashMap<String, Object>() {{
        put("SetupReceiveEvent", SetupReceiveEvent.class);
    }};

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.connection = new Connection(socket);
        waitingForFullLobby = true;
        gson = new Gson();
    }


    @Override
    public void run() {
        status = StatusEnum.LOGIN;

        if (Lobby.getLobby().isFull()) {
            sendErrorMessage("cannot join: Server is full");
            kill();
            return;
        }

        // 1- Wait for valid nickname and password
        login();

        // 2- Wait for others players
        lobby();

        // 3- In game
        game();

    }

    private void login() {
        while (status == StatusEnum.LOGIN) {
            String message = connection.getMessage();

            if (message.equals("quit")) {
                kill();
                return;
            }

            String nickname;
            String password;
            try {
                //todo metti map
                String[] data = gson.fromJson(message, String[].class);
                nickname = data[0];
                password = data[1];
            } catch (JsonSyntaxException e) {
                sendErrorMessage("invalid nickname e/o password");
                continue;
            }

            //todo check valid username and password and senMessage(immetti nickname and password)
            synchronized (Lobby.getLobby()) {

                //dovrei fare qui un altro check per vedere se lobby is full
                //perchè se viene settato il numero mentre aspetto che si liberi il syncronized
                //qui permetto di fare un accesso non consentito
                //oppure posso modificare addPlayer di lobby affinchè faccia un controllo

                if (Lobby.getLobby().isFull()) {
                    sendErrorMessage("cannot join Lobby is full");
                    kill();
                    return;
                }

                PlayerData playerData = Lobby.getLobby().getPlayerDataByNickname(nickname);

                if (playerData == null) {
                    //new player
                    playerData = new PlayerData(nickname, password, this);
                    this.nickname = nickname;
                    status = StatusEnum.LOBBY;
                    Lobby.getLobby().addPlayerData(playerData);
                    break;
                } else if (playerData.isOnline()) {
                    //nickname already in use
                    //error type
                    sendErrorMessage("nickname already in use");
                } else {
                    //is trying to reconnect
                    if (password.equals(playerData.getPassword())) {
                        this.nickname = nickname;
                        Lobby.getLobby().broadcastInfoMessage(nickname + " has reconnected");
                        playerData.setOnline(true);
                        sendInfoMessage("reconnected");
                        status = StatusEnum.GAME;
                    } else {
                        sendErrorMessage("incorrect password");
                    }
                }
            }
        }
    }

    private void chooseNumberOfPlayers() {

        synchronized (Lobby.getLobby()) {
            if (!Lobby.getLobby().isFirstInLobby()) {
                status = StatusEnum.LOBBY;
                return;
            }

            status = StatusEnum.CHOOSENUMPLAYERS;

            sendInfoMessage("Choose the number of players (MAX " + Lobby.MAX_PLAYERS + "): ");
            String message;
            Integer numberOfPlayers = 0;
            while (status == StatusEnum.CHOOSENUMPLAYERS) {
                message = connection.getMessage();

                if (message.equals("quit")) {
                    //todo quit con Evento dipendente da stato
                    Lobby.getLobby().removePlayerData(nickname);
                    kill();
                    return;
                }
                try {
                    numberOfPlayers = gson.fromJson(message, Integer.class);
                } catch (JsonSyntaxException e) {
                    sendErrorMessage(message + " it's not a number. Please re-insert a valid number.");
                    continue;
                }

                if (Lobby.getLobby().setNumberOfPlayers(numberOfPlayers)) {
                    //todo forse dava problemi dopo la nostra sessione
                    if (status == StatusEnum.CHOOSENUMPLAYERS){
                        status = StatusEnum.LOBBY;
                    }
                    return;
                }

                sendErrorMessage("Invalid number of players. MIN: " + Lobby.MIN_PLAYERS + "players and MAX: " + Lobby.MAX_PLAYERS + " players.");
            }
        }
    }

    private void lobby() {
        String message;
        while (status == StatusEnum.LOBBY) {

            //Try to set the number of players
            chooseNumberOfPlayers();

            if (status == StatusEnum.LOBBY) {
                sendInfoMessage("Matchmaking: Waiting for other players...");

                message = connection.getMessage();

                if (message.equals("quit")) {
                    //todo quit con Evento dipendente da stato
                    Lobby.getLobby().removePlayerData(nickname);
                    kill();
                    return;
                }
            }
        }
    }

    private void game(){
        String message;
        //add all types of event to hasmap with key=type of event an value = event.class
        while (status == StatusEnum.GAME) {
            message = connection.getMessage();
            try{
                JsonElement jsonElement = JsonParser.parseString(message);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if(jsonObject.has("type")) {
                    String type = jsonObject.get("type").getAsString();
                    ReceiveEvent event = gson.fromJson(message, (Type) receiveEventByJsonType.get(type));
                    virtualView.notifyObservers(event);
                }
                else System.out.println("malformed json");
            }catch(JsonSyntaxException e) {
                sendErrorMessage("Invalid setup");
            }
        }
    }

    public void sendJsonMessage(String message) {
        connection.sendMessage(message);
    }

    public void sendInfoMessage(String message) {
        Map<String, String> info = new HashMap<>();
        info.put("Type", "Info");
        info.put("Payload", message);
        sendJsonMessage(gson.toJson(info));

    }

    public void sendErrorMessage(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("Type", "Error");
        error.put("Payload", message);
        sendJsonMessage(gson.toJson(error));
    }

    public void kill() {
        status = StatusEnum.EXIT;
        connection.close();

        //maybe close connection properly before this
        Thread.currentThread().interrupt();
    }

    public void setWaitingForFullLobby() {
        status = StatusEnum.GAME;
    }

    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }
}