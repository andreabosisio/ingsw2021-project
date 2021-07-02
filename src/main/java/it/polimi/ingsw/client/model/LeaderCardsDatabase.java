package it.polimi.ingsw.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.commons.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains all the information about all the Leader Cards
 */
public class LeaderCardsDatabase {
    private static LeaderCardsDatabase instance = null;

    private final List<String> leaderCardIDs = new ArrayList<>();
    private final List<String> leaderCardsVictoryPoints = new ArrayList<>();
    private final List<String> leaderCardsRequirements = new ArrayList<>();

    /**
     * Getter for the ability of a leaderCard
     * @param id ID of the leaderCard you want the ability of
     * @return a String containing the ability
     */
    public String getAbility(String id) {
        return leaderCardsAbilities.get(getNumberOfCard(id));
    }

    private final List<String> leaderCardsAbilities = new ArrayList<>();

    /**
     * Create an instance of DevelopmentCardsDatabase or return the existing one
     *
     * @return the only existing instance of DevelopmentCardsDatabase
     */
    public static LeaderCardsDatabase getLeaderCardsDatabase() {
        if (instance == null) {
            instance = new LeaderCardsDatabase();
        }
        return instance;
    }

    private LeaderCardsDatabase() {
        firstSetup();
    }

    /**
     * Get method that return the number of the card into the Json file,
     * the cardIndex must be of the type: type_of_leader_card + specific_number
     *
     * @param cardIndex is the ID of the Card
     * @return the number of the card into the Json file
     */
    private int getNumberOfCard(String cardIndex) {
        return leaderCardIDs.indexOf(cardIndex);
    }

    /**
     * Get method that return the requirements of the card
     *
     * @param cardIndex is the ID of the Card
     * @return the requirements of the card
     */
    public String[] getPrintableRequirements(String cardIndex) {
        int numberOfCard = getNumberOfCard(cardIndex);
        String[] tmpRequirements = leaderCardsRequirements.get(numberOfCard).split("&");
        List<String[]> cardRequirementsList = new ArrayList<>();
        String[] cardRequirements = new String[2];

        int t = 0;
        cardRequirements[0] = "          ";
        cardRequirements[1] = "          ";

        for (String requirement : tmpRequirements) {
            cardRequirementsList.add(requirement.split("_"));
        }

        // cardRequirements[0]: Number of resources, cardRequirements[1]: Color of the resource
        // Production, Market, Warehouse, Discount
        for (String[] splittedRequirements : cardRequirementsList) {
            if (numberOfCard <= 3) { // Production Leader Card
                cardRequirements[0] = "Card: " + AnsiUtilities.colorString(splittedRequirements[0], splittedRequirements[1]) + "   ";
                cardRequirements[1] = "lvl: " + splittedRequirements[2] + AnsiUtilities.RESET + "    ";
            } else if (numberOfCard >= 8 && numberOfCard <= 11) { // Warehouse Leader Card
                cardRequirements[0] = "Res: " + AnsiUtilities.colorString(splittedRequirements[0], splittedRequirements[1]) + "    ";
            } else {
                if (t == 0)
                    cardRequirements[0] = "Cards: " + AnsiUtilities.colorString(splittedRequirements[0], splittedRequirements[1]) + "  ";
                else if (t == 1)
                    cardRequirements[1] = "       " + AnsiUtilities.colorString(splittedRequirements[0], splittedRequirements[1]) + AnsiUtilities.RESET + "  ";
                t++;
            }
        }
        return cardRequirements;
    }

    /**
     * Get method that return the Victory Points of the Card
     *
     * @param cardIndex is the ID of the Card
     * @return the Victory Points of the Card
     */
    public String getPrintableVictoryPoints(String cardIndex) {
        int numberOfCard = getNumberOfCard(cardIndex);

        if (String.valueOf(leaderCardsVictoryPoints.get(numberOfCard)).length() == 1)
            return " " + AnsiUtilities.BLACK + AnsiUtilities.YELLOW_BACKGROUND + leaderCardsVictoryPoints.get(numberOfCard) + AnsiUtilities.RESET;
        else
            return AnsiUtilities.BLACK + AnsiUtilities.YELLOW_BACKGROUND + leaderCardsVictoryPoints.get(numberOfCard) + AnsiUtilities.RESET;
    }

