package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.network.personal.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains all the information about the number and the status of the Players.
 * It has the power to start a new game, to send Broadcast messages and to notify the Controller
 * of the status of a Player (online/offline).
 * It is Singleton so that only a game at time can be instantiate.
 */
public class Lobby {
    private boolean gameStarted;
    private static final int NOT_DECIDED = -1;
    public static final int MIN_PLAYERS = 1;
    public static final int MAX_PLAYERS = 4;
    private static Lobby instance = null;
    private int numberOfPlayers = NOT_DECIDED;
    private final List<VirtualView> virtualViews;
    private Controller controller;

    private Lobby() {
        virtualViews = new ArrayList<>();
        gameStarted = false;
    }

    /**
     * This method is used to get the only instance of the Lobby or create a new one if it does not exist
     *
     * @return instance of the Lobby
     */
    public static synchronized Lobby getLobby() {
        if (instance == null) {
            instance = new Lobby();
        }
        return instance;
    }

    /**
     * This method is used to set the Lobby size
     * It automatically disconnect every player that does not fit in the new size
     *
     * @param numberOfPlayers    size to set
     * @param decidingPlayerName Player deciding the size(he can't be disconnected)
     * @return true if the Lobby sizing was completed successfully
     */
    public boolean setNumberOfPlayers(int numberOfPlayers, String decidingPlayerName) {
        if (numberOfPlayers > MAX_PLAYERS || numberOfPlayers < MIN_PLAYERS) {
            return false;
        }
        if (this.numberOfPlayers == NOT_DECIDED) {
            this.numberOfPlayers = numberOfPlayers;
            //put excess player offline
            int playerOnline = 1;//start as 1 because deciding player must remain connected
            for (VirtualView player : virtualViews) {
                if (player.isOnline() && !player.getNickname().equals(decidingPlayerName)) {
                    if (playerOnline >= numberOfPlayers) {
                        player.getClientHandler().sendInfoMessage("Excluded from starting game");
                        player.setOnline(false);
                        continue;
                    }
                    playerOnline++;
                }
            }
            //remove offline players and close connection with them
            List<VirtualView> offlinePlayers = virtualViews.stream().filter(p -> !p.isOnline()).collect(Collectors.toList());
            offlinePlayers.stream().map(VirtualView::getClientHandler).forEach(c -> c.kill(true));
            virtualViews.removeAll(offlinePlayers);
            return true;
        }
        return false;
    }

    /**
     * This method is used to check if the Lobby is full
     *
     * @return true if the Lobby is full
     */
    public synchronized boolean isFull() {
        if (this.numberOfPlayers == NOT_DECIDED) {
            return false;
        }
        return !(getOnlinePlayersNumber() < this.numberOfPlayers);
    }

    /**
     * This method is used to get the number of players currently online
     *
     * @return the number of players online
     */
    public synchronized int getOnlinePlayersNumber() {
        return (int) virtualViews.stream().filter(VirtualView::isOnline).count();
    }

    /**
     * This method is used to get the virtualView associated with the specified nickname
     *
     * @param nickname nickname to search for
     * @return the VirtualView associated with the nickname or null if nothing was found
     */
    public synchronized VirtualView getVirtualViewByNickname(String nickname) {
        return virtualViews.stream().filter(p -> p.getNickname().equals(nickname)).findFirst().orElse(null);
    }

    /**
     * This method is used to see if the player associated with the specified nickname is still online
     *
     * @param nickname nickname of the player to check
     * @return true if the player is online
     */
    public boolean isPlayerOnline(String nickname) {
        return getVirtualViewByNickname(nickname).isOnline();
    }

    /**
     * This method is used to add a new virtualView to the List saved in the Lobby(If the lobby is nt full)
     * Every VirtualView represent a player
     * This method also automatically broadcast a message to all the other players informing them that a new one joined
     *
     * @param virtualView virtualView to add
     * @return true if added successfully and false if the Lobby was full
     */
    public synchronized boolean addVirtualView(VirtualView virtualView) {
        if (isFull()) {
            return false;
        }
        if (virtualViews.stream().anyMatch(p -> p.getNickname().equals(virtualView.getNickname()))) {
            return false;
        }
        broadcastMessage(virtualView.getNickname() + " joined!");
        virtualViews.add(virtualView);
        return true;
    }

