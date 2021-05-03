package it.polimi.ingsw.server.network;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private final int NOT_DECIDED = -1;
    private final int MAX_PLAYERS = 4;
    private final int MIN_PLAYERS = 1;
    private static Lobby instance = null;
    private int numberOfPlayers = NOT_DECIDED;
    private final List<PlayerData> playerDataList;

    private Lobby() {
        playerDataList = new ArrayList<>();
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
        return playerDataList.stream().filter(PlayerData::isOnline).count() < this.numberOfPlayers;
    }

    public double getOnlinePlayersNumber(){
        return playerDataList.stream().filter(PlayerData::isOnline).count();
    }
}
