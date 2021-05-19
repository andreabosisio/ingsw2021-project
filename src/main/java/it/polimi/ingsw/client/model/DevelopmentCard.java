package it.polimi.ingsw.client.model;

import com.google.gson.*;
import it.polimi.ingsw.client.view.cli.AsciiArts;

import java.io.*;
import java.util.*;

/**
 * Class that contains all the information to print a Card
 */
public class DevelopmentCard {

    private final String developmentCardsFileName = "src/main/resources/developmentCards.json";

    private final List<String> cardsLevel = new ArrayList<>();
    private final List<String> cardsColor = new ArrayList<>();
    private final List<String> cardsPrice = new ArrayList<>();
    private final List<String> cardsInResources = new ArrayList<>();
    private final List<String> cardsOutResources = new ArrayList<>();
    private final List<String> cardsVictoryPoints = new ArrayList<>();

    private final String[] cardPrice = new String[3]; // contains the three possible resources to buy the card
    private String cardLevel; // contains the level of the Card colored like the color of the Card
    private String cardVictoryPoints; // contains the victory points of the Card (vP < 9 ? " " + 3 : 11)
    private final String[] cardInResources = new String[3];
    private final String[] cardOutResources = new String[3];

    public DevelopmentCard() {
        developmentCardsFromJson();
    }

    /**
     * This method return the print of a specific card
     *
     * @param cardIndex is the index of the Card to print
     * @return a List composed by the lines of the Card
     */
    public List<String> getPrintableDevelopmentCard(String cardIndex) {

        int numberOfCard = getNumberOfCard(cardIndex) - 1;
        List<String> developmentCardToPrint = new ArrayList<>();

        setVariablesOfCard(numberOfCard);

        developmentCardToPrint.add("╔══════════╗");
        developmentCardToPrint.add("║" + cardLevel + "        " + cardLevel + "║");
        developmentCardToPrint.add("║  " + cardPrice[0] + " " + cardPrice[1] + " " + cardPrice[2] + "   ║");
        developmentCardToPrint.add("║──────────║");
        developmentCardToPrint.add("║  " + cardInResources[0] + " │ " + cardOutResources[0] + "   ║");
        developmentCardToPrint.add("║  " + cardInResources[1] + " } " + cardOutResources[1] + "   ║");
        developmentCardToPrint.add("║  " + cardInResources[2] + " │ " + cardOutResources[2] + "   ║");
        developmentCardToPrint.add("║        " + cardVictoryPoints + "║");
        developmentCardToPrint.add("╚══════════╝");

        return developmentCardToPrint;
    }

    /**
     * This method return the print of an empty card
     *
     * @return a List composed by the lines of the Card
     */
    public List<String> getPrintableEmptyDevelopmentCard() {
        List<String> developmentCardToPrint = new ArrayList<>();

        developmentCardToPrint.add("╔══════════╗");
        developmentCardToPrint.add("║          ║");
        developmentCardToPrint.add("║          ║");
        developmentCardToPrint.add("║──────────║");
        developmentCardToPrint.add("║    │     ║");
        developmentCardToPrint.add("║    }     ║");
        developmentCardToPrint.add("║    │     ║");
        developmentCardToPrint.add("║          ║");
        developmentCardToPrint.add("╚══════════╝");

        return developmentCardToPrint;
    }

    /**
     * Get method that return the number of the card into the json file,
     * the cardIndex must be of the type: color + "_" + level + "_" + numberOfCard
     *
     * @param cardIndex is the index of the Card
     */
    private int getNumberOfCard(String cardIndex) {
        String[] splittedCardIndex = cardIndex.split("_");
        return Integer.parseInt(splittedCardIndex[2]);
    }

    /**
     * This method call various method that set correctly the variables of the Card
     *
     * @param numberOfCard is the number of the Card
     */
    private void setVariablesOfCard(int numberOfCard) {
        setLevelAndColorOfCard(numberOfCard);
        setPriceOfCard(numberOfCard);
        setVictoryPointsOfCard(numberOfCard);
        setInResourcesOfCard(numberOfCard);
        setOutResourcesOfCard(numberOfCard);
    }

    /**
     * This method set the variable levelColored with the level of the Card.
     * The number is colored like the color of the card
     *
     * @param numberOfCard is the number of the Card
     */
    private void setLevelAndColorOfCard(int numberOfCard) {
        cardLevel = ColorsForDevelopmentCard.getAsciiDevCardByColor(cardsColor.get(numberOfCard)) +
                cardsLevel.get(numberOfCard) + AsciiArts.RESET;
    }

