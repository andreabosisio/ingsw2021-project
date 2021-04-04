package it.polimi.ingsw.server.model.gameBoard;

import com.google.gson.*;
import it.polimi.ingsw.server.model.cards.*;
import it.polimi.ingsw.server.model.enums.CardColor;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DeckLeader {
    private static List<LeaderCard> cards = new ArrayList<>();
    private static final String fileName = "src/main/resources/leaderCards.json";

    public DeckLeader() {
        File input = new File(fileName);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfLeaders = fileObject.get("leaders").getAsJsonArray();

            //Cycle through all leaders element in the file
            for(JsonElement leaderElement:jsonArrayOfLeaders){

                //Get Json object
                JsonObject leaderJsonObject = leaderElement.getAsJsonObject();

                //Cycle through requirements and save them as list of requirements
                List<Requirement> requirements = new ArrayList<>();
                if(leaderJsonObject.has("devRequirements")) {
                    JsonArray jsonArrayOfRequirements = leaderJsonObject.get("devRequirements").getAsJsonArray();
                    for (JsonElement requirementsElement : jsonArrayOfRequirements) {
                        JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();
                        CardColor color = CardColor.valueOf(requirementJsonObject.get("color").getAsString());
                        int level = requirementJsonObject.get("level").getAsInt();
                        int quantity = requirementJsonObject.get("quantity").getAsInt();
                        DevelopmentRequirement developmentRequirement = new DevelopmentRequirement(level,color,quantity);
                        requirements.add(developmentRequirement);
                    }
                }
                if(leaderJsonObject.has("resRequirements")) {
                    JsonArray jsonArrayOfRequirements = leaderJsonObject.get("resRequirements").getAsJsonArray();
                    for (JsonElement requirementsElement : jsonArrayOfRequirements) {
                        JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();
                        ResourceEnum color = ResourceEnum.valueOf(requirementJsonObject.get("color").getAsString());
                        int quantity = requirementJsonObject.get("quantity").getAsInt();
                        ResourceRequirement resourceRequirement = new ResourceRequirement(color,quantity);
                        requirements.add(resourceRequirement);
                    }
                }

                //extract common data to all leaderCards
                String id = leaderJsonObject.get("id").getAsString();
                int points = leaderJsonObject.get("points").getAsInt();
                String type = leaderJsonObject.get("type").getAsString();
                LeaderCard card;

                //Create specific type of leaderCard asking the file for the specific parameters
                switch (type){
                    case "production":
                        //TODO in resources for leaderCard is always one should we use a list anyway?
                        Resource inResource = new OtherResource(ResourceEnum.valueOf(leaderJsonObject.get("inResource").getAsString()));
                        card = new LeaderCardProduction(id,points,requirements,inResource);
                        break;
                    case "market":
                        Resource transformation = new OtherResource(ResourceEnum.valueOf(leaderJsonObject.get("transformation").getAsString()));
                        card = new LeaderCardMarket(id,points,requirements,transformation);
                        break;
                    case "warehouse":
                        Resource extraSlotsType = new OtherResource(ResourceEnum.valueOf(leaderJsonObject.get("extraSlotsType").getAsString()));
                        card = new LeaderCardWarehouse(id,points,requirements,extraSlotsType);
                        break;
                    case "discount":
                        Resource discountResource = new OtherResource(ResourceEnum.valueOf(leaderJsonObject.get("discount").getAsString()));
                        card = new LeaderCardDiscount(id,points,requirements,discountResource);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected leaderCardType: " + type);
                }
                cards.add(card);
            }

        }
        catch (FileNotFoundException e){
            System.err.println("file not found");
            e.printStackTrace();}
        catch (Exception e){
            System.err.println("errore nel file json");
            e.printStackTrace();}
    }

    /**
     * shuffle all the LeaderCards currently in the deck
     */

    public void shuffle(){
        Collections.shuffle(cards);
    }

    /**
     * Draws the top 4 cards of the deck removing them
     * @return a{@link ArrayList<LeaderCard> of the cards drawn}
     */
    public List<LeaderCard> draw(){
        if(cards.size()<4){
            throw new IndexOutOfBoundsException();
        }
        List<LeaderCard> drawn = new ArrayList<>();
        for(int i = 0;i<4;i++) {
            drawn.add(cards.get(i));
        }
        cards.removeAll(drawn);
        return drawn;
    }
}
