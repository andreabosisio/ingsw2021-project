package it.polimi.ingsw.server.network.personal;

import com.google.gson.*;
import it.polimi.ingsw.server.events.receive.*;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.Server;

import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class is used by the Virtual View to send Json Objects.
 * It's purpose is also to notify the Client of the possible Errors during the Login phase.
 */
public class ClientHandler implements Runnable {
    private String nickname;
    private StatusEnum status;
    private final ConnectionToClient connectionToClient;
    private final Gson gson;
    private VirtualView virtualView;
    private static final String TYPE_QUIT = "quit";
    private static final String TYPE_LOBBY_NUMBER_CHOICE = "lobbyChoice";
    private static final String TYPE_MATCHMAKING = "matchmaking";
    private static final String TYPE_LOGIN = "login";
    private static final String CREDENTIALS_REGEXP = "^[a-zA-Z0-9_-]{3,15}$";
    private static final Pattern CREDENTIALS_PATTERN = Pattern.compile(CREDENTIALS_REGEXP);

    private final Map<String, Object> receiveEventByJsonType = new HashMap<String, Object>() {{
        put("buyAction", BuyReceiveEvent.class);
        put("cardPlacementAction", PlaceDevCardReceiveEvent.class);
        put("setupAction", SetupReceiveEvent.class);
        put("endTurnAction", EndTurnReceiveEvent.class);
        put("leaderAction", LeaderReceiveEvent.class);
        put("marketAction", MarketReceiveEvent.class);
        put("productionAction", ProductionReceiveEvent.class);
        put("resourcesPlacementAction", PlaceResourcesReceiveEvent.class);
        put("transformationAction", TransformationReceiveEvent.class);
    }};

    public ClientHandler(Socket socket) {
        this.connectionToClient = new ConnectionToClient(socket);
        this.gson = new Gson();
    }

    @Override
    public void run() {
        status = StatusEnum.LOGIN;

        //sendInfoMessage("Welcome to Maestri del Rinascimento");

        // 1- Wait for valid nickname and password
        login();

        // 3- In game/
        game();

    }

    private void login() {
        while (status == StatusEnum.LOGIN) {

            sendSpecificTypeMessage(TYPE_LOGIN);

            String message = connectionToClient.getMessage();
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
            if (!type.equals(TYPE_LOGIN) || !jsonObject.has("nickname") || !jsonObject.has("password")) {
                sendErrorMessage("invalid login json");
                continue;//go back to reading a new message
            }
            String nickname = jsonObject.get("nickname").getAsString();
            String password = jsonObject.get("password").getAsString();
            //check if username and password are acceptable
            if (!checkCredentials(nickname, password)) {
                sendErrorMessage("nickname/password not permitted");
                continue;
            }
            virtualView = Lobby.getLobby().getVirtualViewByNickname(nickname);
            //new player
            if (virtualView == null) {
                //check if Lobby is full
                if (Lobby.getLobby().isFull()) {
                    sendErrorMessage("cannot join: Server is full");
                    kill(true);
                    return;
                }
                //check if a game is ongoing even if some players are disconnected
                if (Lobby.getLobby().isGameStarted()) {
                    sendErrorMessage("a game is currently ongoing");
                    kill(true);
                    return;
                }
                virtualView = new VirtualView(nickname, password, this);
                //addVirtualView is synchronized and recheck for new player to be multiThread safe
                if (!Lobby.getLobby().addVirtualView(virtualView)) {
                    sendErrorMessage("how unlucky! this nickname was taken a moment ago");
                    continue;//go back to reading message
                }
                this.nickname = nickname;
                sendInfoMessage("Joining lobby... ");
                //try to start game
                //synchronized segment for first in lobby(further testing on what to synchronize on is required)
                synchronized (Server.getServer()) {
                    if (Lobby.getLobby().isFirstInLobby()) {
                        String answer;
                        boolean stillDeciding = true;
                        while (stillDeciding) {
                            sendSpecificTypeMessage(TYPE_LOBBY_NUMBER_CHOICE, "between " + Lobby.MIN_PLAYERS + " and " + Lobby.MAX_PLAYERS);
                            answer = connectionToClient.getMessage();
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
                    } else if (virtualView.isOnline()) {
                        clearMessageStack();
                    }
                    status = StatusEnum.GAME;
                    if (!Lobby.getLobby().isGameStarted()) {
                        sendSpecificTypeMessage(TYPE_MATCHMAKING);
                    }
                    Lobby.getLobby().updateLobbyState();
                }
                //try to start game
            } else if (virtualView.isOnline()) {
                //nickname already in use
                sendErrorMessage("Nickname already in use");
            } else {
                //is trying to reconnect
                if (password.equals(virtualView.getPassword())) {
                    this.nickname = nickname;
                    virtualView.reconnect(this);
                    sendInfoMessage("You are now reconnected");
                    status = StatusEnum.GAME;
                } else {
                    sendErrorMessage("Incorrect password");
                }
            }
        }
    }

    private void game() {
        status = StatusEnum.GAME;
        String message;
        Lobby.getLobby().getVirtualViewByNickname(nickname).startPingPong();

        while (status == StatusEnum.GAME) {
            message = connectionToClient.getMessage();

            if (message.equals("pong")) {
                continue;
            }
            try {
                JsonObject jsonObject = getAsJsonObject(message);
                if (jsonObject == null) {
                    continue;
                }
                String type = jsonObject.get("type").getAsString();
                Type eventType = (Type) receiveEventByJsonType.get(type);
                if (type.equals(TYPE_QUIT)) {
                    sendInfoMessage("quitting");
                    virtualView.disconnect();
                } else if (virtualView == null) {
                    sendErrorMessage("waitForGameToStart");
                } else if (eventType != null) {
                    ReceiveEvent event = gson.fromJson(message, eventType);
                    if (event.getNickname() == null) {
                        sendErrorMessage("you can't play with a null nickname");
                        continue;
                    }
                    if (event.getNickname().equals(nickname)) {
                        virtualView.notifyObservers(event);
                    } else {
                        sendErrorMessage("you can't play for another player");
                    }
                } else {
                    sendErrorMessage("not existing action");
                }
            } catch (JsonSyntaxException e) {
                sendErrorMessage("invalid message");
            }
        }
    }

    /**
     * This function is used to send a Json message to the client via network
     *
     * @param message message to send
     */
    public void sendJsonMessage(String message) {
        connectionToClient.sendMessage(message);
    }

    /**
     * This function is used to translate a plain text message into a Json message with type info
     *
     * @param message message to send as info
     */
    public void sendInfoMessage(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "info");
        jsonObject.addProperty("payload", message);
        sendJsonMessage(jsonObject.toString());
    }

