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
 * Class that contains all the information about all the Development Cards
 */
public class DevelopmentCardsDatabase {
    private static DevelopmentCardsDatabase instance = null;

    private final String developmentCardsFileName = "src/main/resources/developmentCards.json";
    private final List<String> devCardsLevel = new ArrayList<>();
    private final List<String> devCardsColor = new ArrayList<>();
    private final List<String> devCardsPrice = new ArrayList<>();
    private final List<String> devCardsInResources = new ArrayList<>();
    private final List<String> devCardsOutResources = new ArrayList<>();
    private final List<String> devCardsVictoryPoints = new ArrayList<>();

    private static final String EMPTY_CARD_ID = "empty";
    private static final String BASIC_CARD_ID = "basicPowerCard";

    public static String getEmptyCardId() {
        return EMPTY_CARD_ID;
    }

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
                cardResources[0] = AnsiEnum.colorString(splitOutResources[0], splitOutResources[1]);
            else if (t == 1)
                cardResources[1] = AnsiEnum.colorString(splitOutResources[0], splitOutResources[1]);
            else if (t == 2)
                cardResources[2] = AnsiEnum.colorString(splitOutResources[0], splitOutResources[1]);
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
            return " " + AnsiEnum.BLACK + AnsiEnum.YELLOW_BACKGROUND + devCardsVictoryPoints.get(numberOfCard) + AnsiEnum.RESET;
        else
            return AnsiEnum.BLACK + AnsiEnum.YELLOW_BACKGROUND + devCardsVictoryPoints.get(numberOfCard) + AnsiEnum.RESET;
    }

    /**
     * This method is called only at the moment of the creation of the class.
     * It reads the information containing into the Json file and add that into the local variables
     */
    private void firstSetup() {
        File input = new File(developmentCardsFileName);

        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfCards = fileObject.get("cards").getAsJsonArray();

            //Cycle through all cards element in the file
            for (JsonElement cardElement : jsonArrayOfCards) {
                //Get Json object
                JsonObject cardJsonObject = cardElement.getAsJsonObject();
                takeInformationForADevelopmentCard(cardJsonObject);
            }
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("error in the json file format");
            e.printStackTrace();
        }
    }

    /**
     * This method add the information taken from the file json
     * into the local variables that contain all the information of the Cards.
     *
     * @param cardJsonObject JsonObject containing the Card data
     */
    private void takeInformationForADevelopmentCard(JsonObject cardJsonObject) {
        devCardsLevel.add(extractStringValueFromJson(cardJsonObject, "level"));
        devCardsColor.add(extractStringValueFromJson(cardJsonObject, "color"));
        devCardsVictoryPoints.add(extractStringValueFromJson(cardJsonObject, "points"));
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
     * This method is used to extract a String value from the jsonObject
     *
     * @param cardJsonObject  JsonObject containing the Card data
     * @param nameValueInJson name of the value in the JsonObject
     * @return the String value extracted
     */
    private String extractStringValueFromJson(JsonObject cardJsonObject, String nameValueInJson) {
        return cardJsonObject.get(nameValueInJson).getAsString();
    }

    /**
     * Method that creates the specific card according with the iD in input
     *
     * @param iD of the Card
     * @return the Card to print
     */
    public DevelopmentCard createDevelopmentCardByID(String iD) {
        if (iD.equals(EMPTY_CARD_ID))
            return new ProductionSlot(EMPTY_CARD_ID);
        else if (iD.equals(BASIC_CARD_ID))
            return new BasicPowerCard(BASIC_CARD_ID);
        else
            return new DevelopmentCard(iD);
    }
}
