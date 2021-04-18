package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.PersonalBoard;

import java.util.Collections;
import java.util.List;

/**
 * This class represents the Lorenzo Player.
 */
public class LorenzoAI implements Lorenzo{
    private final List<SoloActionToken> soloActionTokens;
    int tokensDeckIndex = 0;

    public LorenzoAI() {
        TokensGenerator tokensGenerator = new TokensGenerator();
        soloActionTokens = tokensGenerator.generateSoloActionTokens();
        this.shuffle();
        GameBoard.getGameBoard().createLorenzoFaithTrack(this);
    }

    /**
     * This method do the Lorenzo's turn.
     * It draws a Solo Action Token and calls the method shuffle
     * if the token drawn is a SingleFaithTrackProgressToken.
     *
     * @return true
     */
    @Override
    public boolean play() {
        if (soloActionTokens.get(tokensDeckIndex).doAction(this)) {
            tokensDeckIndex = 0;
            this.shuffle();
        } else {
            tokensDeckIndex++;
        }
        return true;
    }

    /**
     * shuffle all the Solo Action Tokens
     */
    private void shuffle(){
        Collections.shuffle(soloActionTokens);
    }

    /**
     * Get method that
     *
     * @return Lorenzo's nickname
     */
    @Override
    public String getNickName() {
        return "Lorenzo il Magnifico";
    }

    @Override
    public PersonalBoard getPersonalBoard() {
        return null;
    }
}