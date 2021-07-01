package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.client.view.cli.Printable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that has the capacity to return a printable version of a Basic Power Card
 */
public class BasicPowerCard extends DevelopmentCard {

    /**
     * Create a new Basic Power Card by the ID.
     *
     * @param iD of the Basic Power Card
     */
    public BasicPowerCard(String iD) {
        super(iD);
    }

    /**
     * This method return the print of a the Basic Power Card,
     *
     * @return a List composed by the lines of the Card
     */
    @Override
    public List<String> getPrintable() {
        List<String> developmentCardToPrint = new ArrayList<>();

        String basic = AnsiUtilities.YELLOW_BOLD + "Basic" + AnsiUtilities.RESET;
        String power = AnsiUtilities.YELLOW_BOLD + "Power" + AnsiUtilities.RESET;

        developmentCardToPrint.add("╔══════════╗");
        developmentCardToPrint.add("║  " + basic + "   ║");
        developmentCardToPrint.add("║  " + power + "   ║");
        developmentCardToPrint.add("║──────────║");
        developmentCardToPrint.add("║  ? │     ║");
        developmentCardToPrint.add("║    } ?   ║");
        developmentCardToPrint.add("║  ? │     ║");
        developmentCardToPrint.add("║          ║");
        developmentCardToPrint.add("╚══════════╝");

        setWidth(developmentCardToPrint);
        return developmentCardToPrint;
    }

    /**
     * The Basic Power Card cannot be placed on top of another card, so this method returns the Basic Basic Card.
     *
     * @param oldCards The Printable object representing the cards on which this card is going to be placed
     * @return this
     */
    @Override
    public Printable placeOnOtherCards(Printable oldCards) {
        return this;
    }
}
