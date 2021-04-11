package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;

public class Player implements PlayerInterface {
    private final String nickName;
    private List<LeaderCard> leaderHand;
    private final PersonalBoard personalBoard;

    public Player(String nickName) {
        this.nickName = nickName;
        personalBoard=new PersonalBoard();
    }

    /**
     * Getter for player nickname
     * @return the player nickname
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * set the leaderCards in the Player hand
     * @param leaderHand leaderCards to set, must be of size 2
     * @return true if correctly set
     */
    public boolean setLeaderHand(List<LeaderCard> leaderHand){
        if(leaderHand.size()==2) {
            this.leaderHand = leaderHand;
            return true;
        }
        return false;
    }

    /**
     * Getter for player personalBoard
     * @return player's personalBoard
     */
    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    /**
     * Get the leaderCards the player has in his hand
     * @return List</leaderCard> in the player's hand
     * */
    public List<LeaderCard> getLeaderHand() {
        return leaderHand;
    }

    /**
     * Get the list of LeaderCards the player can activate
     * @return List</LeaderCard> of leaders that can be activated
     */
    public List<LeaderCard> getAvailableLeaderActivation(){
        List<LeaderCard> toReturn = new ArrayList<>();
        for(LeaderCard card:leaderHand){
            if(card.canBeActivated(this)){
                toReturn.add(card);
            }
        }
        return toReturn;
    }

    /**
     * Activate the specified leaderCard
     * @param leaderCard card to activate
     * @return true if activated succesfully and false if not owned by the player
     */
    public boolean setActivateLeader(LeaderCard leaderCard){
        if(leaderHand!=null && leaderHand.contains(leaderCard)) {
            if(leaderCard.activate(this)) {
                //todo leaderCards remove themselves? like this they don't!
                leaderHand.remove(leaderCard);
                return true;
            }
        }
        return false;
    }

    /**
     * Discard leaderCard from player's hand
     * @param leaderCard card to discard
     * @return true if successfully discarded and false if not owned by the player
     */
    public boolean discardLeader(LeaderCard leaderCard){
        if(leaderHand.contains(leaderCard)) {
            leaderHand.remove(leaderCard);
            //todo increments player faithtrack by one
            return true;
        }
        return false;
    }
}




