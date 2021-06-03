package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.events.send.SendEvent;
import it.polimi.ingsw.server.events.send.StartTurnEvent;
import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.resources.WhiteResource;
import it.polimi.ingsw.server.model.turn.State;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.List;

public class Player implements PlayerInterface {

    private State disconnectedState;
    private List<WhiteResource> whiteResourcesFromMarket;
    private DevelopmentCard chosenDevCard;
    private SendEvent lastReceivedEvent;






    private boolean isOnline;
    private final String nickname;
    private List<LeaderCard> leaderHand;
    private final PersonalBoard personalBoard;

    public Player(String nickname) {
        this.nickname = nickname;
        personalBoard = new PersonalBoard();
        isOnline = true;
    }

    /**
     * Getter for player nickname
     *
     * @return the player nickname
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * set the leaderCards in the Player hand
     *
     * @param leaderHand leaderCards to set, must be of size 2
     * @return true if correctly set
     */
    public boolean setLeaderHand(List<LeaderCard> leaderHand) {
        if (leaderHand.size() == 2) {
            this.leaderHand = new ArrayList<>(leaderHand);
            return true;
        }
        return false;
    }

    /**
     * Getter of the player's personalBoard
     *
     * @return player's personalBoard
     */
    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    /**
     * Get the leaderCards the player has in his hand
     *
     * @return List</ leaderCard> in the player's hand
     */
    public List<LeaderCard> getLeaderHand() {
        return leaderHand;
    }

    /**
     * Used only for testing
     * Get the list of LeaderCards the player can activate
     *
     * @return List</ LeaderCard> of leaders that can be activated
     */
    public List<LeaderCard> getAvailableLeaderActivationTest() {
        List<LeaderCard> toReturn = new ArrayList<>();
        for (LeaderCard card : leaderHand) {
            if (card.canBeActivated(this)) {
                toReturn.add(card);
            }
        }
        return toReturn;
    }

    /**
     * Used only for testing
     * Activate the specified leaderCard
     *
     * @param leaderCard card to activate
     * @return true if activated successfully and false if not owned by the player
     */
    public boolean setActivateLeaderTest(LeaderCard leaderCard) {
        if (leaderHand != null && leaderHand.contains(leaderCard)) {
            if (leaderCard.activate(this)) {
                leaderHand.remove(leaderCard);
                return true;
            }
        }
        return false;
    }

    /**
     * Activate specified leaderCard
     *
     * @param leaderCard leaderCard to activate
     * @return true if successfully activated
     */
    public boolean activateLeaderCard(LeaderCard leaderCard) {
        if (leaderHand != null && leaderHand.contains(leaderCard)) {
            if (leaderCard.canBeActivated(this)) {
                if (leaderCard.activate(this)) {
                    leaderHand.remove(leaderCard);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Discard leaderCard from player's hand
     *
     * @param leaderCard card to discard
     * @return true if successfully discarded and false if not owned by the player
     */
    public boolean discardLeader(LeaderCard leaderCard) {
        if (leaderHand.contains(leaderCard)) {
            leaderHand.remove(leaderCard);
            GameBoard.getGameBoard().faithProgress(this, 1);
            return true;
        }
        return false;
    }

    /**
     * Returns if the player is currently online
     * @return true if online
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * Method used to set a player as online/offline
     * @param online status to set the player as
     */
    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setDisconnectedData(State currentState, List<WhiteResource> whiteResourcesFromMarket, DevelopmentCard chosenDevCard,SendEvent lastReceivedEvent) {
        this.disconnectedState = currentState;
        this.whiteResourcesFromMarket = whiteResourcesFromMarket;
        this.chosenDevCard = chosenDevCard;
        this.lastReceivedEvent = lastReceivedEvent;
    }

    public State getDisconnectedState() {
        return disconnectedState;
    }

    public boolean prepareTurn(TurnLogic turnLogic) {
        if(disconnectedState== null){
            return false;
        }
        turnLogic.setWhiteResourcesFromMarket(whiteResourcesFromMarket);
        turnLogic.setChosenDevCard(chosenDevCard);
        turnLogic.setCurrentState(disconnectedState);
        turnLogic.getModelInterface().notifyObservers(lastReceivedEvent);
        turnLogic.setLastEventSent(lastReceivedEvent);
        disconnectedState = null;
        whiteResourcesFromMarket.clear();
        chosenDevCard = null;
        return true;
    }
}