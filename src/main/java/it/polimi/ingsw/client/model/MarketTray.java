package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AsciiArts;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MarketTray extends Printable {

    /**
     * Number of columns of the MarketBoard
     */
    private static final int NUM_C = 4;

    /**
     * Number of rows of the MarketBoard
     */
    private static final int NUM_R = 3;

    /**
     * The MarketBoard
     */
    private static final String[][] marketBoard = new String[NUM_R][NUM_C];

    /**
     * The marble out of the MarketGrid
     */
    private String extraSlot;

    private final List<String> market;

    public MarketTray(List<String> market) {
        this.market = market;
    }

    public final void setMarketBoard() {
        extraSlot = market.get(0);

        int k = 1;
        for (int i = 0; i < NUM_R; i++)
            for (int j = 0; j < NUM_C; j++) {
                marketBoard[i][j] = market.get(k);
                k++;
            }
    }
/*
    public String[] getAsArray(StringBuilder printable) {
        int lastIndex = 0;
        int currentIndex;

        do {
            currentIndex = printable.indexOf("\n", lastIndex);

        } while (currentIndex != -1);
    }
 */

    /**
     * Return a printable representation of the MarketTray.
     * Dimensions: 21*12 chars
     *
     * @return a List<String> containing the representation of the MarketTray row by row.
     */
    @Override
    public List<String> getPrintable() {
        List<String> toReturn = new ArrayList<>();
        StringBuilder printable = new StringBuilder();

        printable.append("┌────────");

        for (int i = 1; i < NUM_C; i++) {
            if (i == NUM_C - 1 )
                printable.append("─").append(Marble.getPrintable(extraSlot)).append("─");
            else
                printable.append("────");
        }

        toReturn.add(printable.toString());

        toReturn.add(getNUMCTopSlots().toString());


        for (int i = 0; i < NUM_R; i++) {
            printable = new StringBuilder();

            printable.append("│ ");
            for (int j = 0; j < NUM_C; j++) {
                printable.append("│").append(Marble.getPrintable(marketBoard[i][j])).append("│");
                if (j == NUM_C - 1) {
                    printable.append("  ").append(AsciiArts.WHITE_BRIGHT).append(AsciiArts.LEFT_ARROW.getAsciiArt()).append(" [").append(i).append("]").append(AsciiArts.RESET);
                }
            }
            toReturn.add(printable.toString());
            toReturn.add(getNUMCBottomSlots().toString());

            if(i != NUM_R - 1) {
                toReturn.add(getNUMCTopSlots().toString());
            }
        }
        toReturn.add(getNUMCUpArrows().toString());
        toReturn.add(getNUMCBottomIndexes().toString());

        setWidth(toReturn);
        return toReturn.stream().map(this::fillWithEmptySpace).collect(Collectors.toList());
    }

    private StringBuilder getNUMCTopSlots() {
        StringBuilder topCorners = new StringBuilder();

        topCorners.append("│ ┌───┐");
        for (int i = 1; i < NUM_C; i++)
            topCorners.append("┌───┐");

        return topCorners;
    }

    private StringBuilder getNUMCBottomSlots() {
        StringBuilder bottomCorners = new StringBuilder();

        bottomCorners.append("│ └───┘");
        for (int i = 1; i < NUM_C; i++)
            bottomCorners.append("└───┘");

        return bottomCorners;
    }

    private StringBuilder getNUMCUpArrows() {
        StringBuilder arrows = new StringBuilder();

        arrows.append("  ");
        for (int i = NUM_C; i > 0; i--)
            arrows.append("  ").append(AsciiArts.WHITE_BRIGHT).append(AsciiArts.UP_ARROW.getAsciiArt()).append("  ").append(AsciiArts.RESET);

        return arrows;
    }

    private StringBuilder getNUMCBottomIndexes() {
        StringBuilder arrows = new StringBuilder();

        arrows.append("  ");
        for (int i = NUM_C; i > 0; i--)
            arrows.append(AsciiArts.WHITE_BRIGHT).append(" [").append(i + 2).append("] ").append(AsciiArts.RESET);

        return arrows;
    }

    public void update() {
        setMarketBoard();
        Board.getBoard().setMarketTray(this);
    }

}
