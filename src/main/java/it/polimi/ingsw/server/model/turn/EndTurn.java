package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.player.Player;

public class EndTurn extends State {
    public EndTurn(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Check if there is a winner: if yes set the state of the game to EndGame, else Lorenzo plays and re-check if
     * there is a winner. If yes re-set the state of the game to EndGame, else set the next player and change
     * the state of the game to StartTurn.
     *
     * @return true if there is a winner
     * @throws InvalidEventException never
     */
    @Override
    public boolean endTurn() throws InvalidEventException {

        //check if the current player is the last player
        if(turnLogic.isLastPlayerTurn() && turnLogic.getGameMode().getICheckWinner().isTheGameOver()) {
            turnLogic.getGameMode().getICheckWinner().getWinner();//method return winner
            turnLogic.setCurrentState(turnLogic.getEndGame());
            //todo evento in uscita
            return true;
        }

        //lorenzo turn
        if(turnLogic.getGameMode().getLorenzo().play() && turnLogic.getGameMode().getICheckWinner().isTheGameOver()) {
            turnLogic.getGameMode().getICheckWinner().getWinner();//method return winner
            turnLogic.setCurrentState(turnLogic.getEndGame());
            //todo evento in uscita
            return true;
        }

        turnLogic.setNextPlayer();
        turnLogic.setCurrentState(turnLogic.getStartTurn());
        return false;
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
                .orElseThrow(() -> new InvalidEventException());
        //if the card has to be discarded
        if(discard){
            if(!currentPlayer.discardLeader(chosenLeaderCard))
                throw new InvalidEventException();
        }else
        //if the card has to be activated
        {
            if(!currentPlayer.activateLeaderCard(chosenLeaderCard))
                throw new InvalidEventException();
        }

        return endTurn();
    }
}
