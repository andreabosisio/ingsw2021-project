package it.polimi.ingsw.server.model.cards;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CardsGenerator {
    private final String developmentCardsFileName = "src/main/resources/developmentCards.json";
    private final String leaderCardsFileName = "src/main/resources/leaderCards.json";
    private List<DevelopmentCard> developmentCards = new ArrayList<>();
    private List<LeaderCard> leaderCards = new ArrayList<>();

    /**
     * Generate all the developmentCards from a Json file
     *
     * @return generated developmentCards
     */
    public List<DevelopmentCard> generateDevelopmentCards() {
        developmentCards = new ArrayList<>();
        File input = new File(developmentCardsFileName);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfCards = fileObject.get("cards").getAsJsonArray();

            //Cycle through all cards element in the file
            //todo assign ID in json
            int cardID = 1;
            for (JsonElement cardElement : jsonArrayOfCards) {
                //Get Json object
                JsonObject cardJsonObject = cardElement.getAsJsonObject();

                //Cycle through requirements and save them as list of requirements
                List<Resource> price = new ArrayList<>();
                JsonArray jsonArrayOfPrices = cardJsonObject.get("price").getAsJsonArray();
                for (JsonElement priceElement : jsonArrayOfPrices) {
                    JsonObject priceJsonObject = priceElement.getAsJsonObject();
                    int quantity = priceJsonObject.get("quantity").getAsInt();
                    ResourceEnum color = ResourceEnum.valueOf(priceJsonObject.get("color").getAsString());
                    for(int i = 0; i < quantity; i++){
                        price.add(new StorableResource(color));
                    }

                }
                List<Resource> inResources = new ArrayList<>();
                JsonArray jsonArrayOfInResources = cardJsonObject.get("inResources").getAsJsonArray();
                for (JsonElement inResourcesElement : jsonArrayOfInResources) {
                    JsonObject inResourcesJsonObject = inResourcesElement.getAsJsonObject();
                    int quantity = inResourcesJsonObject.get("quantity").getAsInt();
                    ResourceEnum color = ResourceEnum.valueOf(inResourcesJsonObject.get("color").getAsString());
                    addNewResource(inResources, color, quantity);

                }
                List<Resource> outResources = new ArrayList<>();
                JsonArray jsonArrayOfOutResources = cardJsonObject.get("outResources").getAsJsonArray();
                for (JsonElement outResourcesElement : jsonArrayOfOutResources) {
                    JsonObject outResourcesJsonObject = outResourcesElement.getAsJsonObject();
                    int quantity = outResourcesJsonObject.get("quantity").getAsInt();
                    ResourceEnum color = ResourceEnum.valueOf(outResourcesJsonObject.get("color").getAsString());
                    addNewResource(outResources, color, quantity);

                }
                CardColorEnum color = CardColorEnum.valueOf(cardJsonObject.get("color").getAsString());
                int level = cardJsonObject.get("level").getAsInt();
                int points = cardJsonObject.get("points").getAsInt();
                //String iD = "cardID:" + cardID;
                String iD = color + "_" + level + "_" + cardID;
                cardID++;

                developmentCards.add(new DevelopmentCard(iD, inResources, outResources, price, color, points, level));
            }
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("errore format nel file json");
            e.printStackTrace();
        }
        return developmentCards;
    }

    /**
     * Add the required number of resources of the specified color.
     *
     * @param resources list were the resources will be added
     * @param color     color of the resources to add
     * @param quantity  number of resources of that color to add
     */
    private void addNewResource(List<Resource> resources, ResourceEnum color, int quantity) {
        if (color == ResourceEnum.RED) {
            for (int i = 0; i < quantity; i++) {
                resources.add(new RedResource());
            }
        } else {
            for (int i = 0; i < quantity; i++) {
                resources.add(new StorableResource(color));
            }
        }
    }

    /**
     * Generate all the leaderCards from a Json file
     *
     * @return generated leaderCards
     */
    public List<LeaderCard> generateLeaderCards() {
        leaderCards = new ArrayList<>();
        File input = new File(leaderCardsFileName);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfLeaders = fileObject.get("leaders").getAsJsonArray();

            //Cycle through all leaders element in the file
            for (JsonElement leaderElement : jsonArrayOfLeaders) {

                //Get Json object
                JsonObject leaderJsonObject = leaderElement.getAsJsonObject();

                //Cycle through dev and res Requirements and save them as a list of requirements
                List<Requirement> requirements = new ArrayList<>();
                if (leaderJsonObject.has("devRequirements")) {
                    JsonArray jsonArrayOfRequirements = leaderJsonObject.get("devRequirements").getAsJsonArray();
                    for (JsonElement requirementsElement : jsonArrayOfRequirements) {
                        JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();
                        CardColorEnum color = CardColorEnum.valueOf(requirementJsonObject.get("color").getAsString());
                        int level = requirementJsonObject.get("level").getAsInt();
                        int quantity = requirementJsonObject.get("quantity").getAsInt();
                        DevelopmentRequirement developmentRequirement = new DevelopmentRequirement(level, color, quantity);
                        requirements.add(developmentRequirement);
                    }
                }
                if (leaderJsonObject.has("resRequirements")) {
                    JsonArray jsonArrayOfRequirements = leaderJsonObject.get("resRequirements").getAsJsonArray();
                    for (JsonElement requirementsElement : jsonArrayOfRequirements) {
                        JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();
                        ResourceEnum color = ResourceEnum.valueOf(requirementJsonObject.get("color").getAsString());
                        int quantity = requirementJsonObject.get("quantity").getAsInt();
                        ResourceRequirement resourceRequirement = new ResourceRequirement(color, quantity);
                        requirements.add(resourceRequirement);
                    }
                }

                //extract common data to all leaderCards
                String id = leaderJsonObject.get("id").getAsString();
                int points = leaderJsonObject.get("points").getAsInt();
                String type = leaderJsonObject.get("type").getAsString();
                LeaderCard card;

                //Create specific type of leaderCard asking the file for the specific parameters
                switch (type) {
                    case "production":
                        Resource inResource = new StorableResource(ResourceEnum.valueOf(leaderJsonObject.get("inResource").getAsString()));
                        card = new ProductionLeaderCard(id, points, requirements, inResource);
                        break;
                    case "market":
                        Resource transformation = new StorableResource(ResourceEnum.valueOf(leaderJsonObject.get("transformation").getAsString()));
                        card = new TransformationLeaderCard(id, points, requirements, transformation);
                        break;
                    case "warehouse":
                        Resource extraSlotsType = new StorableResource(ResourceEnum.valueOf(leaderJsonObject.get("extraSlotsType").getAsString()));
                        card = new WarehouseLeaderCard(id, points, requirements, extraSlotsType);
                        break;
                    case "discount":
                        Resource discountResource = new StorableResource(ResourceEnum.valueOf(leaderJsonObject.get("discount").getAsString()));
                        card = new DiscountLeaderCard(id, points, requirements, discountResource);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected leaderCardType: " + type);
                }
                leaderCards.add(card);
            }

        }
        catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("errore nel file json");
            e.printStackTrace();
        }
        return leaderCards;
    }

    /**
     * Create map using CardColorEnum as key of devCards of the specified level.
     *
     * @param devCards List of cards to map
     * @param level    Level of the cards i want to include in the map
     * @return Map</ CardsColorEnum, List>
     */
    public Map<CardColorEnum, List<DevelopmentCard>> getDevCardsAsGrid(List<DevelopmentCard> devCards, int level) {
        return devCards.stream().filter((el) -> el.getLevel() == level).collect(Collectors.groupingBy(DevelopmentCard::getColor));
    }
}