    /**
     * This method set the array price that contains all the information about the price of the card
     *
     * @param numberOfCard is the number of the Card
     */
    private void setPriceOfCard(int numberOfCard) {
        String[] tmpPrice = cardsPrice.get(numberOfCard).split("&");
        List<String[]> cardPrice = new ArrayList<>();
        int t = 0;
        this.cardPrice[0] = " ";
        this.cardPrice[1] = " "; // if there is not a resource, there is a space
        this.cardPrice[2] = " ";

        for (String price : tmpPrice) {
            cardPrice.add(price.split("_"));
        }

        // splittedPrice[0]: Number of resources, splittedPrice[1]: Color of the resource
        for (String[] splittedPrice : cardPrice) {
            if (t == 0)
                this.cardPrice[0] = ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedPrice[1]) + splittedPrice[0] + AsciiArts.RESET;
            else if (t == 1)
                this.cardPrice[1] = ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedPrice[1]) + splittedPrice[0] + AsciiArts.RESET;
            else if (t == 2)
                this.cardPrice[2] = ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedPrice[1]) + splittedPrice[0] + AsciiArts.RESET;
            t++;
        }

        for (int i = 0; i < 3; i++) {
            if (this.cardPrice[1].equals(" ")) {
                this.cardPrice[2] = this.cardPrice[0] + "  ";
                this.cardPrice[0] = "";
                this.cardPrice[1] = "";
                break;
            } else if (this.cardPrice[2].equals(" ")) {
                this.cardPrice[2] = this.cardPrice[1];
                this.cardPrice[1] = " ";
                break;
            }
        }
    }

    /**
     * This method set the variable cardVictoryPoints with the Victory Points of the Card
     *
     * @param numberOfCard is the number of the Card
     */
    private void setInResourcesOfCard(int numberOfCard) {
        String[] tmpInResources = cardsInResources.get(numberOfCard).split("&");
        List<String[]> cardInResources = new ArrayList<>();
        int t = 0;
        this.cardInResources[0] = " ";
        this.cardInResources[1] = " "; // if there is not a resource, there is a space
        this.cardInResources[2] = " ";

        for (String inResource : tmpInResources) {
            cardInResources.add(inResource.split("_"));
        }

        // splittedInResources[0]: Number of resources, splittedInResources[1]: Color of the resource
        for (String[] splittedInResources : cardInResources) {
            if (t == 0)
                this.cardInResources[0] = ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedInResources[1]) + splittedInResources[0] + AsciiArts.RESET;
            else if (t == 1)
                this.cardInResources[1] = ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedInResources[1]) + splittedInResources[0] + AsciiArts.RESET;
            else if (t == 2)
                this.cardInResources[2] = ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedInResources[1]) + splittedInResources[0] + AsciiArts.RESET;
            t++;
        }

        for (int i = 0; i < 3; i++) {
            if (this.cardInResources[1].equals(" ")) {
                this.cardInResources[1] = this.cardInResources[0];
                this.cardInResources[0] = " ";
                break;
            } else if (this.cardInResources[2].equals(" ")) {
                this.cardInResources[2] = this.cardInResources[1];
                this.cardInResources[1] = " ";
                break;
            }
        }
    }

    /**
     * This method set the variable cardVictoryPoints with the Victory Points of the Card
     *
     * @param numberOfCard is the number of the Card
     */
    private void setOutResourcesOfCard(int numberOfCard) {
        String[] tmpOutResources = cardsOutResources.get(numberOfCard).split("&");
        List<String[]> cardOutResources = new ArrayList<>();
        int t = 0;
        this.cardOutResources[0] = " ";
        this.cardOutResources[1] = " "; // if there is not a resource, there is a space
        this.cardOutResources[2] = " ";

        for (String outResource : tmpOutResources) {
            cardOutResources.add(outResource.split("_"));
        }

        // splittedOutResources[0]: Number of resources, splittedOutResources[1]: Color of the resource
        for (String[] splittedOutResources : cardOutResources) {
            if (t == 0)
                this.cardOutResources[0] = ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedOutResources[1]) + splittedOutResources[0] + AsciiArts.RESET;
            else if (t == 1)
                this.cardOutResources[1] = ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedOutResources[1]) + splittedOutResources[0] + AsciiArts.RESET;
            else if (t == 2)
                this.cardOutResources[2] = ColorsForDevelopmentCard.getAsciiDevCardByColor(splittedOutResources[1]) + splittedOutResources[0] + AsciiArts.RESET;
            t++;
        }

        for (int i = 0; i < 3; i++) {
            if (this.cardOutResources[1].equals(" ")) {
                this.cardOutResources[1] = this.cardOutResources[0];
                this.cardOutResources[0] = " ";
                break;
            } else if (this.cardOutResources[2].equals(" ")) {
                this.cardOutResources[2] = this.cardOutResources[1];
                this.cardOutResources[1] = " ";
                break;
            }
        }
    }

    /**
     * This method set the variable cardVictoryPoints with the Victory Points of the Card
     *
     * @param numberOfCard is the number of the Card
     */
    private void setVictoryPointsOfCard(int numberOfCard) {
        if (String.valueOf(cardsVictoryPoints.get(numberOfCard)).length() == 1)
            cardVictoryPoints = " " + AsciiArts.BLACK + AsciiArts.YELLOW_BACKGROUND + cardsVictoryPoints.get(numberOfCard) + AsciiArts.RESET;
        else
            cardVictoryPoints = AsciiArts.BLACK + AsciiArts.YELLOW_BACKGROUND + cardsVictoryPoints.get(numberOfCard) + AsciiArts.RESET;
    }

    /**
     * This method add the information taken from the file json
     * into the various list that contains all the information of the Cards
     */
    private void developmentCardsFromJson() {
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

                cardsLevel.add(String.valueOf(level));
                cardsColor.add(color);
                cardsVictoryPoints.add(String.valueOf(points));
                cardsPrice.add(price.toString());
                cardsInResources.add(inResources.toString());
                cardsOutResources.add(outResources.toString());
            }
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("errore format nel file json");
            e.printStackTrace();
        }
    }
}
