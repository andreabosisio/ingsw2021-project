package it.polimi.ingsw.server.model.cards;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.server.model.enums.CardColor;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class GenerateCards {
    private static final String fileName="src/main/resources/developmentCards.json";
    private static List<DevelopmentCard> cards;


    /**
     * Generate all the developmentCards from a Json file
     * @return generated developmentCards
     */

    public List<DevelopmentCard> generateCards(){
        cards= new ArrayList<>();
        File input = new File(fileName);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfCards = fileObject.get("cards").getAsJsonArray();

            //Cycle through all leaders element in the file
            for(JsonElement cardElement:jsonArrayOfCards){

                //Get Json object
                JsonObject cardJsonObject = cardElement.getAsJsonObject();

                //Cycle through requirements and save them as list of requirements
                List<Resource> price = new ArrayList<>();
                JsonArray jsonArrayOfPrices = cardJsonObject.get("price").getAsJsonArray();
                for (JsonElement priceElement : jsonArrayOfPrices) {
                    JsonObject priceJsonObject = priceElement.getAsJsonObject();
                    int quantity = priceJsonObject.get("quantity").getAsInt();
                    ResourceEnum color = ResourceEnum.valueOf(priceJsonObject.get("color").getAsString());
                    for(int i=0;i<quantity;i++){
                        price.add(new OtherResource(color));
                    }

                }
                List<Resource> inResources = new ArrayList<>();
                JsonArray jsonArrayOfinResources = cardJsonObject.get("inResources").getAsJsonArray();
                for (JsonElement inResourcesElement : jsonArrayOfinResources) {
                    JsonObject inResourcesJsonObject = inResourcesElement.getAsJsonObject();
                    int quantity = inResourcesJsonObject.get("quantity").getAsInt();
                    ResourceEnum color = ResourceEnum.valueOf(inResourcesJsonObject.get("color").getAsString());
                    addNewResource(inResources,color,quantity);

                }
                List<Resource> outResources = new ArrayList<>();
                JsonArray jsonArrayOfoutResources = cardJsonObject.get("outResources").getAsJsonArray();
                for (JsonElement outResourcesElement : jsonArrayOfoutResources) {
                    JsonObject outResourcesJsonObject = outResourcesElement.getAsJsonObject();
                    int quantity = outResourcesJsonObject.get("quantity").getAsInt();
                    ResourceEnum color = ResourceEnum.valueOf(outResourcesJsonObject.get("color").getAsString());
                    addNewResource(outResources,color,quantity);

                }
                CardColor color = CardColor.valueOf(cardJsonObject.get("color").getAsString());
                int level = cardJsonObject.get("level").getAsInt();
                int points = cardJsonObject.get("points").getAsInt();
                cards.add(new DevelopmentCard(inResources,outResources,price,color,points,level));
            }
        }
        catch (FileNotFoundException e){
            System.err.println("file not found");
            e.printStackTrace();}
        catch (Exception e){
            System.err.println("errore format nel file json");
            e.printStackTrace();}
        return cards;
    }

    /**
     * Add the required number of resources of the specified color
     * @param resources list were the resources will be added
     * @param color color of the resources to add
     * @param quantity number of resources of that color to add
     */

    private void addNewResource(List<Resource> resources,ResourceEnum color,int quantity){
        if (color == ResourceEnum.RED) {
            for (int i = 0; i < quantity; i++) {
                resources.add(new RedResource());
            }
        } else {
            for (int i = 0; i < quantity; i++) {
                resources.add(new OtherResource(color));
            }
        }
    }

}
