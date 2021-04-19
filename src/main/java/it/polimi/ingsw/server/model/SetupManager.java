package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.List;

public class SetupManager {
    private int res = 2110;
    private final List<Player> players;

    public SetupManager(List<Player> players) {
        this.players = players;
        startSetup();
    }

    public void startSetup(){
        //GameBoard.getGameBoard().reset();
        //for(Player player:players) {
          //  int numberOfRes = res%10;
            //res = res/10;
            //List<LeaderCard> leaderDrawn;
            //leaderDrawn = GameBoard.getGameBoard().drawLeader();
            //updatePlayer(player.getNickName(),new SetUpEvent(leaderDrawn,numberOfRes));
        //}
    }
    /*
    public boolean choose(String nickname,List<Integer> leaderCardIDs,List<String> resources){
        check(nickname,leaderCardIDs,resources);

        if (last player to chose) {
            updateAllPlayersWithBoard();
        }

    }
    public boolean check(String nickname,List<Integer> leaderCardsIDs,List<String> resources){

    }

     */

}
