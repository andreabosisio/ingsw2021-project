package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.server.events.send.EndGameEvent;
import it.polimi.ingsw.server.events.send.graphics.FaithTracksUpdate;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.events.send.graphics.LeaderCardSlotsUpdate;
import it.polimi.ingsw.server.events.send.graphics.PersonalBoardUpdate;
import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.player.Player;

public class EndTurnState extends State {
    public EndTurnState(TurnLogic turnLogic) {
        super(turnLogic);
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
        if(turnLogic.isLastPlayerTurn() && turnLogic.getGameMode().getICheckWinner().isTheGameOver()) {
            PlayerInterface winner = turnLogic.getGameMode().getICheckWinner().getWinner();//method return winner
            turnLogic.setCurrentState(turnLogic.getEndGame());
            EndGameEvent endGameEvent = new EndGameEvent(winner,turnLogic.getPlayers());
            turnLogic.getModelInterface().notifyObservers(endGameEvent);
            return true;
        }

        //lorenzo turn
        if(turnLogic.getGameMode().getLorenzo().play(turnLogic)) {
            //if lorenzo action ended the game
            if(turnLogic.getGameMode().getICheckWinner().isTheGameOver()) {
                PlayerInterface winner = turnLogic.getGameMode().getICheckWinner().getWinner();//method return winner
                turnLogic.setCurrentState(turnLogic.getEndGame());
                EndGameEvent endGameEvent = new EndGameEvent(winner, turnLogic.getPlayers());
                turnLogic.getModelInterface().notifyObservers(endGameEvent);
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
                .orElseThrow(() -> new InvalidEventException("LeaderCard is not owned"));
        //if the card has to be discarded
        if(discard){
            if(!currentPlayer.discardLeader(chosenLeaderCard))
                throw new InvalidEventException("You cannot discard this card right now");
            else {

                //graphic update of faithTracks and player's owned leaderCards
                GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer(), new LeaderCardSlotsUpdate()));
                graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
                graphicUpdateEvent.addUpdate( turnLogic.getCurrentPlayer().getNickname() + " discarded a Leader Card!");
                turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            }
        }else
        //if the card has to be activated
        {
            if(!currentPlayer.activateLeaderCard(chosenLeaderCard))
                throw new InvalidEventException("You cannot activate this card right now");
            else {
                //graphic update of leaderCards owned by the player
                GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer(), new LeaderCardSlotsUpdate()));
                graphicUpdateEvent.addUpdate(turnLogic.getCurrentPlayer().getNickname() + " activated a Leader Card!");
                turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            }
        }
        return endTurn();
    }
}
