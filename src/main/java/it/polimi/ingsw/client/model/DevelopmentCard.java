package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AsciiArts;

import java.util.*;

/**
 * Class that has the capacity to return a printable version of a specific Development Card
 */
public class DevelopmentCard {

    /**
     * This method return the print of a specific Development Card
     *
     * @param cardIndex is the index of the Card to print
     * @return a List composed by the lines of the Card
     */
    public List<String> getPrintableDevelopmentCard(String cardIndex) {
        List<String> developmentCardToPrint = new ArrayList<>();

        DevelopmentCardsDatabase devCardsDatabase = DevelopmentCardsDatabase.getDevelopmentCardsDatabase();

        // contains the three possible resources to buy the card
        String[] cardPrice = devCardsDatabase.getPrice(cardIndex);
        // contains the level of the Card colored like the color of the Card
        String cardLevel = devCardsDatabase.getColoredLevel(cardIndex);
        // contains the victory points of the Card (vP < 9 ? " " + 3 : 11)
        String cardVictoryPoints = devCardsDatabase.getVictoryPoints(cardIndex);
        String[] cardInResources = devCardsDatabase.getInResources(cardIndex);
        String[] cardOutResources = devCardsDatabase.getOutResources(cardIndex);

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

        String empty = AsciiArts.RED_BOLD_BRIGHT + "EMPTY" + AsciiArts.RESET;
        String card = AsciiArts.RED_BOLD_BRIGHT + "CARD" + AsciiArts.RESET;

        developmentCardToPrint.add("╔══════════╗");
        developmentCardToPrint.add("║   "+empty+"  ║");
        developmentCardToPrint.add("║   "+card+"   ║");
        developmentCardToPrint.add("║──────────║");
        developmentCardToPrint.add("║    │     ║");
        developmentCardToPrint.add("║    }     ║");
        developmentCardToPrint.add("║    │     ║");
        developmentCardToPrint.add("║          ║");
        developmentCardToPrint.add("╚══════════╝");

        return developmentCardToPrint;
    }
}
