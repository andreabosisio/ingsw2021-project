package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.client.view.cli.Printable;
import it.polimi.ingsw.client.view.cli.PrintableScene;

import java.util.*;

/**
 * Faith Track implementation for the Client
 */
public class FaithTrack extends Printable {
    private final static int DIM_FAITH_TRACK = 25;
    private static final int DIM_POPE_REPORTS = 3;
    private final static String EMPTY_MARKER = " ";

    private static final ArrayList<String> markers = new ArrayList<>() {{
        add(AnsiUtilities.BLUE + '†' + AnsiUtilities.RESET);
        add(AnsiUtilities.YELLOW + '†' + AnsiUtilities.RESET);
        add(AnsiUtilities.GREEN + '†' + AnsiUtilities.RESET);
        add(AnsiUtilities.PURPLE + '†' + AnsiUtilities.RESET);
    }};

    private final HashMap<String, Integer> indexes;
    private final HashMap<String, Boolean[]> reports;

    private static final String[] t1 = new String[DIM_FAITH_TRACK];
    private static final String[] t2 = new String[DIM_FAITH_TRACK];
    private static final String[] t3 = new String[DIM_FAITH_TRACK];
    private static final String[] t4 = new String[DIM_FAITH_TRACK];

    private static final String[] r1 = new String[DIM_POPE_REPORTS];
    private static final String[] r2 = new String[DIM_POPE_REPORTS];
    private static final String[] r3 = new String[DIM_POPE_REPORTS];
    private static final String[] r4 = new String[DIM_POPE_REPORTS];

    private static final Map<String, String> markerByNick = new HashMap<>();
    private static final Map<String, String[]> trackByNick = new HashMap<>();
    private static final Map<String, String[]> tilesByNick = new HashMap<>();

    /**
     * Create a Faith Track by specifying all the needed parameters.
     *
     * @param indexes Map containing all the couples (Nickname, Last Position)
     * @param reports Map containing all the couples (Nickname, Activated Pope Tiles)
     */
    public FaithTrack(HashMap<String, Integer> indexes, HashMap<String, Boolean[]> reports) {
        this.indexes = indexes;
        this.reports = reports;
    }

    /**
     * @return a Map<String, String> of all nicknames with their own marker symbol
     */
    public static Map<String, String> getMarkerByNick() {
        return markerByNick;
    }

    /**
     * Get all the couples (playerNick, lastPoistion)
     *
     * @return all the couples (playerNick, lastPoistion)
     */
    public HashMap<String, Integer> getIndexes() {
        return new HashMap<>(indexes);
    }

    /**
     * Get all the couples (playerNick, activePopeTiles)
     *
     * @return all the couples (playerNick, activePopeTiles)
     */
    public HashMap<String, Boolean[]> getReports() {
        return new HashMap<>(reports);
    }