    /**
     * This method is used to remove a VirtualView from the Lobby
     *
     * @param nickname nickname associated with the virtualView to remove
     */
    public synchronized void removeVirtualView(String nickname) {
        virtualViews.remove(getVirtualViewByNickname(nickname));
    }

    /**
     * This method is used to check if a player is the first to join the lobby
     *
     * @return true if first one to join
     */
    public boolean isFirstInLobby() {
        return numberOfPlayers == NOT_DECIDED;
    }

    /**
     * This method is used to broadcast a message to all the player currently in the Lobby
     * if not parameter type is given a the message is set as an info message
     *
     * @param message message to broadcast
     */
    public synchronized void broadcastMessage(String message) {
        for (VirtualView virtualView : virtualViews) {
            if (virtualView.isOnline()) {
                virtualView.getClientHandler().sendInfoMessage(message);
            }
        }
    }

    /**
     * This method is used to broadcast a message to all the players connected to the Lobby except one
     *
     * @param message message to broadcast
     * @param name    nickname of the player to ignore
     */
    public synchronized void broadcastToOthersInfoMessage(String message, String name) {
        for (VirtualView virtualView : virtualViews) {
            if (virtualView.isOnline() && !virtualView.getNickname().equals(name)) {
                virtualView.getClientHandler().sendInfoMessage(message);
            }
        }
    }

    /**
     * This method is used to check if a game can be started with the current state of the Lobby
     * If the Lobby is still not full a message with the number of online players is broadcast to every player
     * If the game can be started every player is notified and startGame is called
     */
    public synchronized void updateLobbyState() {
        if (gameStarted) {
            return;
        }
        broadcastMessage("Players Online: " + getOnlinePlayersNumber() + " out of " + numberOfPlayers);
        if (isFull()) {
            gameStarted = true;
            startGame();
        }
    }

    /**
     * This method is used to check if a game is currently ongoing
     *
     * @return true if a game is ongoing
     */
    public synchronized boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * This method is used to start a game
     * It does so by creating a new Controller class and passing to it all the virtualViews
     * The Controller will do the rest
     */
    private void startGame() {
        controller = new Controller(virtualViews);
    }

    /**
     * Method used to communicate to the controller that a player is now offline
     * If no game was ongoing the virtualView is removed
     * If the player was the last one connected to the game the lobby is reset
     *
     * @param nickname of the Player offline
     */
    public synchronized void disconnectPlayer(String nickname) {
        if (gameStarted) {
            if (controller.disconnectPlayer(nickname)) {
                System.out.println("Last player left\nResetting server");
                destroyLobby(nickname);
            }
        } else
            removeVirtualView(nickname);
    }

    /**
     * This method is used to reset the lobby state
     *
     * @param nickname nickname of the currently disconnecting player
     */
    private void destroyLobby(String nickname) {
        virtualViews.stream().filter(v -> !v.getNickname().equals(nickname) && v.isOnline()).forEach(VirtualView::disconnect);//if a player is waiting on the lock for a reconnection abort it
        numberOfPlayers = NOT_DECIDED;
        gameStarted = false;
        virtualViews.clear();
    }


    /**
     * Method used to communicate to the controller that a player is now reconnected and online
     *
     * @param nickname of the Player online
     */
    public synchronized void reconnectPlayer(String nickname) {
        if (gameStarted) {
            controller.reconnectPlayer(nickname);
        }
    }

    /**
     * This method is used to cheat 6 resources to each player during the game demo
     */
    public void cheat() {
        try {
            controller.cheat();
            System.out.println("Added 6 resources to each player");
        } catch (NullPointerException e) {
            System.err.println("Cannot cheat right now because the game is not started yet.");
        }
    }
}