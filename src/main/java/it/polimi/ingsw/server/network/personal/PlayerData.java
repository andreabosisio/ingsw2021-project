package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.PongObserver;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerData implements PongObserver {
    private boolean online;
    private final String username;
    private final String password;
    private ClientHandler clientHandler;
    private boolean missingPong = false;
    private final Timer timer;

    public PlayerData(String username, String password, ClientHandler clientHandler) {

        this.username = username;
        this.password = password;
        this.clientHandler = clientHandler;
        this.online = true;

        clientHandler.getConnection().setPongObserver(this);
        timer = new Timer();
    }

    //this method should be used when reconnecting
    public void setClientConnectionHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void setOnline(boolean online) {
        if (!online) {
            Lobby.getLobby().broadcastToOthersInfoMessage(username + " has left the lobby", username);
        }
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }


    public void startPingPong() {
        //fixme activate below for ping system
        //sendPing();
    }

    //todo remember to test if when reconnecting a new timer needs to be created since this one has been killed
    private void sendPing() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (missingPong) {
                    disconnect();
                    System.out.println("no pong was received from " + username);
                    missingPong = false;
                    timer.cancel();
                } else {
                    missingPong = true;
                    clientHandler.sendPing();
                    System.out.println("sending ping to " + username);
                }
            }
        }, 0, 10000);//in milliseconds
    }

    //called when pong is missing
    private void disconnect() {
        //function below also set online as false
        clientHandler.kill(false);
        //todo add disconnection during game code(state save/model notification ecc);
    }

    @Override
    public void PongUpdate() {
        System.out.println("pong received from: " + username);
        missingPong = false;
    }
}