    /**
     * This function is used to translate a plain text message into a Json message with type error
     *
     * @param message message to send as error
     */
    public void sendErrorMessage(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "error");
        jsonObject.addProperty("payload", message);
        sendJsonMessage(jsonObject.toString());
    }

    /**
     * This function is used to send a Json message with only a specified type as data
     *
     * @param type type of the Json to send
     */
    public void sendSpecificTypeMessage(String type) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        sendJsonMessage(jsonObject.toString());
    }

    /**
     * This function is used to send a Json message with both a specified type and a specified data
     *
     * @param type    type of the Json message to send
     * @param message message to save as Json payload
     */
    public void sendSpecificTypeMessage(String type, String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("payload", message);
        sendJsonMessage(jsonObject.toString());
    }

    /**
     * This function is used to close the connection to the client
     * After closing the connection the data of the player can be wiped off the Server depending on the game state
     *
     * @param deleteData parameter used to decide if the data should be deleted(true equals deletion of the data)
     *                   It is false if disconnection happened during gamePhase
     */
    public void kill(boolean deleteData) {
        //deleteData = true when disconnection is called by clientHandler before gameStarted
        if (deleteData) {
            //check if there is data to delete or if quit was called before valid credentials
            VirtualView virtualView = Lobby.getLobby().getVirtualViewByNickname(nickname);
            if (virtualView != null) {
                virtualView.stopPingPong();
                sendInfoMessage("quitting");
                Lobby.getLobby().removeVirtualView(nickname);
            }
        }
        status = StatusEnum.EXIT;
        connectionToClient.close();
        Thread.currentThread().interrupt();
    }

    /**
     * This function is used to translate e plain text message into a JsonObject
     * If the message can't be translated it automatically send an Error message as a response
     *
     * @param message message to translate into a JsonObject
     * @return the new JsonObject
     */
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

    /**
     * Function used to check if the inserted credentials are syntactically valid
     * A correct Nickname must be between 3 and 15 alpha-numeric characters (plus -,_ symbols)
     * A correct password must be not null;
     *
     * @param nickname the nickname the player wish to use
     * @param password the password the player wish to use
     * @return true if they are acceptable
     */
    public boolean checkCredentials(String nickname, String password) {
        return nickname != null && password != null && CREDENTIALS_PATTERN.matcher(nickname).matches() && CREDENTIALS_PATTERN.matcher(password).matches();
    }

    /**
     * This method is used to ignore every message received while the first player was deciding the lobby' dimension
     */
    public void clearMessageStack() {
        connectionToClient.clearStack();
    }

    /**
     * This method is used to send a plain text ping to the client
     */
    public void sendPing() {
        connectionToClient.sendMessage("ping");
    }

    /**
     * This method returns the state of the connection with the client
     *
     * @return true if the connection is up and running
     */
    public ConnectionToClient getConnection() {
        return connectionToClient;
    }

}