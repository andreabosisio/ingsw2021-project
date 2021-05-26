package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiEnum;
import it.polimi.ingsw.client.view.cli.Printable;

import java.util.ArrayList;
import java.util.List;

public class BasicPowerCard extends DevelopmentCard{

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

        String basic = AnsiEnum.YELLOW_BOLD + "Basic" + AnsiEnum.RESET;
        String power = AnsiEnum.YELLOW_BOLD + "Power" + AnsiEnum.RESET;

        developmentCardToPrint.add("╔══════════╗");
        developmentCardToPrint.add("║  "+basic+"   ║");
        developmentCardToPrint.add("║  "+power+"   ║");
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
    public Printable placeOnAnotherCards(Printable oldCards) {
        return this;
    }
}
