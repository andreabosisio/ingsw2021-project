package it.polimi.ingsw.server.model.gameMode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.events.send.StartTurnEvent;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.Collections;
import java.util.List;

/**
 * This class represents the Lorenzo Player.
 */
public class LorenzoAI implements Lorenzo {
    private final List<SoloActionToken> soloActionTokens;
    int tokensDeckIndex = 0;
    private final TokensGenerator tokensGenerator;

    public LorenzoAI() {
        tokensGenerator = new TokensGenerator();
        soloActionTokens = tokensGenerator.generateSoloActionTokens();
        shuffle();
        GameBoard.getGameBoard().createLorenzoFaithTrack(this);
    }

    /**
     * This method performs the Lorenzo's turn.
     * It draws a Solo Action Token and calls the method shuffle
     * if the token drawn is a SingleFaithTrackProgressToken.
     *
     * @param turnLogic is the TurnLogic reference
     * @return true if the deck is shuffled
     */
    @Override
    public boolean play(TurnLogic turnLogic) {
        //notify that lorenzo is playing his turn
        turnLogic.getModelInterface().notifyObservers(new StartTurnEvent(this.getNickname()));
        SoloActionToken drawnToken = soloActionTokens.get(tokensDeckIndex);
        saveToken(drawnToken);
        if (drawnToken.doAction(this, turnLogic)) {
            tokensDeckIndex = 0;
            shuffle();
        } else {
            tokensDeckIndex++;
        }
        return true;
    }

    /**
     * This method saves the token in a file
     *
     * @param drawnToken token to save
     */
    private void saveToken(SoloActionToken drawnToken) {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.SOLO_SAVED_TOKEN_PATH);
        if (fileElement != null && !fileElement.isJsonNull()) {
            JsonArray savedTokens = Parser.extractFromField(fileElement, "savedTokens").getAsJsonArray();
            savedTokens.add(Parser.toJsonTree(drawnToken));
            FileUtilities.writeJsonElementInFile(fileElement, FileUtilities.SOLO_SAVED_TOKEN_PATH);
        }
        else{
            JsonObject tokensObject = new JsonObject();
            JsonArray savedTokens = new JsonArray();
            savedTokens.add(Parser.toJsonTree(drawnToken));
            tokensObject.add("savedTokens",savedTokens);
            FileUtilities.writeJsonElementInFile(tokensObject, FileUtilities.SOLO_SAVED_TOKEN_PATH);
        }
    }

    /**
     * Shuffle all the Solo Action Tokens.
     */
    private void shuffle() {
        Collections.shuffle(soloActionTokens);
    }

    /**
     * Method used for testing.
     *
     * @return the extracted Solo Action Token
     */
    @Override
    public SoloActionToken extractToken() {
        return soloActionTokens.get(tokensDeckIndex);
    }

    /**
     * load the solo action tokens saved in the appropriate file
     */
    @Override
    public void loadSavedTokens() {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.SOLO_SAVED_TOKEN_PATH);
        soloActionTokens.clear();
        if (fileElement != null && !fileElement.isJsonNull()) {
            JsonArray jsonArrayOfTokens = Parser.extractFromField(fileElement, "savedTokens").getAsJsonArray();
            for (JsonElement element : jsonArrayOfTokens) {
               soloActionTokens.add(ServerParser.getTokenFromJsonElement(element));
            }
        }
        FileUtilities.resetTokensData();
    }

    /**
     * This method loads the tokens used in a normal game
     */
    @Override
    public void generateNormalTokens() {
        soloActionTokens.clear();
        soloActionTokens.addAll(tokensGenerator.generateSoloActionTokens());
        tokensDeckIndex = 0;
    }

    /**
     * Get method that return Lorenzo's nickname.
     *
     * @return Lorenzo's nickname
     */
    @Override
    public String getNickname() {
        return "Lorenzo il Magnifico";
    }
}