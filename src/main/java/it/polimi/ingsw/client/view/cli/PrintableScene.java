package it.polimi.ingsw.client.view.cli;

import java.util.ArrayList;
import java.util.List;

public class PrintableScene extends Printable {
    private final List<String> scene;

    public PrintableScene(String scene) {
        this.scene = new ArrayList<String>(){{
            add(scene);
        }};
        setWidth(this.scene);
    }

    public PrintableScene(List<String> scene) {
        this.scene = scene;
        setWidth(this.scene);
    }

    public static String concatenateString (String... args) {
        StringBuilder concatenator = new StringBuilder();
        for (String toConcatenate : args)
            concatenator.append(toConcatenate);
        return concatenator.toString();
    }

    public static Printable concatenatePrintable (List<Printable> printables) {
        return concatenatePrintable(printables, "");
    }

    public static Printable concatenatePrintable (List<Printable> printables, String separator) {
        return concatenatePrintable(separator, printables.toArray(new Printable[0]));
    }

    public static Printable concatenatePrintable (String separator, Printable... args) {
        List<String> rows  = new ArrayList<>();
        String row = "";
        int i = 0;
        int maxSize = 0;
        do {
            for (Printable printable : args) {
                List<String> pRows = printable.getPrintable();
                if(pRows.size() > maxSize)
                    maxSize = pRows.size();
                try {
                    row = concatenateString(row, pRows.get(i), separator);
                } catch (IndexOutOfBoundsException e) {
                    row = concatenateString(row, printable.getEmptySpace(), separator);
                }
            }
            rows.add(row);
            row = "";
            i++;
        } while (i < maxSize);

        return new PrintableScene(rows);
    }

    public static Printable concatenatePrintable (Printable... args) {
        return concatenatePrintable("", args);
    }

    public static Printable addTopString (Printable base, String toAdd) {
        return addStringAtRow(base, toAdd, 0);
    }

    public static Printable addBottomString (Printable base, String toAdd) {
        return addStringAtRow(base, toAdd, base.getPrintable().size());
    }

    public static Printable addStringAtRow (Printable base, String toAdd, int row) {
        List<String> newPrintable = new ArrayList<>(base.getPrintable());
        newPrintable.add(row, String.format("%-" + base.getWidth() + "s", toAdd));
        return new PrintableScene(newPrintable);
    }

    @Override
    public List<String> getPrintable() {
        return scene;
    }
}
