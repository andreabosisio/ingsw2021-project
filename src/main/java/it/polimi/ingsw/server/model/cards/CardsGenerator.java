package it.polimi.ingsw.server.model.cards;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.StorableResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Generates the Cards by reading data from a file.
 */
public class CardsGenerator {
    private final static String mainDevJsonArrayName = "cards";
    private final static String mainLeaderJsonArrayName = "leaders";
    private final static String resAndDevColorNameInJson = "color";
    private final static String levelNameInJson = "level";
    private final static String quantityNameInJson = "quantity";
    private final static String resourcesRequirementNameInJson = "resRequirements";
    private final static String developmentRequirementNameInJson = "devRequirements";
    private final static String pointsNameInJson = "points";
    private final static String idNameInJson = "id";
    private final static String priceNameInJson = "price";
    private final static String inResourcesNameInJson = "inResources";
    private final static String outResourcesNameInJson = "outResources";
    private final static String marketTypeNameInJson = "market";
    private final static String discountTypeNameInJson = "discount";
    private final static String productionTypeNameInJson = "production";
    private final static String warehouseTypeNameInJson = "warehouse";
    private final List<DevelopmentCard> developmentCards = new ArrayList<>();
    private final List<LeaderCard> leaderCards = new ArrayList<>();

    /**
     * Generate all the developmentCards from a Json file
     *
     * @return generated developmentCards
     */
    public List<DevelopmentCard> generateDevelopmentCards() {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.UNMODIFIABLE_DEVELOPMENT_CARDS_PATH);
        JsonArray jsonArrayOfCards = Parser.extractFromField(Objects.requireNonNull(fileElement), mainDevJsonArrayName).getAsJsonArray();

