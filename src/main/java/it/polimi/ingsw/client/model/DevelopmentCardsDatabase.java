package it.polimi.ingsw.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.commons.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that contains all the information about all the Development Cards
 */
public class DevelopmentCardsDatabase {
    private static DevelopmentCardsDatabase instance = null;

    private final List<String> devCardsLevel = new ArrayList<>();
    private final List<String> devCardsColor = new ArrayList<>();
    private final List<String> devCardsPrice = new ArrayList<>();
    private final List<String> devCardsInResources = new ArrayList<>();
    private final List<String> devCardsOutResources = new ArrayList<>();
    private final List<String> devCardsVictoryPoints = new ArrayList<>();

    private static final String BASIC_CARD_ID = "basicPowerCard";

    /**
     * Create an instance of DevelopmentCardsDatabase or return the existing one
     *
     * @return the only existing instance of DevelopmentCardsDatabase
     */
    public static DevelopmentCardsDatabase getDevelopmentCardsDatabase() {
        if (instance == null) {
            instance = new DevelopmentCardsDatabase();
        }
        return instance;
    }

    /**
     * Constructor of the database instance
     */
    private DevelopmentCardsDatabase() {
        firstSetup();
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
     * Method used to get the level of a card from its ID
     *
     * @param cardID ID of the card
     * @return the level of the card
     */
    public int getLevelOf(String cardID) {
        return Integer.parseInt(devCardsLevel.get(getNumberOfCard(cardID)));
    }

    /**
     * Method used to get the color of a card from its ID
     *
     * @param cardID ID of the card
     * @return the color of the card
     */
    public String getColorOf(String cardID) {
        return devCardsColor.get(getNumberOfCard(cardID));
    }

    /**
     * Get method that return price of the Card
     *
     * @param cardID is the ID of the Card
     * @return the price of the Card
     */
    public String[] getPriceOf(String cardID) {
        String[] priceResourcesToSplit = devCardsPrice.get(getNumberOfCard(cardID)).split("&");
        String[] cardPrice = splitResources(priceResourcesToSplit);
        setThePositionOfThePrice(cardPrice);
        return cardPrice;
    }

    /**
     * Get method that return the in resources of the Card
     *
     * @param cardID is the ID of the Card
     * @return the resources in input of the Card
     */
    public String[] getInResources(String cardID) {
        String[] inResourcesToSplit = devCardsInResources.get(getNumberOfCard(cardID)).split("&");
        String[] inResources = splitResources(inResourcesToSplit);
        setThePositionOfTheResources(inResources);
        return inResources;
    }

    /**
     * Get method that return the out resources of the Card
     *
     * @param cardID is the ID of the Card
     * @return the resources in output of the Card
     */
    public String[] getOutResources(String cardID) {
        String[] outResourcesToSplit = devCardsOutResources.get(getNumberOfCard(cardID)).split("&");
        String[] outResources = splitResources(outResourcesToSplit);
        setThePositionOfTheResources(outResources);
        return outResources;
    }

    /**
     * This method split correctly the resources
     *
     * @param resourcesToSplit array of the resources to split
     * @return the splitted resources of the correct color
     */
    private String[] splitResources(String[] resourcesToSplit) {
        List<String[]> cardResourcesInList = new ArrayList<>();
        String[] cardResources = new String[3];

        int t = 0;
        cardResources[0] = " ";
        cardResources[1] = " ";
        cardResources[2] = " ";

        for (String Resource : resourcesToSplit) {
            cardResourcesInList.add(Resource.split("_"));
        }

        for (String[] splitOutResources : cardResourcesInList) {
            if (t == 0)
                cardResources[0] = AnsiUtilities.colorString(splitOutResources[0], splitOutResources[1]);
            else if (t == 1)
                cardResources[1] = AnsiUtilities.colorString(splitOutResources[0], splitOutResources[1]);
            else if (t == 2)
                cardResources[2] = AnsiUtilities.colorString(splitOutResources[0], splitOutResources[1]);
            t++;
        }

        return cardResources;
    }

    /**
     * This method set the position of the out and in resources in the printable Development Card
     *
     * @param cardResources in or out resources of the Card
     */
    private void setThePositionOfTheResources(String[] cardResources) {
        for (int i = 0; i < 3; i++) {
            if (cardResources[1].equals(" ")) {
                cardResources[1] = cardResources[0];
                cardResources[0] = " ";
                break;
            } else if (cardResources[2].equals(" ")) {
                cardResources[2] = cardResources[1];
                cardResources[1] = " ";
                break;
            }
        }
    }

    /**
     * This method set the position of the price in the printable Development Card
     *
     * @param cardPrice price of the Card
     */
    private void setThePositionOfThePrice(String[] cardPrice) {
        for (int i = 0; i < 3; i++) {
            if (cardPrice[1].equals(" ")) {
                cardPrice[2] = cardPrice[0] + "  ";
                cardPrice[0] = "";
                cardPrice[1] = "";
                break;
            } else if (cardPrice[2].equals(" ")) {
                cardPrice[2] = cardPrice[1];
                cardPrice[1] = " ";
                break;
            }
        }
    }

    /**
     * Get method that return the Victory Points of the Card
     *
     * @param cardID is the ID of the Card
     * @return the Victory Points of the Card
     */
    public String getVictoryPoints(String cardID) {
        int numberOfCard = getNumberOfCard(cardID);
        if (String.valueOf(devCardsVictoryPoints.get(numberOfCard)).length() == 1)
            return " " + AnsiUtilities.BLACK + AnsiUtilities.YELLOW_BACKGROUND + devCardsVictoryPoints.get(numberOfCard) + AnsiUtilities.RESET;
        else
            return AnsiUtilities.BLACK + AnsiUtilities.YELLOW_BACKGROUND + devCardsVictoryPoints.get(numberOfCard) + AnsiUtilities.RESET;
    }

    /**
     * This method is called only at the moment of the creation of the class.
     * It reads the information containing into the Json file and add that into the local variables
     */
    private void firstSetup() {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.UNMODIFIABLE_DEVELOPMENT_CARDS_PATH);
        JsonArray jsonArrayOfCards = Parser.extractFromField(Objects.requireNonNull(fileElement), "cards").getAsJsonArray();

        //Cycle through all cards element in the file
        for (JsonElement cardElement : jsonArrayOfCards) {
            //Get Json object
            JsonObject cardJsonObject = cardElement.getAsJsonObject();
            takeInformationForADevelopmentCard(cardJsonObject);
        }
    }

