package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AsciiArts;

import java.util.*;

/**
 * Class that has the capacity to return a printable version of a specific Leader Card
 */
public class LeaderCard {

    /**
     * This method return the print of a specific Leader Card
     *
     * @param cardIndex is the index of the Card to print
     * @return a List composed by the lines of the Card
     */
    public List<String> getPrintableLeaderCard(String cardIndex) {
        List<String> leaderCardToPrint = new ArrayList<>();
        LeaderCardsDatabase leaderCardsDatabase = LeaderCardsDatabase.getLeaderCardsDatabase();

        String[] requirements = leaderCardsDatabase.getRequirements(cardIndex);
        String victoryPoints = leaderCardsDatabase.getVictoryPoints(cardIndex);

        leaderCardToPrint.add("╔══════════╗");
        leaderCardToPrint.add("║"+requirements[0]+"║");
        leaderCardToPrint.add("║"+requirements[1]+"║");
        leaderCardToPrint.add("║──────────║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║        "+victoryPoints+"║");
        leaderCardToPrint.add("╚══════════╝");

        return leaderCardToPrint;
    }

    /**
     * This method return the print of an empty card
     *
     * @return a List composed by the lines of the Card
     */
    public List<String> getPrintableEmptyLeaderCard() {
        List<String> leaderCardToPrint = new ArrayList<>();

        String empty = AsciiArts.RED_BOLD_BRIGHT + "EMPTY" + AsciiArts.RESET;
        String card = AsciiArts.RED_BOLD_BRIGHT + "CARD" + AsciiArts.RESET;

        leaderCardToPrint.add("╔══════════╗");
        leaderCardToPrint.add("║   "+empty+"  ║");
        leaderCardToPrint.add("║   "+card+"   ║");
        leaderCardToPrint.add("║──────────║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("╚══════════╝");

        return leaderCardToPrint;
    }
}