        int cardID = 1;
        for (JsonElement cardElement : jsonArrayOfCards) {
            JsonObject cardJsonObject = cardElement.getAsJsonObject();
            developmentCards.add(generateSingleDevCardFromJsonObject(cardJsonObject, cardID));
            cardID++;
        }
        return developmentCards;
    }

    /**
     * This method is used to generate a single development card from a jsonObject containing its data
     * The card is also assigned an ID structured like this:(color_level_absoluteCardID)
     *
     * @param cardJsonObject JsonObject containing the data of the card
     * @param cardID         Absolute ID of the card
     * @return generated card
     */
    private DevelopmentCard generateSingleDevCardFromJsonObject(JsonObject cardJsonObject, int cardID) {
        //Cycle through requirements and save them as list of requirements
        List<Resource> price = new ArrayList<>();
        addCardPrice(price, cardJsonObject);

        List<Resource> inResources = new ArrayList<>();
        addInResources(inResources, cardJsonObject);

        List<Resource> outResources = new ArrayList<>();
        addOutResources(outResources, cardJsonObject);

        CardColorsEnum color = CardColorsEnum.valueOf(cardJsonObject.get(resAndDevColorNameInJson).getAsString());
        int level = cardJsonObject.get(levelNameInJson).getAsInt();
        int points = cardJsonObject.get(pointsNameInJson).getAsInt();
        //String iD = "cardID:" + cardID;
        String iD = color + "_" + level + "_" + cardID;
        return new DevelopmentCard(iD, inResources, outResources, price, color, points, level);
    }


    /**
     * This method is used to create and add the price of resources of a specific development card to a list
     *
     * @param price          List to populate with the price
     * @param cardJsonObject JsonObject containing the card data
     */
    private void addCardPrice(List<Resource> price, JsonObject cardJsonObject) {
        JsonArray jsonArrayOfPrices = cardJsonObject.get(priceNameInJson).getAsJsonArray();
        for (JsonElement priceElement : jsonArrayOfPrices) {
            JsonObject priceJsonObject = priceElement.getAsJsonObject();
            int quantity = priceJsonObject.get(quantityNameInJson).getAsInt();
            ResourcesEnum color = ResourcesEnum.valueOf(priceJsonObject.get(resAndDevColorNameInJson).getAsString());
            for (int i = 0; i < quantity; i++) {
                price.add(new StorableResource(color));
            }

        }
    }

    /**
     * This method is used to add the price of resources to activate a production of a specific devCard to a list
     *
     * @param inResources    List to populate with the resources required
     * @param cardJsonObject JsonObject containing the card data
     */
    private void addInResources(List<Resource> inResources, JsonObject cardJsonObject) {
        extractResourcesFromJson(inResources, cardJsonObject, inResourcesNameInJson);
    }

    /**
     * This method is used to add the resources produced by a specific devCard during a production to a list
     *
     * @param outResources   List to populate with the resources produced
     * @param cardJsonObject JsonObject containing the card data
     */
    private void addOutResources(List<Resource> outResources, JsonObject cardJsonObject) {
        extractResourcesFromJson(outResources, cardJsonObject, outResourcesNameInJson);
    }

    /**
     * This method is used to create resources from a JsonArray contained in a jsonObject
     *
     * @param resourcesListToPopulate  List where to add the resources
     * @param cardJsonObject           JsonObject containing the jsonArray with the resources data
     * @param resourcesArrayNameInJson name of the JsonArray in the JsonObject
     */
    private void extractResourcesFromJson(List<Resource> resourcesListToPopulate, JsonObject cardJsonObject, String resourcesArrayNameInJson) {
        JsonArray jsonArrayOfOutResources = cardJsonObject.get(resourcesArrayNameInJson).getAsJsonArray();
        for (JsonElement outResourcesElement : jsonArrayOfOutResources) {
            JsonObject outResourcesJsonObject = outResourcesElement.getAsJsonObject();
            int quantity = outResourcesJsonObject.get(quantityNameInJson).getAsInt();
            ResourcesEnum color = ResourcesEnum.valueOf(outResourcesJsonObject.get(resAndDevColorNameInJson).getAsString());
            addNewResource(resourcesListToPopulate, color, quantity);
        }
    }

    /**
     * Add the required number of resources of the specified color to a list.
     *
     * @param resources list were the resources will be added
     * @param color     color of the resources to add
     * @param quantity  number of resources of that color to add
     */
    private void addNewResource(List<Resource> resources, ResourcesEnum color, int quantity) {
        if (color == ResourcesEnum.RED) {
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
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.UNMODIFIABLE_LEADER_CARDS_PATH);
        JsonArray jsonArrayOfLeaders = Parser.extractFromField(Objects.requireNonNull(fileElement), mainLeaderJsonArrayName).getAsJsonArray();

        for (JsonElement leaderElement : jsonArrayOfLeaders) {//Cycle through all leaders element in the file
            JsonObject leaderJsonObject = leaderElement.getAsJsonObject();
            leaderCards.add(generateLeaderCardFromJsonObject(leaderJsonObject));
        }
        return leaderCards;
    }

    /**
     * This method is used to create a single Leader card from a jsonObject data
     * A LeaderCard data consist in its:(id,points,requirements,specificTypeAttributes)
     *
     * @param leaderJsonObject JsonObject containing the card data
     * @return a new LeaderCard
     */
    private LeaderCard generateLeaderCardFromJsonObject(JsonObject leaderJsonObject) {
        //Save card resources and development requirements
        List<Requirement> requirements = new ArrayList<>();
        addDevRequirements(requirements, leaderJsonObject);
        addResRequirements(requirements, leaderJsonObject);
        //extract common data to all leaderCards
        String id = leaderJsonObject.get(idNameInJson).getAsString();
        int points = leaderJsonObject.get(pointsNameInJson).getAsInt();
        String type = Parser.getTypeFieldAsString(leaderJsonObject);
        LeaderCard card;
        //Create specific type of leaderCard asking the file for the specific parameters
        switch (type) {
            case productionTypeNameInJson:
                Resource inResource = new StorableResource(ResourcesEnum.valueOf(leaderJsonObject.get("inResource").getAsString()));
                card = new ProductionLeaderCard(id, points, requirements, inResource);
                break;
            case marketTypeNameInJson:
                Resource transformation = new StorableResource(ResourcesEnum.valueOf(leaderJsonObject.get("transformation").getAsString()));
                card = new TransformationLeaderCard(id, points, requirements, transformation);
                break;
            case warehouseTypeNameInJson:
                Resource extraSlotsType = new StorableResource(ResourcesEnum.valueOf(leaderJsonObject.get("extraSlotsType").getAsString()));
                card = new WarehouseLeaderCard(id, points, requirements, extraSlotsType);
                break;
            case discountTypeNameInJson:
                Resource discountResource = new StorableResource(ResourcesEnum.valueOf(leaderJsonObject.get("discount").getAsString()));
                card = new DiscountLeaderCard(id, points, requirements, discountResource);
                break;
            default:
                throw new IllegalStateException("Unexpected leaderCardType: " + type);
        }
        return card;
    }

    /**
     * This method is used to create ad add the development type requirements of the leader card
     *
     * @param requirements     List of requirements to populate
     * @param leaderJsonObject JsonObject containing the leaderCard data
     */
    private void addDevRequirements(List<Requirement> requirements, JsonObject leaderJsonObject) {
        if (leaderJsonObject.has(developmentRequirementNameInJson)) {
            JsonArray jsonArrayOfRequirements = leaderJsonObject.get(developmentRequirementNameInJson).getAsJsonArray();
            for (JsonElement requirementsElement : jsonArrayOfRequirements) {
                JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();
                CardColorsEnum color = CardColorsEnum.valueOf(requirementJsonObject.get(resAndDevColorNameInJson).getAsString());
                int level = requirementJsonObject.get(levelNameInJson).getAsInt();
                int quantity = requirementJsonObject.get(quantityNameInJson).getAsInt();
                DevelopmentRequirement developmentRequirement = new DevelopmentRequirement(level, color, quantity);
                requirements.add(developmentRequirement);
            }
        }
    }

    /**
     * This method is used to create ad add the resources type requirements of the leader card
     *
     * @param requirements     List of requirements to populate
     * @param leaderJsonObject JsonObject containing the leaderCard data
     */
    private void addResRequirements(List<Requirement> requirements, JsonObject leaderJsonObject) {
        if (leaderJsonObject.has(resourcesRequirementNameInJson)) {
            JsonArray jsonArrayOfRequirements = leaderJsonObject.get(resourcesRequirementNameInJson).getAsJsonArray();
            for (JsonElement requirementsElement : jsonArrayOfRequirements) {
                JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();
                ResourcesEnum color = ResourcesEnum.valueOf(requirementJsonObject.get(resAndDevColorNameInJson).getAsString());
                int quantity = requirementJsonObject.get(quantityNameInJson).getAsInt();
                ResourceRequirement resourceRequirement = new ResourceRequirement(color, quantity);
                requirements.add(resourceRequirement);
            }
        }
    }

    /**
     * Create map using CardColorsEnum as key of devCards of the specified level.
     *
     * @param devCards List of cards to map
     * @param level    Level of the cards i want to include in the map
     * @return Map<CardsColorEnum, List>
     */
    public Map<CardColorsEnum, List<DevelopmentCard>> getDevCardsAsGrid(List<DevelopmentCard> devCards, int level) {
        return devCards.stream().filter((el) -> el.getLevel() == level).collect(Collectors.groupingBy(DevelopmentCard::getColor));
    }

    /**
     * This method generate a Development Card from his ID.
     * It takes the information from the Json File.
     *
     * @param cardId the ID of the Card
     * @return the Development Card generated
     */
    public DevelopmentCard generateDevelopmentCardFromId(String cardId) {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.UNMODIFIABLE_DEVELOPMENT_CARDS_PATH);
        JsonArray jsonArrayOfCards = Parser.extractFromField(Objects.requireNonNull(fileElement), mainDevJsonArrayName).getAsJsonArray();

        int cardIDNumber = getNumberOfCard(cardId);
        JsonObject cardJsonObject = jsonArrayOfCards.get(cardIDNumber).getAsJsonObject();
        return generateSingleDevCardFromJsonObject(cardJsonObject, cardIDNumber + 1);
    }


    /**
     * Get method that return the number of the card into the Json file.
     * the cardID must be of the type: color + "_" + level + "_" + numberOfCard
     *
     * @param cardID is the ID of the Card
     * @return the number of the Card in the Json file
     */
    private int getNumberOfCard(String cardID) {
        String[] splitCardIndex = cardID.split("_");
        return Integer.parseInt(splitCardIndex[2]) - 1;
    }

    /**
     * This method generate a Leader Card from his ID.
     * It takes the information from the Json File.
     *
     * @param cardId the ID of the Card
     * @return the Leader Card
     */
    public LeaderCard generateLeaderCardFromId(String cardId) {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.UNMODIFIABLE_LEADER_CARDS_PATH);
        JsonArray jsonArrayOfCards = Parser.extractFromField(Objects.requireNonNull(fileElement), mainLeaderJsonArrayName).getAsJsonArray();

        for (JsonElement el : jsonArrayOfCards) {
            JsonObject cardJsonObject = el.getAsJsonObject();
            if (cardJsonObject.get("id").getAsString().equals(cardId)) {
                return generateLeaderCardFromJsonObject(cardJsonObject);
            }
        }
        return null;
    }
}
