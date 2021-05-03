package it.polimi.ingsw.server.network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable{
    private Socket socket;
    private String nickname;
    private boolean status;
    private Connection connection;
    private final Gson gson;

    public ClientHandler(Socket socket){
        this.socket = socket;
        this.connection = new Connection(socket);
        gson = new Gson();
    }


    @Override
    public void run() {
        status = true;

        if(!Lobby.getLobby().isNotFull()){
            sendErrorMessage("cannot join");
            kill();

            //todo to check
            return;
        }


        // 1- Wait for valid nickname and password
        login();

        // 2- Try to set the number of players
        chooseNumberOfPlayers();

        // 3- Wait for others players
        lobby();

        //todo setup();

    }

    private void login(){
        while(status) {
            String message = connection.getMessage();

            if(message.equals("quit")){
                kill();
                //todo return 0 e via else
            } else {
                String nickname;
                String password;
                try {
                    //todo metti map
                    String[] data = gson.fromJson(message, String[].class);
                    nickname = data[0];
                    password = data[1];
                }catch (JsonSyntaxException e){
                    sendErrorMessage("invalid nickname e/o password");
                    continue;
                }

                //todo check valid username and password and senMessage(immetti nickname and password)
                synchronized (Lobby.getLobby()) {
                    PlayerData playerData = Lobby.getLobby().getPlayerDataByNickname(nickname);

                    if (playerData == null) {
                        //new player
                        playerData = new PlayerData(nickname, password, this);
                        this.nickname = nickname;
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
                        } else {
                            sendErrorMessage("incorrect password");
                        }
                    }
                }
            }
        }
    }

    private void chooseNumberOfPlayers(){
        //todo pensare a un altro modo
        synchronized (Lobby.getLobby()) {
            if (!Lobby.getLobby().isFirstInLobby()) {
                //todo what if while choosing more people than allowed joined?
                return;
            }
            connection.sendMessage("choose number of players");
            String message;
            Integer numberOfPlayers = 0;
            while (status) {
                message = connection.getMessage();
                if (message.equals("quit")) {
                    //todo quit con Evento dipendente da stato
                    Lobby.getLobby().removePlayerData(nickname);
                    kill();
                } else {
                    try {
                        numberOfPlayers = gson.fromJson(message, Integer.class);
                    } catch (JsonSyntaxException e) {
                        sendErrorMessage("invalid number");
                    }
                    if (Lobby.getLobby().setNumberOfPlayers(numberOfPlayers)) {
                        return;
                    }
                    sendErrorMessage("invalid number");
                }
            }
        }
    }

    private void lobby(){
        sendInfoMessage("In Lobby: waiting for other players...");
        String message;
        while(status) {
            message = connection.getMessage();
            if(message.equals("quit")) {
                //todo quit con Evento dipendente da stato
                Lobby.getLobby().removePlayerData(nickname);
                kill();
            }

        }
    }

    public void sendJSONMessage(String message){
        connection.sendMessage(message);
    }

    public void sendInfoMessage(String message) {
        Map<String,String> info = new HashMap<>();
        info.put("Type", "Info");
        info.put("Payload", message);
        sendJSONMessage(gson.toJson(info));

    }
    public void sendErrorMessage(String message){
        Map<String,String> error = new HashMap<>();
        error.put("Type", "Error");
        error.put("Payload", message);
        sendJSONMessage(gson.toJson(error));
    }


    public void kill(){
        status = false;
        connection.close();

        //maybe close connection properly before this
        Thread.currentThread().interrupt();
    }
}
