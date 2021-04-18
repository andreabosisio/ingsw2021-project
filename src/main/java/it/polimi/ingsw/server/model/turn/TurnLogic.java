package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.gameMode.GameMode;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.WhiteResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class TurnLogic {
    private final GameMode gameMode;
    private final List<Player> players;
    private Player currentPlayer;
    private State currentState;
    private final State startTurn, waitDevCardPlacement, waitTransformation, waitResourcePlacement, endTurn, endGame;
    private List<Resource> whiteResourcesFromMarket = new ArrayList<>();
    private DevelopmentCard chosenDevCard;

    public TurnLogic(List<Player> players) {
        this.players = players;
        this.currentPlayer = players.get(0);
        this.currentState = new StartTurn(this);
        GameBoard.getGameBoard().createFaithTracks(players);
        GameBoard.getGameBoard().setTurnLogicOfMarketTray(this);
        this.gameMode = new GameMode(players);
        this.setTheObservers();

        this.startTurn = new StartTurn(this);
        this.waitDevCardPlacement = new WaitDevCardPlacement(this);
        this.waitTransformation = new WaitTransformation(this);
        this.waitResourcePlacement = new WaitResourcePlacement(this);
        this.endTurn = new EndTurn(this);
        this.endGame = new EndGame(this);

    }

    /**
     * This method set all the observers for the correct functional of the two Observer Pattern.
     * One is used for the Faith Tracks to check the reach of a PopeSpace.
     * The other one is used by the classes that implement the interface ICheckWinner
     * to check all the condition of End of Game and to decree the Winner.
     */
    private void setTheObservers() {
        // Set the Observer of the Personal Boards
        for (Player player : players)
            player.getPersonalBoard().registerEndGameObserver(gameMode.getICheckWinner());
        // Set the Observer of the First Of Faith Track
        GameBoard.getGameBoard().setObserversOfFirstOfFaithTrack(GameBoard.getGameBoard().getFaithTracks(), gameMode.getICheckWinner());
        // Set the Observer of the Development Cards Grid
        GameBoard.getGameBoard().setObserverOfDevCardsGrid(gameMode.getICheckWinner());
    }

    public boolean isLastPlayerTurn(){
        return players.indexOf(currentPlayer) == players.size() - 1;
    }

    /**
     * Set the next player and reset the current value.
     */
    public void setNextPlayer() {
        if(isLastPlayerTurn())
            currentPlayer = players.get(0);
        else
            currentPlayer = players.get(players.indexOf(currentPlayer) + 1);

        //reset
        whiteResourcesFromMarket.clear();
        chosenDevCard = null;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public State getStartTurn() {
        return startTurn;
    }

    public State getWaitDevCardPlacement() {
        return waitDevCardPlacement;
    }

    public State getWaitTransformation() {
        return waitTransformation;
    }

    public State getWaitResourcePlacement() {
        return waitResourcePlacement;
    }

    public State getEndTurn() {
        return endTurn;
    }

    public State getEndGame() {
        return endGame;
    }

    public void setWhiteResourcesFromMarket(WhiteResource whiteResourceFromMarket) {
        this.whiteResourcesFromMarket.add(whiteResourceFromMarket);
    }

    public List<Resource> getWhiteResourcesFromMarket() {
        return whiteResourcesFromMarket;
    }

    public void setChosenDevCard(DevelopmentCard chosenDevCard) {
        this.chosenDevCard = chosenDevCard;
    }

    public DevelopmentCard getChosenDevCard() {
        return chosenDevCard;
    }


    public boolean marketAction(int arrowID) throws InvalidEventException, InvalidIndexException {
        return currentState.marketAction(arrowID);
    }
    public boolean productionAction(Map<Integer, List<Integer>> productionMap) throws InvalidEventException, InvalidIndexException, NonStorableResourceException {
        return currentState.productionAction(productionMap);
    }
    public boolean buyAction(String cardColor, int cardLevel, List<Integer> resourcesPositions) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        return currentState.buyAction(cardColor, cardLevel, resourcesPositions);
    }
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {
        return currentState.leaderAction(ID, discard);
    }
    public boolean placeResourceAction(List<Integer> swapPairs) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        return currentState.placeResourceAction(swapPairs);
    }
    public boolean placeDevCardAction(int slotPosition) throws InvalidEventException {
        return currentState.placeDevCardAction(slotPosition);
    }
    public boolean transformationAction(List<String> chosenColors) throws InvalidEventException, NonStorableResourceException {
        return currentState.transformationAction(chosenColors);
    }
    public boolean endTurn() throws InvalidEventException {
        return currentState.endTurn();
    }
}