    /**
     * This method add the information taken from the file json
     * into the local variables that contain all the information of the Cards.
     *
     * @param cardJsonObject JsonObject containing the Card data
     */
    private void takeInformationForADevelopmentCard(JsonObject cardJsonObject) {
        devCardsLevel.add(Parser.extractFromField(cardJsonObject, "level").getAsString());
        devCardsColor.add(Parser.extractFromField(cardJsonObject, "color").getAsString());
        devCardsVictoryPoints.add(Parser.extractFromField(cardJsonObject, "points").getAsString());
        devCardsPrice.add(extractResourcesFromJson(cardJsonObject, "price"));
        devCardsInResources.add(extractResourcesFromJson(cardJsonObject, "inResources"));
        devCardsOutResources.add(extractResourcesFromJson(cardJsonObject, "outResources"));
    }

    /**
     * This method is used to extract the resources from a JsonArray contained in a jsonObject
     *
     * @param cardJsonObject           JsonObject containing the jsonArray with the resources data
     * @param resourcesArrayNameInJson name of the JsonArray in the JsonObject
     * @return a Sting containing all the resources
     */
    private String extractResourcesFromJson(JsonObject cardJsonObject, String resourcesArrayNameInJson) {
        StringBuilder resourcesToPopulate = new StringBuilder();
        JsonArray jsonArrayOfResources = cardJsonObject.get(resourcesArrayNameInJson).getAsJsonArray();

        for (JsonElement outResourcesElement : jsonArrayOfResources) {
            JsonObject outResourcesJsonObject = outResourcesElement.getAsJsonObject();
            resourcesToPopulate.append(outResourcesJsonObject.get("quantity").getAsString())
                    .append("_")
                    .append(outResourcesJsonObject.get("color").getAsString())
                    .append("&");
        }

        return resourcesToPopulate.toString();
    }

    /**
     * Method that creates the specific card according with the iD in input
     *
     * @param iD of the Card
     * @return the Card to print
     */
    public DevelopmentCard createDevelopmentCardByID(String iD) {
        if (iD.equals(DevelopmentCard.EMPTY_CARD_ID))
            return new ProductionSlot(DevelopmentCard.EMPTY_CARD_ID);
        else if (iD.equals(BASIC_CARD_ID))
            return new BasicPowerCard(BASIC_CARD_ID);
        else
            return new DevelopmentCard(iD);
    }
}
