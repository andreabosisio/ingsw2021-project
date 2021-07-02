package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.player.Player;

import java.util.List;

/**
 * The task of this class is to set the correct Strategy.
 * When the method constructor is called,
 * if there is only one Player the method creates a new LorenzoAI and a new SinglePlayerCheckWinner
 * otherwise it creates a new LorenzoDoingNothing and a new MultiPlayerCheckWinner.
 */
public class GameMode {
    private final Lorenzo lorenzo;
    private final ICheckWinner iCheckWinner;

    public GameMode(List<Player> players) {
        if (players.size() == 1) {
            lorenzo = new LorenzoAI();
            iCheckWinner = new SinglePlayerWinnerChecker(lorenzo, players.get(0));
        } else {
            lorenzo = new LorenzoDoingNothing();
            iCheckWinner = new MultiPlayerWinnerChecker(players);
        }
    }

    /**
     * Get method that
     *
     * @return the reference of lorenzo
     */
    public Lorenzo getLorenzo() {
        return lorenzo;
    }

    /**
     * Get method that
     *
     * @return the reference of iCheckWinner
     */
    public ICheckWinner getICheckWinner() {
        return iCheckWinner;
    }
}