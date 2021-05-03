package it.polimi.ingsw.server.network;

import java.util.HashSet;
import java.util.Set;

public class Lobby {

    private final int NOT_DECIDED = -1;
    private final int MAX_PLAYERS = 4;
    private final int MIN_PLAYERS = 1;
    private static Lobby instance = null;
    private int numberOfPlayers = NOT_DECIDED;
    private final Set<PlayerData> playersData;

    private Lobby() {
        playersData = new HashSet<>();
    }

    public static synchronized Lobby getLobby() {
        if (instance == null) {
            instance = new Lobby();
        }
        return instance;
    }

    public boolean setNumberOfPlayers(int numberOfPlayers) {
        if(numberOfPlayers>MAX_PLAYERS || numberOfPlayers<MIN_PLAYERS){
            return false;
        }
        if(this.numberOfPlayers==NOT_DECIDED){
            this.numberOfPlayers = numberOfPlayers;
            return true;
        }
        return false;
    }

    public boolean isNotFull(){
        if(this.numberOfPlayers==NOT_DECIDED){
            return true;
        }
        return getOnlinePlayersNumber() < this.numberOfPlayers;
    }

    public double getOnlinePlayersNumber(){
        return playersData.stream().filter(PlayerData::isOnline).count();
    }

    public PlayerData getPlayerDataByNickname(String nickname){
        return playersData.stream().filter(p -> p.getUsername().equals(nickname)).findFirst().orElse(null);
    }

    public void addPlayerData(PlayerData playerData) {
        broadcastInfoMessage(playerData.getUsername() + " joined!");
        playersData.add(playerData);
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



}
