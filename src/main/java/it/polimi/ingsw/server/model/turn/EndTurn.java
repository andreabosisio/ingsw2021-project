package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.server.events.send.graphics.FaithTracksUpdate;
import it.polimi.ingsw.server.events.send.graphics.PersonalBoardUpdate;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.player.Player;

public class EndTurn extends State {
    public EndTurn(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Check if there is a winner: if yes set the state of the game to EndGame, else Lorenzo plays and re-check if
     * there is a winner. If yes set the state of the game to EndGame, else set the next player and change
     * the state of the game to StartTurn.
     *
     * @return true if there is a winner
     */
    @Override
    public boolean endTurn() {

        //check if the current player is the last player and check if it's the winner
        if(turnLogic.isLastPlayerTurn() && turnLogic.getGameMode().getICheckWinner().isTheGameOver()) {
            turnLogic.getGameMode().getICheckWinner().getWinner();//method return winner
            turnLogic.setCurrentState(turnLogic.getEndGame());
            //todo evento in uscita di endgame
            return true;
        }

        //lorenzo turn
        if(turnLogic.getGameMode().getLorenzo().play() && turnLogic.getGameMode().getICheckWinner().isTheGameOver()) {
            turnLogic.getGameMode().getICheckWinner().getWinner();//method return winner
            turnLogic.setCurrentState(turnLogic.getEndGame());
            //todo evento in uscita di endgame
            return true;
        }
        //todo graaphic update after Lorenzo turn(if above should be split in 2!)
        //reset and change player
        turnLogic.setNextPlayer();

        turnLogic.setCurrentState(turnLogic.getStartTurn());
        return true;
    }

    /**
     * Activate or Discard a LeaderCard. If done successfully call endTurn because another LeaderAction is not accepted.
     *
     * @param ID of the chosen LeaderCard
     * @param discard true if the chosen LeaderCard has to be discarded, false if has to be activated
     * @return true if the leaderAction has been successfully applied
     * @throws InvalidEventException if the leaderAction can't be applied
     */
    @Override
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {
        Player currentPlayer = turnLogic.getCurrentPlayer();

        //get the chosen leader card
        LeaderCard chosenLeaderCard = currentPlayer.getLeaderHand().stream()
                .filter(card -> card.getID().equals(ID)).findFirst()
                .orElseThrow(() -> new InvalidEventException("leaderCard is not owned"));
        //if the card has to be discarded
        if(discard){
            if(!currentPlayer.discardLeader(chosenLeaderCard))
                throw new InvalidEventException("can't discard this card");
            else {

                //graphic update of faithTracks and player's owned leaderCards

                turnLogic.getModelInterface().notifyObservers(new PersonalBoardUpdate(turnLogic.getCurrentPlayer()));
                turnLogic.getModelInterface().notifyObservers(new FaithTracksUpdate());
            }
        }else
        //if the card has to be activated
        {
            if(!currentPlayer.activateLeaderCard(chosenLeaderCard))
                throw new InvalidEventException("leaderCard activation failed");
            else {

                //graphic update of leaderCards owned by the player

                turnLogic.getModelInterface().notifyObservers(new PersonalBoardUpdate(turnLogic.getCurrentPlayer()));
            }
        }

        return endTurn();
    }
}
