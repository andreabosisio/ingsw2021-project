package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiEnum;
import it.polimi.ingsw.client.view.cli.Printable;

import java.util.*;

/**
 * Class that has the capacity to return a printable version of a specific Leader Card
 */
public class LeaderCard extends Printable {
    private static final String EMPTY_CARD = "empty";

    private final String iD;

    public LeaderCard(String iD) {
        this.iD = iD;
    }

    /**
     * This method return the print of the card
     *
     * @return a List composed by the lines of the Card
     */
    @Override
    public List<String> getPrintable() {
        List<String> leaderCardToPrint = new ArrayList<>();

        if (iD.equals(EMPTY_CARD))
            return getPrintableEmptyCard();

        LeaderCardsDatabase leaderCardsDatabase = LeaderCardsDatabase.getLeaderCardsDatabase();

        String[] requirements = leaderCardsDatabase.getRequirements(iD);
        String victoryPoints = leaderCardsDatabase.getVictoryPoints(iD);
        String ability = leaderCardsDatabase.getAbility(iD);

        leaderCardToPrint.add("╔══════════╗");
        leaderCardToPrint.add("║" + requirements[0] + "║");
        leaderCardToPrint.add("║" + requirements[1] + "║");
        leaderCardToPrint.add("║──────────║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║"+ability+" ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║"+"leader"+"  " + victoryPoints + "║");
        leaderCardToPrint.add("╚══════════╝");

        setWidth(leaderCardToPrint);
        return leaderCardToPrint;
    }

    /**
     * This method return the print of an empty card
     *
     * @return a List composed by the lines of the Card
     */
    private List<String> getPrintableEmptyCard() {
        List<String> leaderCardToPrint = new ArrayList<>();

        String empty = AnsiEnum.RED_BOLD_BRIGHT + "EMPTY" + AnsiEnum.RESET;
        String card = AnsiEnum.RED_BOLD_BRIGHT + "CARD" + AnsiEnum.RESET;

        leaderCardToPrint.add("╔══════════╗");
        leaderCardToPrint.add("║   " + empty + "  ║");
        leaderCardToPrint.add("║   " + card + "   ║");
        leaderCardToPrint.add("║──────────║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("╚══════════╝");

        return leaderCardToPrint;
    }
}
