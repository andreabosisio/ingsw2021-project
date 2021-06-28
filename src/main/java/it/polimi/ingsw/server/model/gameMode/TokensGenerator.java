package it.polimi.ingsw.server.model.gameMode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.utils.FileUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TokensGenerator {

    private final List<SoloActionToken> soloActionTokens = new ArrayList<>();
    private final String tokensNameInJson = "tokens";
    private final String colorNameInJson = "color";

    /**
     * Generate all the Solo Action Tokens from a Json file
     *
     * @return generated Solo Action Tokens
     */
    public List<SoloActionToken> generateSoloActionTokens() {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.getTokenFileName());
        assert fileElement != null;
        JsonObject fileObject = fileElement.getAsJsonObject();
        JsonArray jsonArrayOfTokens = fileObject.get(tokensNameInJson).getAsJsonArray();
        //Cycle through all tokens element in the file
        for (JsonElement tokenElement : jsonArrayOfTokens) {
            //Get Json object
            JsonObject tokenElementAsJsonObject = tokenElement.getAsJsonObject();
            extractTokensFromJson(tokenElementAsJsonObject);
        }
        return soloActionTokens;
    }

    /**
     * This method is used to extract the Tokens from a JsonArray contained in a jsonObject
     *
     * @param tokenElementAsJsonObject JsonObject containing the jsonArray with the Tokens data
     */
    private void extractTokensFromJson(JsonObject tokenElementAsJsonObject) {
        String typeOfToken = FileUtilities.getTypeFieldAsString(tokenElementAsJsonObject);
        switch (typeOfToken) {
            case "DiscardDevCardsToken":
                CardColorEnum colorOfToken = CardColorEnum.valueOf(tokenElementAsJsonObject.get(colorNameInJson).getAsString());
                soloActionTokens.add(new DiscardDevCardsToken(colorOfToken));
                break;

            case "DoubleFaithTrackProgressToken":
                soloActionTokens.add(new DoubleFaithTrackProgressToken());
                break;

            case "SingleFaithTrackProgressToken":
                soloActionTokens.add(new SingleFaithTrackProgressToken());
                break;
        }
    }
}