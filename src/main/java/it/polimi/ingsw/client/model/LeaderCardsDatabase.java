package it.polimi.ingsw.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.client.view.cli.AnsiEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains all the information about all the Leader Cards
 */
public class LeaderCardsDatabase {
    private static LeaderCardsDatabase instance = null;

    private final String leaderCardsFileName = "src/main/resources/leaderCards.json";
    private final List<String> leaderCardsID = new ArrayList<>();
    private final List<String> leaderCardsVictoryPoints = new ArrayList<>();
    private final List<String> leaderCardsRequirements = new ArrayList<>();

    public String getLeaderCardAbility(String id) {
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
        return leaderCardsID.indexOf(cardIndex);
    }

    /**
     * Get method that return the requirements of the card
     *
     * @param cardIndex is the ID of the Card
     * @return the requirements of the card
     */
    public String[] getRequirements(String cardIndex) {
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
                cardRequirements[0] = "Card: " + AnsiEnum.colorString(splittedRequirements[0], splittedRequirements[1]) + "   ";
                cardRequirements[1] = "lvl: " + splittedRequirements[2] + AnsiEnum.RESET + "    ";
            } else if (numberOfCard >= 8 && numberOfCard <= 11) { // Warehouse Leader Card
                cardRequirements[0] = "Res: " + AnsiEnum.colorString(splittedRequirements[0], splittedRequirements[1]) + "    ";
            } else {
                if (t == 0)
                    cardRequirements[0] = "Cards: " + AnsiEnum.colorString(splittedRequirements[0], splittedRequirements[1]) + "  ";
                else if (t == 1)
                    cardRequirements[1] = "       " + AnsiEnum.colorString(splittedRequirements[0], splittedRequirements[1]) + AnsiEnum.RESET + "  ";
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
    public String getVictoryPoints(String cardIndex) {
        int numberOfCard = getNumberOfCard(cardIndex);

        if (String.valueOf(leaderCardsVictoryPoints.get(numberOfCard)).length() == 1)
            return " " + AnsiEnum.BLACK + AnsiEnum.YELLOW_BACKGROUND + leaderCardsVictoryPoints.get(numberOfCard) + AnsiEnum.RESET;
        else
            return AnsiEnum.BLACK + AnsiEnum.YELLOW_BACKGROUND + leaderCardsVictoryPoints.get(numberOfCard) + AnsiEnum.RESET;
    }

    /**
     * Get method that return the Ability of the Card
     *
     * @param cardIndex is the ID of the Card
     * @return the Ability of the Card
     */
    public String getAbility(String cardIndex) {
        int numberOfCard = getNumberOfCard(cardIndex);

        switch (cardIndex.split("")[0]) {
            case "p":
                return AnsiEnum.colorString("1", leaderCardsAbilities.get(numberOfCard)) + " } " + "? + " + AnsiEnum.colorString("1", "RED");
            case "m":
                return "  1 = " + AnsiEnum.colorString("1", leaderCardsAbilities.get(numberOfCard)) + "  ";
            case "w":
                return AnsiEnum.colorString("  |_|" + "|_| ", leaderCardsAbilities.get(numberOfCard));
            case "d":
                return AnsiEnum.colorString("   -1    ", leaderCardsAbilities.get(numberOfCard));
        }
        return null;
    }

    /**
     * This method is called only at the moment of the creation of the class.
     * It reads the information containing into the Json file and add that into the local variables
     */
    private void firstSetup() {
        File input = new File(leaderCardsFileName);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfLeaders = fileObject.get("leaders").getAsJsonArray();

            //Cycle through all leaders element in the file
            for (JsonElement leaderElement : jsonArrayOfLeaders) {
                //Get Json object
                JsonObject leaderJsonObject = leaderElement.getAsJsonObject();
                takeInformationForALeaderCard(leaderJsonObject);
            }
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("errore nel file json");
            e.printStackTrace();
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
        leaderCardsID.add(extractStringValueFromJson(leaderJsonObject, "id"));
        leaderCardsVictoryPoints.add(extractStringValueFromJson(leaderJsonObject, "points"));
        String type = extractStringValueFromJson(leaderJsonObject, "type");

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

    /**
     * This method is used to extract a String value from the jsonObject
     *
     * @param cardJsonObject  JsonObject containing the Card data
     * @param nameValueInJson name of the value in the JsonObject
     * @return the String value extracted
     */
    private String extractStringValueFromJson(JsonObject cardJsonObject, String nameValueInJson) {
        return cardJsonObject.get(nameValueInJson).getAsString();
    }
}
