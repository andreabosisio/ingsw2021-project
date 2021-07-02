package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.client.view.cli.Printable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that has the capacity to print the Market Tray
 */
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

    /**
     * Update the data of the Market Tray.
     */
    private void setMarketBoard() {
        extraSlot = market.get(0);
        int k = 1;
        for (int i = 0; i < NUM_R; i++)
            for (int j = 0; j < NUM_C; j++) {
                marketBoard[i][j] = market.get(k);
                k++;
            }
    }

    /**
     * Return a printable representation of the MarketTray.
     *
     * @return a List<String> containing the representation of the MarketTray row by row.
     */
    @Override
    public List<String> getPrintable() {
        List<String> toReturn = new ArrayList<>();
        StringBuilder printable = new StringBuilder();

        printable.append("┌────────");

        for (int i = 1; i < NUM_C; i++) {
            if (i == NUM_C - 1)
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
                    printable.append("  ").append(AnsiUtilities.WHITE_BRIGHT).append(AnsiUtilities.LEFT_ARROW.getAsciiArt()).append(" [").append(i).append("]").append(AnsiUtilities.RESET);
                }
            }
            toReturn.add(printable.toString());
            toReturn.add(getNUMCBottomSlots().toString());

            if (i != NUM_R - 1) {
                toReturn.add(getNUMCTopSlots().toString());
            }
        }
        toReturn.add(getNUMCUpArrows().toString());
        toReturn.add(getNUMCBottomIndexes().toString());

        setWidth(toReturn);
        return toReturn.stream().map(this::fillWithEmptySpace).collect(Collectors.toList());
    }

    /**
     * @return the Ascii Art of the top of NUMC slots
     */
    private StringBuilder getNUMCTopSlots() {
        StringBuilder topCorners = new StringBuilder();

        topCorners.append("│ ┌───┐");
        for (int i = 1; i < NUM_C; i++)
            topCorners.append("┌───┐");

        return topCorners;
    }

    /**
     * @return the Ascii Art of the bottom of NUMC slots
     */
    private StringBuilder getNUMCBottomSlots() {
        StringBuilder bottomCorners = new StringBuilder();

        bottomCorners.append("│ └───┘");
        for (int i = 1; i < NUM_C; i++)
            bottomCorners.append("└───┘");

        return bottomCorners;
    }

    /**
     * @return the Ascii Art of NUMC up arrows
     */
    private StringBuilder getNUMCUpArrows() {
        StringBuilder arrows = new StringBuilder();

        arrows.append("  ");
        for (int i = NUM_C; i > 0; i--)
            arrows.append("  ").append(AnsiUtilities.WHITE_BRIGHT).append(AnsiUtilities.UP_ARROW.getAsciiArt()).append("  ").append(AnsiUtilities.RESET);

        return arrows;
    }

    /**
     * @return the Ascii Art of NUMC bottom indexes
     */
    private StringBuilder getNUMCBottomIndexes() {
        StringBuilder arrows = new StringBuilder();
        arrows.append("  ");
        for (int i = NUM_C; i > 0; i--)
            arrows.append(AnsiUtilities.WHITE_BRIGHT).append(" [").append(i + 2).append("] ").append(AnsiUtilities.RESET);

        return arrows;
    }

    /**
     * Update the data of the Market Tray and call the view to show the update.
     *
     * @param view the current View
     */
    public void update(View view) {
        setMarketBoard();
        Board.getBoard().setMarketTray(this);
        view.marketUpdate();
    }

    /**
     * @return the content of the Market Tray row by row.
     */
    public List<String> toStringList() {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(extraSlot);
        for (int i = 0; i < NUM_R; i++)
            toReturn.addAll(Arrays.asList(marketBoard[i]).subList(0, NUM_C));

        return toReturn;
    }

}
