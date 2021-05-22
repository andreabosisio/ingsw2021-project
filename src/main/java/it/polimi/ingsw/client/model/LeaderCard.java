package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiEnum;
import it.polimi.ingsw.client.view.cli.Printable;

import java.util.*;

/**
 * Class that has the capacity to return a printable version of a specific Leader Card
 */
public class LeaderCard extends Printable {
    private static final String EMPTY_CARD_ID = "empty";

    private final String iD;

    public LeaderCard(String iD) {
        this.iD = iD;
    }

    public String getiD() {
        return iD;
    }

    public static String getEmptyCardID() {
        return EMPTY_CARD_ID;
    }

    /**
     * This method return the print of the card
     *
     * @return a List composed by the lines of the Card
     */
    @Override
    public List<String> getPrintable() {
        List<String> leaderCardToPrint = new ArrayList<>();

        if (iD.equals(EMPTY_CARD_ID))
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
        leaderCardToPrint.add("║" + ability + " ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║" + "leader" + "  " + victoryPoints + "║");
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

        leaderCardToPrint.add("╔══════════╗");
        leaderCardToPrint.add("║   " + "Leader" + " ║");
        leaderCardToPrint.add("║   " + "Card" + "   ║");
        leaderCardToPrint.add("║   " + "Slot" + "   ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("║          ║");
        leaderCardToPrint.add("╚══════════╝");

        return leaderCardToPrint;
    }
}
