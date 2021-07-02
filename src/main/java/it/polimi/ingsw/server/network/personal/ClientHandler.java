package it.polimi.ingsw.server.network.personal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.client.network.FakeConnectionToServer;
import it.polimi.ingsw.commons.Connection;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.events.receive.EventFromClient;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.utils.ServerParser;

import java.net.Socket;
import java.util.regex.Pattern;

/**
 * This class is used by the Virtual View to send Json Objects.
 * It's purpose is also to notify the Client of the possible Errors during the Login phase.
 */
public class ClientHandler implements Runnable {
    private String nickname, password;
    private StatusEnum status;
    private final ServerConnection connectionToClient;
    private VirtualView virtualView;
    private static final String TYPE_LOBBY_NUMBER_CHOICE = "lobbyChoice";
    private static final String TYPE_MATCHMAKING = "matchmaking";
    private static final String TYPE_LOGIN = "login";
    private static final String CREDENTIALS_REGEXP = "^[a-zA-Z0-9_-]{3,15}$";
    private static final Pattern CREDENTIALS_PATTERN = Pattern.compile(CREDENTIALS_REGEXP);

    /**
     * Creates a new ClientHandler for a remote Client.
     *
     * @param socket The Socket connected with the remote Client
     */
    public ClientHandler(Socket socket) {
        this.connectionToClient = new ConnectionToClient(socket);
    }

    /**
     * Creates a new ClientHandler for a local Client.
     *
     * @param fakeConnectionToServer The FakeConnectionToServer of the local Client
     */
    public ClientHandler(FakeConnectionToServer fakeConnectionToServer) {
        FakeConnectionToClient fakeConnectionToClient = new FakeConnectionToClient(fakeConnectionToServer);
        fakeConnectionToServer.setFakeConnectionToClient(fakeConnectionToClient);
        this.connectionToClient = fakeConnectionToClient;
    }

    /**
     * Main loop of the Server.
     */
    @Override
    public void run() {
        status = StatusEnum.LOGIN;
        // 1- Wait for valid nickname and password
        login();
        // 2- In Lobby/Game
        game();

    }

    /**
     * This method is used to handle the login phase of the player
     * A player will remain in this method till he is successfully added to the lobby
     * or if he rejoins an ongoing game
     */
    private void login() {
        while (status == StatusEnum.LOGIN) {
            sendSpecificTypeMessage(TYPE_LOGIN);

            String message = connectionToClient.getMessage();

            JsonObject jsonObject = getAsJsonObject(message);

            if (jsonObject == null) {
                continue;
            }

            String type = Parser.getTypeFieldAsString(jsonObject);

            if (type.equals(Connection.QUIT_MSG)) {
                kill(true);
                return;
            }

            //check if message is not a login valid json
            if (!type.equals(TYPE_LOGIN) || !jsonObject.has("nickname") || !jsonObject.has("password")) {
                sendErrorMessage("Invalid Login");
                continue;//go back to reading a new message
            }

            this.nickname = jsonObject.get("nickname").getAsString();
            this.password = jsonObject.get("password").getAsString();

            //check if username and password are acceptable
            if (!checkCredentials(nickname, password)) {
                sendErrorMessage("Nickname/Password not permitted");
                continue;
            }

            virtualView = Lobby.getLobby().getVirtualViewByNickname(nickname);

            if (handleLogin()) {
                return;
            }
        }
    }

    /**
     * This method is used to handle the 2 different types of login a player can attempt:
     * 1) first time login
     * 2) reconnection to an ongoing game
     *
     * @return true if the player was unable to join/rejoin
     */
    private boolean handleLogin() {
        if (virtualView == null) {
            return firstTimeConnection();
        } else if (virtualView.isOnline()) {//try to start game
            sendErrorMessage("Nickname already in use");//nickname already in use
        } else {
            reConnect();
        }
        return false;
    }

    /**
     * This method is used to try and connect a player for the first time
     * If the player is the first one in the lobby he is also asked to decide the lobby size
     *
     * @return true If the player couldn't be added to the lobby or if he wishes to leave
     */
    private boolean firstTimeConnection() {
        if (unableToJoin()) {
            return true;
        }
        virtualView = new VirtualView(nickname, password, this);
        if (!Lobby.getLobby().addVirtualView(virtualView)) {
            sendErrorMessage("How unlucky! This nickname was taken a moment ago");
            return false;//go back to reading message
        }
        sendInfoMessage("Joining lobby... ");
        return !firstInLobby();
    }

    /**
     * This method handles the connection attempt of the player
     * It does so by checking if the lobby is full or if a game is already started
     *
     * @return true if the player couldn't be added to the lobby
     */
    private boolean unableToJoin() {
        if (Lobby.getLobby().isFull()) {
            sendErrorMessage("Cannot join: Server is full");
            kill(true);
            return true;
        }
        if (Lobby.getLobby().isGameStarted()) {
            sendErrorMessage("A game is currently ongoing");
            kill(true);
            return true;
        }
        return false;
    }

