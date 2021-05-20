package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AsciiArts;

import java.util.*;

/**
 * Class that has the capacity to return a printable version of a specific Development Card
 */
public class DevelopmentCard extends Printable{

    private static final String EMPTY_CARD = "empty";

    private final String iD;

    public DevelopmentCard(String iD) {
        this.iD = iD;
    }

    /**
     * This method return the print of a specific Development Card
     *
     * @return a List composed by the lines of the Card
     */
    @Override
    public List<String> getPrintable() {
        List<String> developmentCardToPrint = new ArrayList<>();

        if (iD.equals(EMPTY_CARD))
            return getPrintableEmptyCard();

        DevelopmentCardsDatabase devCardsDatabase = DevelopmentCardsDatabase.getDevelopmentCardsDatabase();

        // contains the three possible resources to buy the card
        String[] cardPrice = devCardsDatabase.getPrice(iD);
        // contains the level of the Card colored like the color of the Card
        String cardLevel = devCardsDatabase.getColoredLevel(iD);
        // contains the victory points of the Card (vP < 9 ? " " + 3 : 11)
        String cardVictoryPoints = devCardsDatabase.getVictoryPoints(iD);
        String[] cardInResources = devCardsDatabase.getInResources(iD);
        String[] cardOutResources = devCardsDatabase.getOutResources(iD);

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
    private List<String> getPrintableEmptyCard() {
        List<String> developmentCardToPrint = new ArrayList<>();

        String empty = AsciiArts.RED_BOLD_BRIGHT + "EMPTY" + AsciiArts.RESET;
        String card = AsciiArts.RED_BOLD_BRIGHT + "CARD" + AsciiArts.RESET;

        developmentCardToPrint.add("╔══════════╗");
        developmentCardToPrint.add("║   " + empty + "  ║");
        developmentCardToPrint.add("║   " + card + "   ║");
        developmentCardToPrint.add("║──────────║");
        developmentCardToPrint.add("║    │     ║");
        developmentCardToPrint.add("║    }     ║");
        developmentCardToPrint.add("║    │     ║");
        developmentCardToPrint.add("║          ║");
        developmentCardToPrint.add("╚══════════╝");

        return developmentCardToPrint;
    }

}
