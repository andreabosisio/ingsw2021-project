package it.polimi.ingsw.server.model.turn;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.events.send.EndGameEvent;
import it.polimi.ingsw.server.exceptions.InvalidEventException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.PlayerInterface;

import java.util.ArrayList;

/**
 * State of the end of the Turn.
 */
public class EndTurnState extends State {
    public EndTurnState(ModelInterface modelInterface) {
        super(modelInterface);
    }

    /**
     * Check if there is a winner: if yes set the state of the game to EndGameState, else Lorenzo plays and re-check if
     * there is a winner. If yes set the state of the game to EndGameState, else set the next player and change
     * the state of the game to StartTurnState.
     *
     * @return true if there is a winner
     */
    @Override
    public boolean endTurn() {

        //check if the current player is the last player and check if it's the winner
        if (turnLogic.isLastPlayerTurn() && turnLogic.getGameMode().getICheckWinner().isTheGameOver()) {
            sendGraphicForEndGame();
            return true;
        }

        //lorenzo turn
        if (turnLogic.getGameMode().getLorenzo().play(turnLogic)) {
            //if lorenzo action ended the game
            if (turnLogic.getGameMode().getICheckWinner().isTheGameOver()) {
                sendGraphicForEndGame();
                return true;
            }
        }

        //reset and change player
        turnLogic.setNextPlayer();
        return true;
    }

    /**
     * Activate or Discard a LeaderCard. If done successfully call endTurn because another LeaderAction is not accepted.
     *
     * @param cardID  of the chosen LeaderCard
     * @param discard true if the chosen LeaderCard has to be discarded, false if has to be activated
     * @return true if the leaderAction has been successfully applied
     * @throws InvalidEventException if the leaderAction can't be applied
     */
    @Override
    public boolean leaderAction(String cardID, boolean discard) throws InvalidEventException {

        ((StartTurnState) modelInterface.getStartTurn()).executeLeaderAction(cardID, discard);

        return endTurn();
    }

    /**
     * Send the graphicUpdate necessary to display an end game screen for the player
     * It also set the turnLogic in the endGame state
     */
    private void sendGraphicForEndGame() {
        resetSavedData();

        PlayerInterface winner = turnLogic.getGameMode().getICheckWinner().getWinner();//method return winner
        modelInterface.setCurrentState(modelInterface.getEndGame());
        EndGameEvent endGameEvent = new EndGameEvent(winner, modelInterface.getPlayers());
        modelInterface.notifyObservers(endGameEvent);
    }

    /**
     * This method is used to reset the saveData file as this game is no longer worthy of reload
     */
    private void resetSavedData() {
        FileUtilities.resetGameData(new ArrayList<>());
    }
}
