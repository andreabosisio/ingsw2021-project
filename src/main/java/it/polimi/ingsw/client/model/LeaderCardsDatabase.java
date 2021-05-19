package it.polimi.ingsw.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.client.view.cli.AsciiArts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class that contains all the information about all the Leader Cards
 */
public class LeaderCardsDatabase {
    private static LeaderCardsDatabase instance = null;

    private final String leaderCardsFileName = "src/main/resources/leaderCards.json";
    private final List<String> leaderCardsID = new ArrayList<>();
    private final List<String> leaderCardsVictoryPoints = new ArrayList<>();
    private final List<String> leaderCardsRequirements = new ArrayList<>();

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
        leaderCardsFirstSettings();
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
        System.out.println(numberOfCard);
        // cardRequirements[0]: Number of resources, cardRequirements[1]: Color of the resource
        // Production, Market, Warehouse, Discount
        for (String[] splittedRequirements : cardRequirementsList) {
            if (numberOfCard <= 3) { // Production Leader Card
                cardRequirements[0] = "Card: " + ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedRequirements[1]) + splittedRequirements[0] + AsciiArts.RESET + "   ";
                cardRequirements[1] = "lvl: " + splittedRequirements[2] + AsciiArts.RESET + "    ";
            } else if (numberOfCard >= 8 && numberOfCard <= 11) { // Warehouse Leader Card
                cardRequirements[0] = "Res: " + ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedRequirements[1]) + splittedRequirements[0] + AsciiArts.RESET + "    ";
            } else {
                if (t == 0)
                    cardRequirements[0] = "Cards: " + ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedRequirements[1]) + splittedRequirements[0] + AsciiArts.RESET + "  ";
                else if (t == 1)
                    cardRequirements[1] = "       " + ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedRequirements[1]) + splittedRequirements[0] + AsciiArts.RESET + "  ";
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
            return " " + AsciiArts.BLACK + AsciiArts.YELLOW_BACKGROUND + leaderCardsVictoryPoints.get(numberOfCard) + AsciiArts.RESET;
        else
            return AsciiArts.BLACK + AsciiArts.YELLOW_BACKGROUND + leaderCardsVictoryPoints.get(numberOfCard) + AsciiArts.RESET;
    }

    /**
     * This method add the information taken from the file json
     * into the various list that contains all the information of the Cards.
     * It is called only at the moment of the creation of the class.
     */
    private void leaderCardsFirstSettings() {
        File input = new File(leaderCardsFileName);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfLeaders = fileObject.get("leaders").getAsJsonArray();

            //Cycle through all leaders element in the file
            for (JsonElement leaderElement : jsonArrayOfLeaders) {

                //Get Json object
                JsonObject leaderJsonObject = leaderElement.getAsJsonObject();

                StringBuilder devRequirements = new StringBuilder();
                if (leaderJsonObject.has("devRequirements")) {

                    JsonArray jsonArrayOfRequirements = leaderJsonObject.get("devRequirements").getAsJsonArray();

                    for (JsonElement requirementsElement : jsonArrayOfRequirements) {
                        JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();

                        devRequirements.append(requirementJsonObject.get("quantity").getAsString())
                                .append("_")
                                .append(requirementJsonObject.get("color").getAsString())
                                .append("_")
                                .append(requirementJsonObject.get("level").getAsInt())
                                .append("&");
                    }
                }

                StringBuilder resRequirements = new StringBuilder();
                if (leaderJsonObject.has("resRequirements")) {

                    JsonArray jsonArrayOfRequirements = leaderJsonObject.get("resRequirements").getAsJsonArray();

                    for (JsonElement requirementsElement : jsonArrayOfRequirements) {
                        JsonObject requirementJsonObject = requirementsElement.getAsJsonObject();

                        resRequirements.append(requirementJsonObject.get("quantity").getAsInt())
                                .append("_")
                                .append(requirementJsonObject.get("color").getAsString())
                                .append("&");
                    }
                }

                //extract common data to all leaderCards
                leaderCardsID.add(leaderJsonObject.get("id").getAsString());
                leaderCardsVictoryPoints.add(leaderJsonObject.get("points").getAsString());
                String type = leaderJsonObject.get("type").getAsString();

                if (type.equals("warehouse"))
                    leaderCardsRequirements.add(resRequirements.toString());
                else
                    leaderCardsRequirements.add(devRequirements.toString());
            }
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("errore nel file json");
            e.printStackTrace();
        }
    }
}
