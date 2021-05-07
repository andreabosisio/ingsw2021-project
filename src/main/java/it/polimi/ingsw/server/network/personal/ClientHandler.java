package it.polimi.ingsw.server.network.personal;

import com.google.gson.*;
import it.polimi.ingsw.server.events.receive.*;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.Server;
import it.polimi.ingsw.server.virtualView.VirtualView;

import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private String nickname;
    private StatusEnum status;
    private final Connection connection;
    private final Gson gson;
    private VirtualView virtualView;
    private static final String TYPE_QUIT = "quit";
    private static final String TYPE_LOBBY_NUMBER_CHOICE = "lobby_choice";
    private static final String TYPE_LOGIN = "login";
    private static final String NICKNAME_REGEXP = "^[a-zA-Z0-9_-]{3,15}$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEXP);
    private final Map<String, Object> receiveEventByJsonType = new HashMap<String, Object>() {{
        put("setupAction", SetupReceiveEvent.class);
        put("leaderAction", LeaderReceiveEvent.class);
        put("buyAction", BuyReceiveEvent.class);
        put("marketAction", MarketReceiveEvent.class);
        put("productionAction", ProductionReceiveEvent.class);
        put("cardPlacementAction", PlaceDevCardReceiveEvent.class);
        put("resourcesPlacementAction", PlaceResourcesReceiveEvent.class);
        put("transformationAction", TransformationReceiveEvent.class);
        put("endTurnAction", EndTurnReceiveEvent.class);
    }};

    public ClientHandler(Socket socket) {
        this.connection = new Connection(socket);
        this.gson = new Gson();
    }

    @Override
    public void run() {
        status = StatusEnum.LOGIN;

        sendInfoMessage("Welcome to Maestri del Rinascimento");

        // 1- Wait for valid nickname and password
        login();

        // 3- In game/
        game();

    }

    private void login() {
        while (status == StatusEnum.LOGIN) {
            if (Lobby.getLobby().isFull()) {
                sendErrorMessage("cannot join: Server is full");
                kill(true);
                return;
            }
            String message = connection.getMessage();
            JsonObject jsonObject = getAsJsonObject(message);
            if (jsonObject == null) {
                continue;
            }
            String type = jsonObject.get("type").getAsString();
            if (type.equals(TYPE_QUIT)) {
                kill(true);
                return;
            }
            //check if message is not a login valid json
            if (!type.equals(TYPE_LOGIN) || !jsonObject.has("name") || !jsonObject.has("password")) {
                sendErrorMessage("invalid login json");
                continue;//go back to reading a new message
            }
            String nickname = jsonObject.get("name").getAsString();
            String password = jsonObject.get("password").getAsString();
            //check if username and password are acceptable
            if (!checkCredentials(nickname, password)) {
                sendErrorMessage("name/password not permitted");
                continue;
            }
            PlayerData playerData = Lobby.getLobby().getPlayerDataByNickname(nickname);
            //new player
            if (playerData == null) {
                //check if a game is ongoing
                if (Lobby.getLobby().isGameStarted()) {
                    sendErrorMessage("a game is currently ongoing");
                    kill(true);
                    return;
                }
                playerData = new PlayerData(nickname, password, this);
                //addPlayer is synchronized and recheck for new player to be multiThread safe
                if (!Lobby.getLobby().addPlayerData(playerData)) {
                    sendErrorMessage("how unlucky! this nickname was taken a moment ago");
                    continue;//go back to reading message
                }
                this.nickname = nickname;
                sendInfoMessage("lobby joined...");
                //try to start game
                //synchronized segment for first in lobby(further testing on what to synchronize on is required)
                synchronized (Server.getServer()) {
                    if (Lobby.getLobby().isFirstInLobby()) {
                        String answer;
                        boolean stillDeciding = true;
                        while (stillDeciding) {
                            sendInfoMessage("choose number of players(between 1 and 4)");
                            answer = connection.getMessage();
                            JsonObject jsonAnswerObject = getAsJsonObject(answer);
                            if (jsonAnswerObject == null) {
                                continue;
                            }
                            String answerType = jsonAnswerObject.get("type").getAsString();
                            if (answerType.equals(TYPE_QUIT)) {
                                kill(true);
                                return;
                            }
                            if (!answerType.equals(TYPE_LOBBY_NUMBER_CHOICE) || !jsonAnswerObject.has("size")) {
                                continue;//go back to reading a new message
                            }
                            int choice = jsonAnswerObject.get("size").getAsInt();
                            if (Lobby.getLobby().setNumberOfPlayers(choice, nickname)) {
                                stillDeciding = false;
                                status = StatusEnum.GAME;
                            }
                        }
                    } else if (playerData.isOnline())
                        clearMessageStack();
                    status = StatusEnum.GAME;
                    Lobby.getLobby().UpdateLobbyState();
                }
                //try to start game
            } else if (playerData.isOnline()) {
                //nickname already in use
                sendErrorMessage("nickname already in use");
            } else {
                //is trying to reconnect
                //todo not multi reconnection to same data safe
                if (password.equals(playerData.getPassword())) {
                    //todo code below is severely incomplete (virtualView and PlayerData together?)
                    this.nickname = nickname;
                    Lobby.getLobby().broadcastInfoMessage(nickname + " has reconnected");
                    playerData.setOnline(true);
                    playerData.setClientConnectionHandler(this);
                    Lobby.getLobby().getController().getModelInterface().getTurnLogic().removeDisconnectedPlayer(nickname);
                    sendInfoMessage("reconnected");
                    status = StatusEnum.GAME;
                } else {
                    sendErrorMessage("incorrect password");
                }
            }
        }
    }


    private void game() {
        status = StatusEnum.GAME;
        String message;
        Lobby.getLobby().getPlayerDataByNickname(nickname).startPingPong();
        //add all types of event to hashmap with key=type of event an value = event.class
        //todo write better malformed json detector code
        while (status == StatusEnum.GAME) {
            message = connection.getMessage();
            //fixme added with ping pong
            //if below
            if (message.equals("pong")) {
                continue;
            }
            try {
                JsonElement jsonElement = JsonParser.parseString(message);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("type")) {
                        String type = jsonObject.get("type").getAsString();
                        Type eventType = (Type) receiveEventByJsonType.get(type);
                        if (type.equals(TYPE_QUIT)) {
                            kill(false);
                        } else if (virtualView == null) {
                            sendErrorMessage("waitForGameToStart");
                        } else if (eventType != null) {
                            ReceiveEvent event = gson.fromJson(message, eventType);
                            if (event.getNickname().equals(nickname)) {
                                virtualView.notifyObservers(event);
                            } else {
                                sendErrorMessage("you can't play for another player");
                            }
                        } else {
                            sendErrorMessage("not existing action");
                        }
                    } else {
                        sendErrorMessage("malformed json");
                    }
                } else {
                    sendErrorMessage("not a json message");
                }
            } catch (JsonSyntaxException e) {
                sendErrorMessage("invalid message");
            }
        }
    }

    public void sendJsonMessage(String message) {
        connection.sendMessage(message);
    }

    public void sendInfoMessage(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "info");
        jsonObject.addProperty("payload", message);
        sendJsonMessage(jsonObject.toString());
    }

    public void sendErrorMessage(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "error");
        jsonObject.addProperty("payload", message);
        sendJsonMessage(jsonObject.toString());
    }

    //deleteData = false if player should be able to reconnect
    //deleteData = true if disconnection equals data deletion
    public void kill(boolean deleteData) {
        sendInfoMessage("quitting");
        //deleteData=remove playerData from Lobby
        if (deleteData) {
            Lobby.getLobby().removePlayerData(nickname);
        }
        //playerData must remain saved and player set as offline
        else {
            //todo add code to save state in game quit
            PlayerData p = Lobby.getLobby().getPlayerDataByNickname(nickname);
            p.setOnline(false);
            Lobby.getLobby().getController().getModelInterface().getTurnLogic().setDisconnectedPlayer(nickname);
        }
        //game has not started so playerData can be safely deleted
        status = StatusEnum.EXIT;
        connection.close();
        Thread.currentThread().interrupt();
    }

    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    public JsonObject getAsJsonObject(String message) {
        try {
            JsonElement jsonElement = JsonParser.parseString(message);
            if (!jsonElement.isJsonObject()) {
                sendErrorMessage("not a json message");
                return null;//go back to reading a new message
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (!jsonObject.has("type")) {
                sendErrorMessage("not a valid json");
                return null;//go back to reading a new message
            }
            return jsonObject;
        } catch (JsonSyntaxException e) {
            sendErrorMessage("invalid message structure");
            return null;
        }
    }

    //todo add more specific credential checks (maybe DONE)
    //the nickname must be a minimum of 3 and a maximum of 15 alpha-numeric characters (plus -,_ symbols)
    public boolean checkCredentials(String name, String password) {
        return name != null && password != null && NICKNAME_PATTERN.matcher(name).matches();
    }

    //todo i want to clear all messages received while stuck on synchronized (maybe DONE)
    //this function is called in a comment in lobby startGame(): line 141 more or less
    //the problem is that connection.clearStack() blocks server in listening mode
    public void clearMessageStack() {
        connection.clearStack();
    }

    public void sendPing() {
        connection.sendMessage("ping");
    }

    public Connection getConnection() {
        return connection;
    }
}