package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.send.SendEvent;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.PongObserver;
import it.polimi.ingsw.server.utils.ReceiveObservable;
import it.polimi.ingsw.server.utils.ReceiveObserver;
import it.polimi.ingsw.server.utils.SendObserver;

import java.util.Timer;
import java.util.TimerTask;

public class VirtualView implements PongObserver, SendObserver, ReceiveObservable {
    //ping period variables in milliseconds
    private final static int PING_DELAY = 0;
    private final static int PING_PERIOD = 5000;

    private ReceiveObserver controllerObserver;
    private boolean online;
    private final String nickname;
    private final String password;
    private ClientHandler clientHandler;
    private boolean missingPong = false;
    private Timer timer;

    public VirtualView(String nickname, String password, ClientHandler clientHandler) {

        this.nickname = nickname;
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
            Lobby.getLobby().broadcastToOthersInfoMessage(nickname + " has left the lobby", nickname);
        }
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public String getNickname() {
        return nickname;
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

    public void stopPingPong() {
        timer.cancel();
    }

    private void sendPing() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (missingPong) {
                    disconnect();
                    System.out.println("no pong was received from " + nickname);
                    missingPong = false;
                    stopPingPong();
                } else {
                    missingPong = true;
                    clientHandler.sendPing();
                    System.out.println("sending ping to " + nickname);
                }
            }
        },PING_DELAY,PING_PERIOD);
    }

    //called when pong is missing
    private void disconnect() {
        //function below also set online as false
        clientHandler.kill(false);
        //todo add disconnection during game code(state save/model notification ecc);
    }

    @Override
    public void PongUpdate() {
        System.out.println("pong received from: " + nickname);
        missingPong = false;
    }

    public void reconnect(ClientHandler clientHandler) {
        Lobby.getLobby().broadcastInfoMessage(nickname + " has reconnected");
        this.setOnline(true);
        this.setClientConnectionHandler(clientHandler);
        clientHandler.getConnection().setPongObserver(this);
        this.timer = new Timer();
        //this.sendPing();
    }

    @Override
    public void registerObserver(ReceiveObserver observer) {
        this.controllerObserver = observer;
    }

    /**
     * This method notify the Controller of the reach of an Event from the Client
     *
     * @param receiveEventFromClient the Event from the Client
     */
    @Override
    public void notifyObservers(ReceiveEvent receiveEventFromClient) {
        controllerObserver.update(receiveEventFromClient);
    }

    /**
     * This method is called by the ModelInterface to notify this class
     * of an amendment of the Model
     *
     * @param sendEvent the Event from the Model
     */
    @Override
    public void update(SendEvent sendEvent) {
        if (sendEvent.isForYou(nickname)) {
            clientHandler.sendJsonMessage(sendEvent.toJson());
        }
        //check if player is owner of this virtual view
        //if yes send serializable event with data to client
    }
}