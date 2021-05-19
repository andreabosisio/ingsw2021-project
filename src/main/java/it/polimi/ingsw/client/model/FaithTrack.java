package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AsciiArts;

import java.util.*;

public class FaithTrack {
    private final static int DIM_FAITH_TRACK = 25;
    private static final int DIM_POPE_REPORTS = 3;
    private final static String EMPTY_MARKER = " ";

    private static final ArrayList<String> markers = new ArrayList<String>() {{
        add(AsciiArts.BLUE + '†' + AsciiArts.RESET);
        add(AsciiArts.YELLOW + '†' + AsciiArts.RESET);
        add(AsciiArts.GREEN + '†' + AsciiArts.RESET);
        add(AsciiArts.PURPLE + '†' + AsciiArts.RESET);
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
     * Return a printable representation of the FaithTrack containing all the Players' markers.
     * Dimensions: 115*10 chars
     *
     * @return a List<String> containing the representation of the FaithTrack row by row.
     */
    public List<String> getPrintable() {
        List<String> toReturn = new ArrayList<>();

        ArrayList<String[]> tracks = new ArrayList<String[]>() {{
            add(t1);
            add(t2);
            add(t3);
            add(t4);
        }};
        ArrayList<String[]> tiles = new ArrayList<String[]>() {{
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
            for(int i = 0; i < DIM_POPE_REPORTS; i++) {
                if(r[i]) {
                    tilesByNick.get(n)[i] = AsciiArts.REVERSE_VIDEO + markerByNick.get(n) + AsciiArts.RESET;
                }
            }
        });

        toReturn.add("            ┌─────┬═════┬══"+AsciiArts.REVERSE_VIDEO+"2"+AsciiArts.RESET+"══┬═════┬═════┬──"+AsciiArts.REVERSE_VIDEO+"4"+AsciiArts.RESET+"──┐                       ┌──"+AsciiArts.REVERSE_VIDEO+"12"+AsciiArts.RESET+"─┬═════┬═════┬══"+AsciiArts.REVERSE_VIDEO+"16"+AsciiArts.RESET+"═┬═════┬═════┬══"+AsciiArts.REVERSE_VIDEO+"20"+AsciiArts.RESET+"═╗");
        toReturn.add("            │ "+t1[4]+" "+t2[4]+" ║ "+t1[5]+" "+t2[5]+" │ "+t1[6]+" "+t2[6]+" │ "+t1[7]+" "+t2[7]+" │"+AsciiArts.RED_BACKGROUND+" "+t1[8]+" "+t2[8]+" "+AsciiArts.RESET+"║ "+t1[9]+" "+t2[9]+" │                       │ "+t1[18]+" "+t2[18]+" ║ "+t1[19]+" "+t2[19]+" │ "+t1[20]+" "+t2[20]+" │ "+t1[21]+" "+t2[21]+" │ "+t1[22]+" "+t2[22]+" │ "+t1[23]+" "+t2[23]+" │"+AsciiArts.RED_BACKGROUND+" "+t1[24]+" "+t2[24]+" "+AsciiArts.RESET+"║");
        toReturn.add("            │ "+t3[4]+" "+t4[4]+" ║ "+t3[5]+" "+t4[5]+" │ "+t3[6]+" "+t4[6]+" │ "+t3[7]+" "+t4[7]+" │"+AsciiArts.RED_BACKGROUND+" "+t3[8]+" "+t4[8]+" "+AsciiArts.RESET+"║ "+t3[9]+" "+t4[9]+" │                       │ "+t3[18]+" "+t4[18]+" ║ "+t3[19]+" "+t4[19]+" │ "+t3[20]+" "+t4[20]+" │ "+t3[21]+" "+t4[21]+" │ "+t3[22]+" "+t4[22]+" │ "+t3[23]+" "+t4[23]+" │"+AsciiArts.RED_BACKGROUND+" "+t3[24]+" "+t4[24]+" "+AsciiArts.RESET+"║");
        toReturn.add("            ├─────┼═════┼─────┴─────┼═════┼─────┤     ╔═══════════╗     ├─────┼═════┴═════┼─────┴─────┼═════┴═════╝");
        toReturn.add("            "+AsciiArts.REVERSE_VIDEO+"1"+AsciiArts.RESET+" "+t1[3]+" "+t2[3]+" │     ║ "+r1[0]+"       "+r2[0]+" ║     │ "+t1[10]+" "+t2[10]+" │     ║ "+r1[1]+"   "+AsciiArts.REVERSE_VIDEO+"2"+AsciiArts.RESET+"   "+r2[1]+" ║     │ "+t1[17]+" "+t2[17]+" │           ║ "+r1[2]+"       "+r2[2]+" ║            ");
        toReturn.add("            │ "+t3[3]+" "+t4[3]+" │     ║ "+r3[0]+"   "+AsciiArts.REVERSE_VIDEO+"2"+AsciiArts.RESET+"   "+r4[0]+" ║     │ "+t3[10]+" "+t4[10]+" │     ║ "+r3[1]+"       "+r4[1]+" ║     │ "+t3[17]+" "+t4[17]+" │           ║ "+r3[2]+"   "+AsciiArts.REVERSE_VIDEO+"4"+AsciiArts.RESET+"   "+r4[2]+" ║            ");
        toReturn.add("┌─────┬─────┼─────┤     ╚═══════════╝     ├─────┼═════┼─────┬─────┼═════┼═════┤           ╚═══════════╝            ");
        toReturn.add("│ "+t1[0]+" "+t2[0]+" │ "+t1[1]+" "+ t2[1]+" │ "+t1[2]+" "+t2[2]+" │                       │ "+t1[11]+" "+t2[11]+" ║ "+t1[12]+" "+t2[12]+" │ "+t1[13]+" "+t2[13]+" │ "+t1[14]+" "+t2[14]+" │ "+t1[15]+" "+t2[15]+" │"+AsciiArts.RED_BACKGROUND+" "+t1[16]+" "+t2[16]+" "+AsciiArts.RESET+"║                                    ");
        toReturn.add("│ "+t3[0]+" "+t4[0]+" │ "+t3[1]+" "+ t4[1]+" │ "+t3[2]+" "+t4[2]+" │                       │ "+t3[11]+" "+t4[11]+" ║ "+t3[12]+" "+t4[12]+" │ "+t3[13]+" "+t4[13]+" │ "+t3[14]+" "+t4[14]+" │ "+t3[15]+" "+t4[15]+" │"+AsciiArts.RED_BACKGROUND+" "+t3[16]+" "+t4[16]+" "+AsciiArts.RESET+"║                                    ");
        toReturn.add("└─────┴─────┴─────┘                       └─────┴══"+AsciiArts.REVERSE_VIDEO+"6"+AsciiArts.RESET+"══┴═════┴═════┴══"+AsciiArts.REVERSE_VIDEO+"9"+AsciiArts.RESET+"══┴═════┘ ");


        return toReturn;
    }

}
