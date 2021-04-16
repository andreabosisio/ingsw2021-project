package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;

public class EndTurn extends State {
    public EndTurn(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Check if there is a winner: if yes set the state of the game to EndGame, else Lorenzo plays and re-check if
     * there is a winner.
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

        return false;
    }

    @Override
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {
        return super.leaderAction(ID, discard);
    }
}
