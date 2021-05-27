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

    public static String getBasicCardId() {
        return BASIC_CARD_ID;
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
        String[] splittedCardIndex = cardID.split("_");
        return Integer.parseInt(splittedCardIndex[2]) - 1;
    }

    public int getLevelOf(String cardID) {
        return Integer.parseInt(devCardsLevel.get(getNumberOfCard(cardID)));
    }

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
        int numberOfCard = getNumberOfCard(cardID);
        String[] tmpPrice = devCardsPrice.get(numberOfCard).split("&");
        String[] cardPrice = new String[3];
        List<String[]> cardPriceList = new ArrayList<>();

        int t = 0;
        cardPrice[0] = " ";
        cardPrice[1] = " "; // if there is not a resource, there is a space
        cardPrice[2] = " ";

        for (String price : tmpPrice) {
            cardPriceList.add(price.split("_"));
        }

        // splittedPrice[0]: Number of resources, splittedPrice[1]: Color of the resource
        for (String[] splittedPrice : cardPriceList) {
            if (t == 0)
                cardPrice[0] = AnsiEnum.colorString(splittedPrice[0], splittedPrice[1]);
            else if (t == 1)
                cardPrice[1] = AnsiEnum.colorString(splittedPrice[0], splittedPrice[1]);
            else if (t == 2)
                cardPrice[2] = AnsiEnum.colorString(splittedPrice[0], splittedPrice[1]);
            t++;
        }

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
        return cardPrice;
    }

    /**
     * Get method that return the in resources of the Card
     *
     * @param cardID is the ID of the Card
     * @return the resources in input of the Card
     */
    public String[] getInResources(String cardID) {
        int numberOfCard = getNumberOfCard(cardID);
        String[] tmpInResources = devCardsInResources.get(numberOfCard).split("&");
        List<String[]> cardInResourcesList = new ArrayList<>();
        String[] cardInResources = new String[3];

        int t = 0;
        cardInResources[0] = " ";
        cardInResources[1] = " "; // if there is not a resource, there is a space
        cardInResources[2] = " ";

        for (String inResource : tmpInResources) {
            cardInResourcesList.add(inResource.split("_"));
        }

        // splittedInResources[0]: Number of resources, splittedInResources[1]: Color of the resource
        for (String[] splittedInResources : cardInResourcesList) {
            if (t == 0)
                cardInResources[0] = AnsiEnum.colorString(splittedInResources[0], splittedInResources[1]);
            else if (t == 1)
                cardInResources[1] = AnsiEnum.colorString(splittedInResources[0], splittedInResources[1]);
            else if (t == 2)
                cardInResources[2] = AnsiEnum.colorString(splittedInResources[0], splittedInResources[1]);
            t++;
        }

        for (int i = 0; i < 3; i++) {
            if (cardInResources[1].equals(" ")) {
                cardInResources[1] = cardInResources[0];
                cardInResources[0] = " ";
                break;
            } else if (cardInResources[2].equals(" ")) {
                cardInResources[2] = cardInResources[1];
                cardInResources[1] = " ";
                break;
            }
        }
        return cardInResources;
    }

    /**
     * Get method that return the out resources of the Card
     *
     * @param cardID is the ID of the Card
     * @return the resources in output of the Card
     */
    public String[] getOutResources(String cardID) {
        int numberOfCard = getNumberOfCard(cardID);
        String[] tmpOutResources = devCardsOutResources.get(numberOfCard).split("&");
        List<String[]> cardOutResourcesList = new ArrayList<>();
        String[] cardOutResources = new String[3];

        int t = 0;
        cardOutResources[0] = " ";
        cardOutResources[1] = " "; // if there is not a resource, there is a space
        cardOutResources[2] = " ";

        for (String outResource : tmpOutResources) {
            cardOutResourcesList.add(outResource.split("_"));
        }

        // splittedOutResources[0]: Number of resources, splittedOutResources[1]: Color of the resource
        for (String[] splittedOutResources : cardOutResourcesList) {
            if (t == 0)
                cardOutResources[0] = AnsiEnum.colorString(splittedOutResources[0], splittedOutResources[1]);
            else if (t == 1)
                cardOutResources[1] = AnsiEnum.colorString(splittedOutResources[0], splittedOutResources[1]);
            else if (t == 2)
                cardOutResources[2] = AnsiEnum.colorString(splittedOutResources[0], splittedOutResources[1]);
            t++;
        }

        for (int i = 0; i < 3; i++) {
            if (cardOutResources[1].equals(" ")) {
                cardOutResources[1] = cardOutResources[0];
                cardOutResources[0] = " ";
                break;
            } else if (cardOutResources[2].equals(" ")) {
                cardOutResources[2] = cardOutResources[1];
                cardOutResources[1] = " ";
                break;
            }
        }
        return cardOutResources;
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
     * This method add the information taken from the file json
     * into the various list that contains all the information of the Cards.
     * It is called only at the moment of the creation of the class.
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

                StringBuilder price = new StringBuilder();
                JsonArray jsonArrayOfPrices = cardJsonObject.get("price").getAsJsonArray();

                for (JsonElement priceElement : jsonArrayOfPrices) {
                    JsonObject priceJsonObject = priceElement.getAsJsonObject();
                    price.append(priceJsonObject.get("quantity").getAsString())
                            .append("_")
                            .append(priceJsonObject.get("color").getAsString())
                            .append("&");
                }

                StringBuilder inResources = new StringBuilder();
                JsonArray jsonArrayOfInResources = cardJsonObject.get("inResources").getAsJsonArray();

                for (JsonElement inResourcesElement : jsonArrayOfInResources) {
                    JsonObject inResourcesJsonObject = inResourcesElement.getAsJsonObject();
                    inResources.append(inResourcesJsonObject.get("quantity").getAsString())
                            .append("_")
                            .append(inResourcesJsonObject.get("color").getAsString())
                            .append("&");
                }

                StringBuilder outResources = new StringBuilder();
                JsonArray jsonArrayOfOutResources = cardJsonObject.get("outResources").getAsJsonArray();

                for (JsonElement outResourcesElement : jsonArrayOfOutResources) {
                    JsonObject outResourcesJsonObject = outResourcesElement.getAsJsonObject();
                    outResources.append(outResourcesJsonObject.get("quantity").getAsString())
                            .append("_")
                            .append(outResourcesJsonObject.get("color").getAsString())
                            .append("&");
                }

                String color = cardJsonObject.get("color").getAsString();
                int level = cardJsonObject.get("level").getAsInt();
                int points = cardJsonObject.get("points").getAsInt();

                devCardsLevel.add(String.valueOf(level));
                devCardsColor.add(color);
                devCardsVictoryPoints.add(String.valueOf(points));
                devCardsPrice.add(price.toString());
                devCardsInResources.add(inResources.toString());
                devCardsOutResources.add(outResources.toString());
            }
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("errore format nel file json");
            e.printStackTrace();
        }
    }

    public DevelopmentCard createDevelopmentCardByID(String iD) {
        if(iD.equals(EMPTY_CARD_ID))
            return new ProductionSlot(EMPTY_CARD_ID);
        else if (iD.equals(BASIC_CARD_ID))
            return new BasicPowerCard(BASIC_CARD_ID);
        else
            return new DevelopmentCard(iD);
    }
}
