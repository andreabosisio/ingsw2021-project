package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.gameMode.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenerateSoloActionTokensTest {
    @Test
    void generateSoloActionTokenTest() {
        List<SoloActionToken> soloActionTokens;
        TokensGenerator tokensGenerator = new TokensGenerator();
        soloActionTokens = tokensGenerator.generateSoloActionTokens();

        // Check that there are seven tokens of the three expected types
        assertEquals(7,soloActionTokens.size());
        assertEquals(4,soloActionTokens.stream().filter(token -> token instanceof DiscardDevCardsToken).count());
        assertEquals(1,soloActionTokens.stream().filter(token -> token instanceof SingleFaithTrackProgressToken).count());
        assertEquals(2,soloActionTokens.stream().filter(token -> token instanceof DoubleFaithTrackProgressToken).count());
    }
}