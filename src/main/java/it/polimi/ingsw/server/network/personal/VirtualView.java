package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.server.events.receive.EventFromClient;
import it.polimi.ingsw.server.events.send.EventToClient;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.PongObserver;
import it.polimi.ingsw.server.utils.EventFromClientObservable;
import it.polimi.ingsw.server.utils.EventToClientObserver;
import it.polimi.ingsw.server.utils.EventsFromClientObserver;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class like an usual View of the pattern MVC is Observer of the Model and
 * it is Observable by the Controller. It works with the network sending the Events reaches
 * from the Model and it notify the Controller of the Events reaches from the Network.
 * Every Player have an instance of this class and it permits to communicate with the Model
 * with Object of type Event, the most abstract messages.
 */
public class VirtualView implements PongObserver, EventToClientObserver, EventFromClientObservable {
    //ping period variables in milliseconds
    private final static int PING_DELAY = 0;
    private final static int PING_PERIOD = 5000;
    private EventsFromClientObserver controllerObserver;
    private boolean online;
    private final String nickname;
    private final String password;
    private ClientHandler clientHandler;
    private boolean missingPong = false;
    private Timer timer;

    /**
     * This method is used to construct a new virtualView for a player
     * It also sets the virtualView as a pongObserver
     *
     * @param nickname      nickname of the player owner of the virtualView
     * @param password      password of the player owner of the virtualView
     * @param clientHandler clientHandler handling the connection with the player
     */
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
            Lobby.getLobby().broadcastToOthersInfoMessage(nickname + " has left", nickname);
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
        sendPing();
    }

    /**
     * This method is used to stop the pingPong system with the player
     * It is called when a player is manually disconnected from the game
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
                    System.out.println("No pong was received from " + nickname);
                    disconnect();
                    missingPong = false;
                } else {
                    missingPong = true;
                    clientHandler.sendPing();
                }
            }
        }, PING_DELAY, PING_PERIOD);
    }

    /**
     * This function is called to disconnect a player from the server
     * It is called when a pong response was not received in time or
     * when a quit message is received during the game phase
     */
    public void disconnect() {
        //used to notify the game of disconnection
        Lobby.getLobby().disconnectPlayer(nickname);

        setOnline(false);
        stopPingPong();
        clientHandler.kill(false);
    }

    /**
     * This method is used to reconnect a player to an ongoing game
     * The new ClientHandler is also saved as this player's handler
     *
     * @param clientHandler clientHandler of the reconnecting player
     */
    public void reconnect(ClientHandler clientHandler) {
        this.setOnline(true);
        Lobby.getLobby().broadcastToOthersInfoMessage(nickname + " has reconnected", nickname);
        this.setClientConnectionHandler(clientHandler);
        clientHandler.getConnection().setPongObserver(this);
        this.timer = new Timer();
        Lobby.getLobby().reconnectPlayer(nickname);
    }

    /**
     * This method is called to signal that a Pong response was received from the player
     */
    @Override
    public void pongUpdate() {
        missingPong = false;
    }

    /**
     * This method disables the Ping Pong
     */
    @Override
    public void disablePingPong() {
        timer.cancel();
    }

    /**
     * This method is used to set the Controller as an Observer of this virtualView
     *
     * @param observer controller of the MVC pattern
     */
    @Override
    public void registerObserver(EventsFromClientObserver observer) {
        this.controllerObserver = observer;
    }

    /**
     * This method notify the Controller when an event is received from the client
     *
     * @param eventFromClient the Event received from the Client
     */
    @Override
    public void notifyObservers(EventFromClient eventFromClient) {
        controllerObserver.update(eventFromClient);
    }

    /**
     * This method is called by the ModelInterface to notify this class when the model state has changed
     * If the change is meant to be seen by this virtualView's client it is serialized as a Json message
     * The message is then sent to the client via clientHandler
     *
     * @param eventToClient the Event from the Model
     */
    @Override
    public void update(EventToClient eventToClient) {
        if (eventToClient.isForYou(nickname) && isOnline()) {
            clientHandler.sendJsonMessage(eventToClient.toJson());
        }
    }
}