    /**
     * Return a printable representation of the FaithTrack containing all the Players' markers.
     *
     * @return a List<String> containing the representation of the FaithTrack row by row.
     */
    @Override
    public List<String> getPrintable() {
        List<String> faithTrack = new ArrayList<>();

        ArrayList<String[]> tracks = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
            add(t4);
        }};
        ArrayList<String[]> tiles = new ArrayList<>() {{
            add(r1);
            add(r2);
            add(r3);
            add(r4);
        }};

        tracks.forEach(a -> Arrays.fill(a, EMPTY_MARKER));
        tiles.forEach(a -> Arrays.fill(a, EMPTY_MARKER));

        final int[] j = {0};
        indexes.forEach((n, i) -> {
            markerByNick.put(n, markers.get(j[0]));
            trackByNick.put(n, tracks.get(j[0]));
            tilesByNick.put(n, tiles.get(j[0]));
            j[0] = j[0] + 1;
        });

        indexes.forEach((n, i) -> trackByNick.get(n)[i] = markerByNick.get(n));
        reports.forEach((n, r) -> {
            for (int i = 0; i < DIM_POPE_REPORTS; i++) {
                if (r[i]) {
                    tilesByNick.get(n)[i] = AnsiUtilities.REVERSE_VIDEO + markerByNick.get(n) + AnsiUtilities.RESET;
                }
            }
        });

        faithTrack.add("            ┌─────┬═════┬══" + AnsiUtilities.REVERSE_VIDEO + "2" + AnsiUtilities.RESET + "══┬═════┬═════┬──" + AnsiUtilities.REVERSE_VIDEO + "4" + AnsiUtilities.RESET + "──┐                       ┌──" + AnsiUtilities.REVERSE_VIDEO + "12" + AnsiUtilities.RESET + "─┬═════┬═════┬══" + AnsiUtilities.REVERSE_VIDEO + "16" + AnsiUtilities.RESET + "═┬═════┬═════┬══" + AnsiUtilities.REVERSE_VIDEO + "20" + AnsiUtilities.RESET + "═╗");
        faithTrack.add("            │ " + t1[4] + " " + t2[4] + " ║ " + t1[5] + " " + t2[5] + " │ " + t1[6] + " " + t2[6] + " │ " + t1[7] + " " + t2[7] + " │" + AnsiUtilities.RED_BACKGROUND + " " + t1[8] + " " + t2[8] + " " + AnsiUtilities.RESET + "║ " + t1[9] + " " + t2[9] + " │                       │ " + t1[18] + " " + t2[18] + " ║ " + t1[19] + " " + t2[19] + " │ " + t1[20] + " " + t2[20] + " │ " + t1[21] + " " + t2[21] + " │ " + t1[22] + " " + t2[22] + " │ " + t1[23] + " " + t2[23] + " │" + AnsiUtilities.RED_BACKGROUND + " " + t1[24] + " " + t2[24] + " " + AnsiUtilities.RESET + "║");
        faithTrack.add("            │ " + t3[4] + " " + t4[4] + " ║ " + t3[5] + " " + t4[5] + " │ " + t3[6] + " " + t4[6] + " │ " + t3[7] + " " + t4[7] + " │" + AnsiUtilities.RED_BACKGROUND + " " + t3[8] + " " + t4[8] + " " + AnsiUtilities.RESET + "║ " + t3[9] + " " + t4[9] + " │                       │ " + t3[18] + " " + t4[18] + " ║ " + t3[19] + " " + t4[19] + " │ " + t3[20] + " " + t4[20] + " │ " + t3[21] + " " + t4[21] + " │ " + t3[22] + " " + t4[22] + " │ " + t3[23] + " " + t4[23] + " │" + AnsiUtilities.RED_BACKGROUND + " " + t3[24] + " " + t4[24] + " " + AnsiUtilities.RESET + "║");
        faithTrack.add("            ├─────┼═════┼─────┴─────┼═════┼─────┤     ╔═══════════╗     ├─────┼═════┴═════┼─────┴─────┼═════┴═════╝");
        faithTrack.add("            " + AnsiUtilities.REVERSE_VIDEO + "1" + AnsiUtilities.RESET + " " + t1[3] + " " + t2[3] + " │     ║ " + r1[0] + "       " + r2[0] + " ║     │ " + t1[10] + " " + t2[10] + " │     ║ " + r1[1] + "   " + AnsiUtilities.REVERSE_VIDEO + "2" + AnsiUtilities.RESET + "   " + r2[1] + " ║     │ " + t1[17] + " " + t2[17] + " │           ║ " + r1[2] + "       " + r2[2] + " ║            ");
        faithTrack.add("            │ " + t3[3] + " " + t4[3] + " │     ║ " + r3[0] + "   " + AnsiUtilities.REVERSE_VIDEO + "2" + AnsiUtilities.RESET + "   " + r4[0] + " ║     │ " + t3[10] + " " + t4[10] + " │     ║ " + r3[1] + "       " + r4[1] + " ║     │ " + t3[17] + " " + t4[17] + " │           ║ " + r3[2] + "   " + AnsiUtilities.REVERSE_VIDEO + "4" + AnsiUtilities.RESET + "   " + r4[2] + " ║            ");
        faithTrack.add("┌─────┬─────┼─────┤     ╚═══════════╝     ├─────┼═════┼─────┬─────┼═════┼═════┤           ╚═══════════╝            ");
        faithTrack.add("│ " + t1[0] + " " + t2[0] + " │ " + t1[1] + " " + t2[1] + " │ " + t1[2] + " " + t2[2] + " │                       │ " + t1[11] + " " + t2[11] + " ║ " + t1[12] + " " + t2[12] + " │ " + t1[13] + " " + t2[13] + " │ " + t1[14] + " " + t2[14] + " │ " + t1[15] + " " + t2[15] + " │" + AnsiUtilities.RED_BACKGROUND + " " + t1[16] + " " + t2[16] + " " + AnsiUtilities.RESET + "║                                    ");
        faithTrack.add("│ " + t3[0] + " " + t4[0] + " │ " + t3[1] + " " + t4[1] + " │ " + t3[2] + " " + t4[2] + " │                       │ " + t3[11] + " " + t4[11] + " ║ " + t3[12] + " " + t4[12] + " │ " + t3[13] + " " + t4[13] + " │ " + t3[14] + " " + t4[14] + " │ " + t3[15] + " " + t4[15] + " │" + AnsiUtilities.RED_BACKGROUND + " " + t3[16] + " " + t4[16] + " " + AnsiUtilities.RESET + "║                                    ");
        faithTrack.add("└─────┴─────┴─────┘                       └─────┴══" + AnsiUtilities.REVERSE_VIDEO + "6" + AnsiUtilities.RESET + "══┴═════┴═════┴══" + AnsiUtilities.REVERSE_VIDEO + "9" + AnsiUtilities.RESET + "══┴═════┘ ");

        setWidth(faithTrack);
        return faithTrack;
    }

    /**
     * Create a Scene containing the Faith Track with the legend of all the players.
     *
     * @return the generated Scene
     */
    public PrintableScene getFaithTrackWithLegendScene() {
        List<String> legend = new ArrayList<>();
        markerByNick.forEach((nick, marker) -> legend.add(AnsiUtilities.WHITE_BRIGHT + nick + AnsiUtilities.RESET + ": " + marker));

        return new PrintableScene(PrintableScene.concatenatePrintables(" ", new PrintableScene(legend), new PrintableScene(getPrintable())));
    }

    /**
     * Update the View with this object data and update the Board.
     *
     * @param view The current UI
     */
    public void update(View view) {
        Board.getBoard().setFaithTrack(this);
        view.faithTracksUpdate();
    }

}