    /**
     * This method is used to connect new players one by one
     * If the player is the first one in the lobby he is also asked to decide the size of the lobby
     * or else his messageStack is cleared in case he was waiting on the synchronization
     * It also asks the lobby to notify all other players that he joined
     *
     * @return false if the player left in the middle of his connection
     */
    private boolean firstInLobby() {
        synchronized (Lobby.getLobby()) {
            if (Lobby.getLobby().isFirstInLobby()) {
                if (!decidingSizeLoop()) {
                    return false;
                }
            } else if (virtualView.isOnline()) {
                clearMessageStack();
            }
            status = StatusEnum.GAME;
            if (!Lobby.getLobby().isGameStarted()) {
                sendSpecificTypeMessage(TYPE_MATCHMAKING);
            }
            Lobby.getLobby().updateLobbyState();
            return true;
        }
    }

    /**
     * This method is used to ask the player the size of the lobby he wants
     *
     * @return false if the player left before deciding the size
     */
    private boolean decidingSizeLoop() {
        boolean stillDeciding = true;
        while (stillDeciding) {
            sendSpecificTypeMessage(TYPE_LOBBY_NUMBER_CHOICE, "between " + Lobby.MIN_PLAYERS + " and " + Lobby.MAX_PLAYERS);
            String answer = connectionToClient.getMessage();
            JsonObject jsonAnswerObject = getAsJsonObject(answer);
            if (jsonAnswerObject == null) {
                continue;
            }
            String answerType = jsonAnswerObject.get(Parser.TYPE_ID).getAsString();
            if (answerType.equals(Connection.QUIT_MSG)) {
                kill(true);
                return false;
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
        return true;
    }

    /**
     * This method is used to try reconnecting a player to an ongoing game
     * If the nickname and password do not match an error message is sent to the client
     * Otherwise the player is reconnected to his game
     */
    private void reConnect() {
        if (password.equals(virtualView.getPassword())) {
            sendInfoMessage("You are now reconnected");
            virtualView.reconnect(this);
            status = StatusEnum.GAME;
        } else {
            sendErrorMessage("Incorrect password");
        }
    }


    /**
     * This method is used to handle the game phase of the player
     */
    private void game() {
        status = StatusEnum.GAME;
        String message;

        Lobby.getLobby().getVirtualViewByNickname(nickname).startPingPong();

        while (status == StatusEnum.GAME) {
            message = connectionToClient.getMessage();

            if (message.equals(Connection.PONG_MSG)) {
                continue;
            }
            try {
                JsonObject jsonObject = getAsJsonObject(message);

                if (jsonObject == null) {
                    continue;
                }

                String type = Parser.getTypeFieldAsString(jsonObject);

                if (type.equals(Connection.QUIT_MSG)) {
                    //sendInfoMessage("quitting");
                    virtualView.disconnect();
                } else if (virtualView == null) {
                    sendErrorMessage("waitForGameToStart");
                } else {
                    EventFromClient event = ServerParser.getEventFromClient(jsonObject);
                    if (event != null) {
                        if (event.getNickname() == null) {
                            sendErrorMessage("You can't play with a null nickname");
                            continue;
                        }
                        if (event.getNickname().equals(nickname)) {
                            virtualView.notifyObservers(event);
                        } else {
                            sendErrorMessage("You can't play for another player");
                        }
                    } else {
                        sendErrorMessage("' " + type + " '" + " isn't an existing action");
                    }
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
        jsonObject.addProperty(Parser.TYPE_ID, "info");
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
        jsonObject.addProperty(Parser.TYPE_ID, "error");
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
        jsonObject.addProperty(Parser.TYPE_ID, type);
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
        jsonObject.addProperty(Parser.TYPE_ID, type);
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
                //sendInfoMessage("quitting");
                Lobby.getLobby().removeVirtualView(nickname);
            }
        }
        status = StatusEnum.EXIT;
        connectionToClient.close(true);
        Thread.currentThread().interrupt();
    }

    /**
     * This function is used to translate e plain text message into a JsonObject
     * If the message can't be translated it automatically send an Error message as a response and return null
     *
     * @param message message to translate into a JsonObject
     * @return the new JsonObject
     */
    public JsonObject getAsJsonObject(String message) {
        try {
            JsonElement jsonElement = JsonParser.parseString(message);
            if (!jsonElement.isJsonObject()) {
                sendErrorMessage("Not a JSON message");
                return null;//go back to reading a new message
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (!jsonObject.has(Parser.TYPE_ID)) {
                sendErrorMessage("Not a valid message");
                return null;//go back to reading a new message
            }
            return jsonObject;
        } catch (JsonSyntaxException e) {
            sendErrorMessage("Invalid JSON structure");
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
        connectionToClient.sendStillAliveMsg();
    }

    /**
     * This method returns the state of the connection with the client
     *
     * @return true if the connection is up and running
     */
    public ServerConnection getConnection() {
        return connectionToClient;
    }

}