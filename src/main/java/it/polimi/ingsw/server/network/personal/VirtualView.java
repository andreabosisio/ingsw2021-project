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

    /**
     * This method is used to pair a connection handler to this VirtualView
     * It is used when a player reconnects
     *
     * @param clientHandler handler to set
     */
    public void setClientConnectionHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * This method is used to set a player status as online/offline
     *
     * @param online player status
     */
    public void setOnline(boolean online) {
        if (!online) {
            Lobby.getLobby().broadcastToOthersInfoMessage(nickname + " has left the lobby", nickname);
        }
        this.online = online;
    }

    /**
     * This method is used to get  a player status(online/offline)
     *
     * @return the player status
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * This method is used to get the nickname of the player associated with this virtualView
     *
     * @return the player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * This method is used to get the password of the player associated with this virtualView
     *
     * @return the player's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method is used to get the clientHandler of the player associated with this virtualView
     *
     * @return the player's clientHandler
     */
    public ClientHandler getClientHandler() {
        return clientHandler;
    }


    /**
     * This method is used to start the pingPong system with the player
     * It does so by calling the sendPing function(not called directly to better be able to turnoff the system in debug)
     */
    public void startPingPong() {
        //fixme activate below for ping system
        //sendPing();
    }

    /**
     * This method is used to stop the pingPong system with the player
     * It is called when a player is manually disconnected frm the game
     */
    public void stopPingPong() {
        timer.cancel();
    }

    /**
     * This method is used to detect if the connection with the player is down
     * It does so by periodically sending a ping system and setting up a timer waiting for a reply
     * If a pong reply isn't received before the timer is over the player is considered disconnected and removed from the game
     */
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

    /**
     * This function is called to disconnect a player from the server
     * It is generally called when a pong response was not received in time
     */
    private void disconnect() {
        //function below also set online as false
        clientHandler.kill(false);
        //todo add disconnection during game code(state save/model notification ecc);
    }

    /**
     * This method is called to signal that a Pong response was received from the player
     */
    @Override
    public void PongUpdate() {
        System.out.println("pong received from: " + nickname);
        missingPong = false;
    }

    /**
     * This method is used to reconnect a player to an ongoing game
     * The new ClientHandler is also saved as this player's handler
     *
     * @param clientHandler clientHandler of the reconnecting player
     */
    public void reconnect(ClientHandler clientHandler) {
        Lobby.getLobby().broadcastInfoMessage(nickname + " has reconnected");
        this.setOnline(true);
        this.setClientConnectionHandler(clientHandler);
        clientHandler.getConnection().setPongObserver(this);
        this.timer = new Timer();
        //this.sendPing();
    }

    /**
     * This method is used to set the Controller as an Observer of this virtualView
     *
     * @param observer controller of the MVC pattern
     */
    @Override
    public void registerObserver(ReceiveObserver observer) {
        this.controllerObserver = observer;
    }

    /**
     * This method notify the Controller when an event is received from the client
     *
     * @param receiveEventFromClient the Event received from the Client
     */
    @Override
    public void notifyObservers(ReceiveEvent receiveEventFromClient) {
        controllerObserver.update(receiveEventFromClient);
    }

    /**
     * This method is called by the ModelInterface to notify this class when the model state has changed
     * If the change is meant to be seen by this virtualView's client it is serialized as a Json message
     * The message is then sent to the client via clientHandler
     *
     * @param sendEvent the Event from the Model
     */
    @Override
    public void update(SendEvent sendEvent) {
        //check if player is owner of this virtual view
        //if yes send serializable event with data to client
        if (sendEvent.isForYou(nickname)) {
            clientHandler.sendJsonMessage(sendEvent.toJson());
        }
    }
}