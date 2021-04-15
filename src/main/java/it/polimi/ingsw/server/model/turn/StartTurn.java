package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.List;
import java.util.Map;

public class StartTurn extends State {
    public StartTurn(TurnLogic turnLogic) {
        super(turnLogic);
    }

    @Override
    public boolean marketAction(int arrowID) throws InvalidIndexException, NonStorableResourceException {
        if(turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()
                .addResourcesFromMarket(GameBoard.getGameBoard().getMarketTray().takeResources(arrowID))){
            if(turnLogic.getWhiteResourcesFromMarket().size()>0){
                //todo evento in uscita
                turnLogic.setCurrentState(turnLogic.getWaitTransformation());
                return true;
            }
            //todo evento in uscita
            turnLogic.setCurrentState(turnLogic.getWaitResourcePlacement());
        }
        return false;

    }

    @Override
    public boolean productionAction(Map<Integer, List<Integer>> productionMap) throws InvalidEventException {
        return super.productionAction(productionMap);
    }

    @Override
    public boolean buyAction(int cardGridIndex, List<Integer> positions) throws InvalidEventException {
        return super.buyAction(cardGridIndex, positions);
    }

    @Override
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {
        return super.leaderAction(ID, discard);
    }
}
