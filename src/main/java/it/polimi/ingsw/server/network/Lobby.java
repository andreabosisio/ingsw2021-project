package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.personal.PlayerData;
import it.polimi.ingsw.server.virtualView.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lobby {

    private boolean gameStarted;
    public static final int NOT_DECIDED = -1;
    public static final int MAX_PLAYERS = 4;
    public static final int MIN_PLAYERS = 1;
    private static Lobby instance = null;
    private int numberOfPlayers = NOT_DECIDED;
    private final List<PlayerData> playersData;
    private Controller controller;

    private Lobby() {
        playersData = new ArrayList<>();
        gameStarted=false;
    }

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
            for (PlayerData player : playersData) {
                if (player.isOnline() && !player.getUsername().equals(decidingPlayerName)) {
                    if (playerOnline >= numberOfPlayers) {
                        player.getClientHandler().sendInfoMessage("excluded from starting game");
                        player.setOnline(false);
                        continue;
                    }
                    playerOnline++;
                }
            }
            //remove offline players and close connection with them
            List<PlayerData>offlinePlayers = playersData.stream().filter(p->!p.isOnline()).collect(Collectors.toList());
            offlinePlayers.stream().map(PlayerData::getClientHandler).forEach(c->c.kill(true));
            playersData.removeAll(offlinePlayers);
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
        return (int)playersData.stream().filter(PlayerData::isOnline).count();
    }

    public PlayerData getPlayerDataByNickname(String nickname){
        return playersData.stream().filter(p -> p.getUsername().equals(nickname)).findFirst().orElse(null);
    }

    public synchronized boolean addPlayerData(PlayerData playerData) {
        if(isFull()){
            return false;
        }
        if(playersData.stream().anyMatch(p -> p.getUsername().equals(playerData.getUsername()))){
            return false;
        }
        broadcastInfoMessage(playerData.getUsername() + " joined!");
        playersData.add(playerData);
        return true;
    }

    public void removePlayerData(String nickname) {
        playersData.remove(getPlayerDataByNickname(nickname));
    }

    public boolean isFirstInLobby() {
        return numberOfPlayers == NOT_DECIDED;
    }

    public void broadcastInfoMessage(String message) {
        for(PlayerData playerData : playersData){
            if(playerData.isOnline()) {
                playerData.getClientHandler().sendInfoMessage(message);
            }
        }
    }

    public void broadcastToOthersInfoMessage(String message,String name) {
        for(PlayerData playerData : playersData){
            if(playerData.isOnline()&& !playerData.getUsername().equals(name)) {
                playerData.getClientHandler().sendInfoMessage(message);
            }
        }
    }

    public synchronized void UpdateLobbyState() {
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
        //playersData.stream().map(PlayerData::getClientHandler).forEach(ClientHandler::clearMessageStack);
        controller = new Controller(playersData.stream().map(PlayerData::getUsername).collect(Collectors.toList()));
        List<VirtualView> virtualViews = new ArrayList<>();
        playersData.forEach(playerData -> virtualViews.add(new VirtualView(playerData)));
        controller.setupObservers(virtualViews);
    }
}