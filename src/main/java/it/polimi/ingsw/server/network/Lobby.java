package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.network.personal.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lobby {

    private boolean gameStarted;
    public static final int NOT_DECIDED = -1;
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

    //todo synchronizzare su this per ogni metodo

    public static synchronized Lobby getLobby() {
        if (instance == null) {
            instance = new Lobby();
        }
        return instance;
    }

    public boolean setNumberOfPlayers(int numberOfPlayers,String decidingPlayerName) {
        if(numberOfPlayers>MAX_PLAYERS || numberOfPlayers<MIN_PLAYERS){
            return false;
        }
        if(this.numberOfPlayers == NOT_DECIDED){
            this.numberOfPlayers = numberOfPlayers;
            //put excess player offline
            int playerOnline = 1;//start as 1 because deciding player must remain connected
            for (VirtualView player : virtualViews) {
                if (player.isOnline() && !player.getNickname().equals(decidingPlayerName)) {
                    if (playerOnline >= numberOfPlayers) {
                        player.getClientHandler().sendInfoMessage("excluded from starting game");
                        player.setOnline(false);
                        continue;
                    }
                    playerOnline++;
                }
            }
            //remove offline players and close connection with them
            List<VirtualView>offlinePlayers = virtualViews.stream().filter(p->!p.isOnline()).collect(Collectors.toList());
            offlinePlayers.stream().map(VirtualView::getClientHandler).forEach(c->c.kill(true));
            virtualViews.removeAll(offlinePlayers);
            return true;
        }
        return false;
    }

    public boolean isFull(){
        if(this.numberOfPlayers == NOT_DECIDED){
            return false;
        }
        return !(getOnlinePlayersNumber() < this.numberOfPlayers);
    }

    public int getOnlinePlayersNumber(){
        return (int) virtualViews.stream().filter(VirtualView::isOnline).count();
    }

    public VirtualView getVirtualViewByNickname(String nickname){
        return virtualViews.stream().filter(p -> p.getNickname().equals(nickname)).findFirst().orElse(null);
    }

    public boolean isPlayerOnline(String nickname) {
        return getVirtualViewByNickname(nickname).isOnline();
    }

    public synchronized boolean addVirtualView(VirtualView virtualView) {
        if(isFull()){
            return false;
        }
        if(virtualViews.stream().anyMatch(p -> p.getNickname().equals(virtualView.getNickname()))){
            return false;
        }
        broadcastInfoMessage(virtualView.getNickname() + " joined!");
        virtualViews.add(virtualView);
        return true;
    }

    public synchronized void removeVirtualView(String nickname) {
        virtualViews.remove(getVirtualViewByNickname(nickname));
    }

    public boolean isFirstInLobby() {
        return numberOfPlayers == NOT_DECIDED;
    }

    public void broadcastInfoMessage(String message) {
        for(VirtualView virtualView : virtualViews){
            if(virtualView.isOnline()) {
                virtualView.getClientHandler().sendInfoMessage(message);
            }
        }
    }

    public void broadcastToOthersInfoMessage(String message,String name) {
        for(VirtualView virtualView : virtualViews){
            if(virtualView.isOnline()&& !virtualView.getNickname().equals(name)) {
                virtualView.getClientHandler().sendInfoMessage(message);
            }
        }
    }

    public synchronized void updateLobbyState() {
        if(gameStarted){
            return;
        }
        broadcastInfoMessage("Player Online: "+getOnlinePlayersNumber()+" out of "+numberOfPlayers);
        if(isFull()){
            gameStarted=true;
            startGame();
        }
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    private void startGame(){
        broadcastInfoMessage("Game is starting...");
        //playersData.stream().map(VirtualView::getClientHandler).forEach(ClientHandler::clearMessageStack);
        controller = new Controller(virtualViews.stream().map(VirtualView::getNickname).collect(Collectors.toList()));
        controller.setupObservers(virtualViews);
    }
}