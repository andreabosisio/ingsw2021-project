package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.client.view.cli.Printable;
import it.polimi.ingsw.client.view.cli.PrintableScene;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that has the capacity to return a printable version of a specific Development Card
 */
public class DevelopmentCard extends Printable {

    private final String iD;

    public static final String EMPTY_CARD_ID = "empty";

    /**
     * Create a new Development Card by specifying the ID.
     *
     * @param iD The ID of the Card
     */
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

        DevelopmentCardsDatabase devCardsDatabase = DevelopmentCardsDatabase.getDevelopmentCardsDatabase();

        // contains the three possible resources to buy the card
        String[] cardPrice = devCardsDatabase.getPriceOf(iD);
        // contains the level of the Card colored like the color of the Card
        String cardLevel = this.getColoredLevel();
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
     * Create a Printable object that represents this card on top of other cards letting visible only the old cards' level and color.
     *
     * @param oldCards The Printable object representing the cards on which this card is going to be placed
     * @return a new Printable object that represents this card on top of other cards letting visible only the old cards' level and color
     */
    public Printable placeOnOtherCards(Printable oldCards) {
        if (oldCards.getPrintable().size() == 0)
            return this;
        Printable cutCards = new PrintableScene(oldCards.getPrintable().subList(0, oldCards.getPrintable().size() - this.getPrintable().size() + 2));
        return PrintableScene.addPrintablesToTop(this, cutCards);
    }

    /**
     * Get method that return the level of the Card:
     * The number is colored like the color of the card
     *
     * @return the colored level of the Card
     */
    private String getColoredLevel() {
        return AnsiUtilities.colorString(String.valueOf(DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getLevelOf(this.iD)), DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getColorOf(this.iD));
    }
}
