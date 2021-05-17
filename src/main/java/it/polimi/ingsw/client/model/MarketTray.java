package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AsciiArts;
import it.polimi.ingsw.client.view.cli.CLI;

import java.util.List;

public class MarketTray {
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

    public StringBuilder getPrintable(int xShift) {

        //todo why i have to do this here ?
        setMarketBoard();
        StringBuilder printable = new StringBuilder();

        printable.append(CLI.getHorizontalShift(xShift));
        printable.append("┌─────");

        for (int i = 1; i < NUM_C; i++) {
            if (i == NUM_C - 1 )
                printable.append("─").append(Marble.getAsciiMarbleByColor(extraSlot)).append("─");
            else
                printable.append("────");
        }

        printable.append("\n");
        printable.append(getNUMCTopSlots(xShift));
        printable.append("\n");

        for (int i = 0; i < NUM_R; i++) {
            printable.append(CLI.getHorizontalShift(xShift));
            printable.append("│ ");
            for (int j = 0; j < NUM_C; j++) {
                printable.append("│").append(Marble.getAsciiMarbleByColor(marketBoard[i][j])).append("│");
                if (j == NUM_C - 1) {
                    printable.append("  ").append(AsciiArts.WHITE_BRIGHT).append(AsciiArts.LEFT_ARROW.getAsciiArt()).append(" [").append(i).append("]").append(AsciiArts.RESET);
                }
            }
            printable.append("\n");
            printable.append(getNUMCBottomSlots(xShift));
            printable.append("\n");

            if(i != NUM_R - 1) {
                printable.append(getNUMCTopSlots(xShift));
                printable.append("\n");
            }
        }
        printable.append(getNUMCUpArrows(xShift)).append("\n");

        //System.out.println( "\u001b[100C" + printable);
        return printable;
    }

    private StringBuilder getNUMCTopSlots(int xShift) {
        StringBuilder topCorners = new StringBuilder();
        topCorners.append(CLI.getHorizontalShift(xShift));
        topCorners.append("│ ┌──┐");
        for (int i = 1; i < NUM_C; i++)
            topCorners.append("┌──┐");

        return topCorners;
    }

    private StringBuilder getNUMCBottomSlots(int xShift) {
        StringBuilder bottomCorners = new StringBuilder();
        bottomCorners.append(CLI.getHorizontalShift(xShift));
        bottomCorners.append("│ └──┘");
        for (int i = 1; i < NUM_C; i++)
            bottomCorners.append("└──┘");

        return bottomCorners;
    }

    private StringBuilder getNUMCUpArrows(int xShift) {
        StringBuilder arrows = new StringBuilder();

        arrows.append(CLI.getHorizontalShift(xShift));
        arrows.append("  ");
        for (int i = NUM_C; i > 0; i--)
            arrows.append(" ").append(AsciiArts.WHITE_BRIGHT).append(AsciiArts.UP_ARROW.getAsciiArt()).append("  ").append(AsciiArts.RESET);

        arrows.append("\n");
        arrows.append(CLI.getHorizontalShift(xShift));
        arrows.append("  ");

        for (int i = NUM_C; i > 0; i--)
            arrows.append(AsciiArts.WHITE_BRIGHT).append("[").append(i + 2).append("] ").append(AsciiArts.RESET);

        return arrows;
    }

}
