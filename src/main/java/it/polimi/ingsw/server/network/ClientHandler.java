package it.polimi.ingsw.server.network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket socket;
    private String nickname;
    private StatusEnum status;
    private boolean waitingForFullLobby;
    private Connection connection;
    private final Gson gson;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.connection = new Connection(socket);
        waitingForFullLobby = true;
        gson = new Gson();
    }


    @Override
    public void run() {
        status = StatusEnum.LOGIN;

        if (!Lobby.getLobby().isNotFull()) {
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

                if (!Lobby.getLobby().isNotFull()) {
                    sendErrorMessage("cannot join Lobby is full");
                    kill();
                    return;
                }

                PlayerData playerData = Lobby.getLobby().getPlayerDataByNickname(nickname);

                if (playerData == null) {
                    //new player
                    playerData = new PlayerData(nickname, password, this);
                    this.nickname = nickname;
                    Lobby.getLobby().addPlayerData(playerData);
                    status = StatusEnum.LOBBY;
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
                    sendErrorMessage("It's not a number");
                    continue;
                }

                if (Lobby.getLobby().setNumberOfPlayers(numberOfPlayers)) {
                    status = StatusEnum.LOBBY;
                    return;
                }

                sendErrorMessage("invalid number");
            }
        }
    }

    private void lobby() {

        String message;
        while (status == StatusEnum.LOBBY) {

            //Try to set the number of players
            chooseNumberOfPlayers();

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

    private void game(){

        while (status == StatusEnum.SETUP) {
        }
    }

    public void sendJSONMessage(String message) {
        connection.sendMessage(message);
    }

    public void sendInfoMessage(String message) {
        Map<String, String> info = new HashMap<>();
        info.put("Type", "Info");
        info.put("Payload", message);
        sendJSONMessage(gson.toJson(info));

    }

    public void sendErrorMessage(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("Type", "Error");
        error.put("Payload", message);
        sendJSONMessage(gson.toJson(error));
    }


    public void kill() {
        status = StatusEnum.EXIT;
        connection.close();

        //maybe close connection properly before this
        Thread.currentThread().interrupt();
    }

    public void setWaitingForFullLobby() {
        status = StatusEnum.SETUP;
    }
}
