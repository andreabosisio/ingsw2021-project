package it.polimi.ingsw.server.model.turn;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.events.send.EndGameEvent;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.player.Player;

import java.util.ArrayList;

/**
 * State of the end of the Game.
 */
public class EndGameState extends State {

    public EndGameState(ModelInterface modelInterface) {
        super(modelInterface);
    }

    @Override
    public boolean disconnectAction(String nickname) {

        Player disconnected = modelInterface.getPlayerByNickname(nickname);
        assert disconnected != null;
        disconnected.setOnline(false);
        if (modelInterface.getPlayers().stream().noneMatch(Player::isOnline))
            return true;
        if (getCurrentPlayer().equals(disconnected)) {
            getCurrentPlayer().setDisconnectedData(modelInterface.getCurrentState(), turnLogic.getWhiteResourcesFromMarket(), turnLogic.getChosenDevCard(), turnLogic.getLastEventSent());
        }
        return false;
    }

    @Override
    public void reconnectAction(String nickname) {
        PlayerInterface winner = modelInterface.getTurnLogic().getGameMode().getICheckWinner().getWinner();//method return winner
        EndGameEvent endGameEvent = new EndGameEvent(winner, modelInterface.getPlayers());
        modelInterface.notifyObservers(endGameEvent);
        Player reconnected = modelInterface.getPlayerByNickname(nickname);
        assert reconnected != null;
        reconnected.setOnline(true);
    }

    /**
     * This method is used to reset the saveData file as this game is no longer worthy of reload
     */
    private void resetSavedData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("players", Parser.toJsonTree(new ArrayList<>()));
        jsonObject.add("actions", new JsonArray());
        FileUtilities.writeJsonElementInFile(jsonObject, FileUtilities.SAVED_GAME_PATH);
    }

}
