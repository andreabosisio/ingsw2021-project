package it.polimi.ingsw.server.model.turn;


public class EndGameState extends State{
    public EndGameState(TurnLogic turnLogic) {
        super(turnLogic);
    }

    public void endGame(){
        //todo ask to play again?
    }
}