    /**
     * Get method that return the Ability of the Card
     *
     * @param cardIndex is the ID of the Card
     * @return the Ability of the Card
     */
    public String getPrintableAbility(String cardIndex) {
        int numberOfCard = getNumberOfCard(cardIndex);
        switch (cardIndex.charAt(0)) {
            case LeaderCard.PRODUCTION_LEADER_CARD_ID_PREFIX:
                return AnsiUtilities.colorString("1", leaderCardsAbilities.get(numberOfCard)) + " } " + "? + " + AnsiUtilities.colorString("1", "RED");
            case LeaderCard.MARKET_LEADER_CARD_ID_PREFIX:
                return "  1 = " + AnsiUtilities.colorString("1", leaderCardsAbilities.get(numberOfCard)) + "  ";
            case LeaderCard.WAREHOUSE_LEADER_CARD_ID_PREFIX:
                return AnsiUtilities.colorString("  |_|" + "|_| ", leaderCardsAbilities.get(numberOfCard));
            case LeaderCard.DISCOUNT_LEADER_CARD_ID_PREFIX:
                return AnsiUtilities.colorString("   -1    ", leaderCardsAbilities.get(numberOfCard));
        }
        return null;
    }

    /**
     * This method is called only at the moment of the creation of the class.
     * It reads the information containing into the Json file and add that into the local variables
     */
    private void firstSetup() {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.UNMODIFIABLE_LEADER_CARDS_PATH);
        assert fileElement != null;
        JsonObject fileObject = fileElement.getAsJsonObject();
        JsonArray jsonArrayOfLeaders = fileObject.get("leaders").getAsJsonArray();

        //Cycle through all leaders element in the file
        for (JsonElement leaderElement : jsonArrayOfLeaders) {
            //Get Json object
            JsonObject leaderJsonObject = leaderElement.getAsJsonObject();
            takeInformationForALeaderCard(leaderJsonObject);
        }
    }

    /**
     * This method add the information taken from the file json
     * into the local variables that contain all the information of the Cards.
     *
     * @param leaderJsonObject JsonObject containing the Card data
     */
    private void takeInformationForALeaderCard(JsonObject leaderJsonObject) {
        String requirements = null;
        if (leaderJsonObject.has("devRequirements"))
            requirements = extractDevRequirementsFromJson(leaderJsonObject);

        if (leaderJsonObject.has("resRequirements"))
            requirements = extractResRequirementsFromJson(leaderJsonObject);

        //extract common data to all leaderCards
        leaderCardIDs.add(Parser.extractFromField(leaderJsonObject, "id").getAsString());
        leaderCardsVictoryPoints.add(Parser.extractFromField(leaderJsonObject, "points").getAsString());
        String type = Parser.extractFromField(leaderJsonObject, "type").getAsString();

        switch (type) {
            case "production":
                leaderCardsAbilities.add(leaderJsonObject.get("inResource").getAsString());
                leaderCardsRequirements.add(requirements);
                break;
            case "market":
                leaderCardsAbilities.add(leaderJsonObject.get("transformation").getAsString());
                leaderCardsRequirements.add(requirements);
                break;
            case "warehouse":
                leaderCardsAbilities.add(leaderJsonObject.get("extraSlotsType").getAsString());
                leaderCardsRequirements.add(requirements);
                break;
            case "discount":
                leaderCardsAbilities.add(leaderJsonObject.get("discount").getAsString());
                leaderCardsRequirements.add(requirements);
                break;
            default:
                throw new IllegalStateException("Unexpected leaderCardType: " + type);
        }
    }

    /**
     * This method is used to extract Resources Requirements from a JsonArray contained in a jsonObject
     *
     * @param cardJsonObject JsonObject containing the jsonArray with the resources data
     * @return a Sting containing all the Resources Requirements
     */
    private String extractResRequirementsFromJson(JsonObject cardJsonObject) {
        StringBuilder resRequirementsToPopulate = new StringBuilder();
        JsonArray jsonArrayOfRequirements = cardJsonObject.get("resRequirements").getAsJsonArray();

        for (JsonElement requirementsElement : jsonArrayOfRequirements) {
            JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();

            resRequirementsToPopulate.append(requirementJsonObject.get("quantity").getAsInt())
                    .append("_")
                    .append(requirementJsonObject.get("color").getAsString())
                    .append("&");
        }

        return resRequirementsToPopulate.toString();
    }

    /**
     * This method is used to extract Development Card Requirements from a JsonArray contained in a jsonObject
     *
     * @param cardJsonObject JsonObject containing the jsonArray with the Development Card Requirements data
     * @return a Sting containing all the Development Card Requirements
     */
    private String extractDevRequirementsFromJson(JsonObject cardJsonObject) {
        StringBuilder devRequirementsToPopulate = new StringBuilder();
        JsonArray jsonArrayOfRequirements = cardJsonObject.get("devRequirements").getAsJsonArray();

        for (JsonElement requirementsElement : jsonArrayOfRequirements) {
            JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();

            devRequirementsToPopulate.append(requirementJsonObject.get("quantity").getAsString())
                    .append("_")
                    .append(requirementJsonObject.get("color").getAsString())
                    .append("_")
                    .append(requirementJsonObject.get("level").getAsInt())
                    .append("&");
        }

        return devRequirementsToPopulate.toString();
    }

}
