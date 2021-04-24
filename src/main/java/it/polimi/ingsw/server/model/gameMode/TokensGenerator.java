package it.polimi.ingsw.server.model.gameMode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.server.model.enums.CardColorEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TokensGenerator {

    private final String tokenFileName ="src/main/resources/soloActionTokens.json";
    private final List<SoloActionToken> soloActionTokens = new ArrayList<>();

    public List<SoloActionToken> generateSoloActionTokens(){
        File input = new File(tokenFileName);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfTokens = fileObject.get("tokens").getAsJsonArray();

            //Cycle through all tokens element in the file
            for(JsonElement tokenElement:jsonArrayOfTokens) {

                //Get Json object
                JsonObject tokenElementAsJsonObject = tokenElement.getAsJsonObject();
                String typeOfToken = tokenElementAsJsonObject.get("type").getAsString();
                switch (typeOfToken) {
                    case "DiscardDevCardsToken":
                        CardColorEnum colorOfToken = CardColorEnum.valueOf(tokenElementAsJsonObject.get("color").getAsString());
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
        catch (FileNotFoundException e){
            System.err.println("file not found");
            e.printStackTrace();}
        catch (Exception e){
            System.err.println("error format in file json");
            e.printStackTrace();}
        return soloActionTokens;
    }
}