package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.PongObsever;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.virtualView.VirtualView;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerData implements PongObsever {
    private boolean online;
    private final String username;
    private final String password;
    private ClientHandler clientHandler;
    private VirtualView virtualView;

    private boolean missingPong = false;
    private Timer timer;

    public PlayerData(String username, String password, ClientHandler clientHandler) {
        /*
         //todo farei partire il pingpong quando viene creato player data
         può avere una funzione con start legato a un timer che manda un ping e starta un timer di attesa
         si può fare observer che viene notificato quando connection riceve un pong e azzera il timer di attesa
        */

        this.username = username;
        this.password = password;
        this.clientHandler = clientHandler;
        this.online=true;

        clientHandler.getConnection().setPongObserver(this);
        timer = new Timer();
    }



    //this method should be used when reconnecting
    public void setClientConnectionHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void setOnline(boolean online) {
        Lobby.getLobby().broadcastToOthersInfoMessage(username+" has left the lobby",username);
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


    public void startPingPong(){
        //fixme activate below for ping system
        //sendPing();
    }

    private void sendPing(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(missingPong){
                    disconnect();
                    System.out.println("no pong was received from "+username);
                    missingPong=false;
                    timer.cancel();
                }
                else {
                    missingPong = true;
                    clientHandler.sendPing();
                    System.out.println("sending ping to "+username);
                }
            }
        }, 0, 10000);//in milliseconds
    }

    //called when pong is missing
    private void disconnect(){
        //function below also set online as false
        clientHandler.kill(false);
        //todo add disconnection for missing pong code;
    }


    @Override
    public void PongUpdate(){
        System.out.println("pong received from: "+username);
        missingPong=false;
    }

}
