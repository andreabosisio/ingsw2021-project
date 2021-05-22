package it.polimi.ingsw.client.view.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PrintableScene extends Printable {
    private final List<String> scene;

    public PrintableScene(String scene) {
        this.scene = new ArrayList<String>(){{
            add(scene);
        }};
        setWidth(this.scene);
    }

    public PrintableScene(List<String> scene) {
        setWidth(scene);
        this.scene = scene.stream().map(this::fillWithEmptySpace).collect(Collectors.toList());
    }

    public PrintableScene(Printable scene) {
        this.scene = scene.getPrintable();
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

        int maxHeight = 0;
        for(Printable printable : args) {
            int printableSize = printable.getPrintable().size();
            if(printableSize > maxHeight)
                maxHeight = printableSize;
        }

        int i = 0;
        do {
            for (Printable printable : args) {
                List<String> pRows = printable.getPrintable();
                try {
                    row = concatenateString(row, pRows.get(i), separator);
                } catch (IndexOutOfBoundsException e) {
                    row = concatenateString(row, printable.getEmptySpace(), separator);
                }
            }
            rows.add(row);
            row = "";
            i++;
        } while (i < maxHeight);

        return new PrintableScene(rows);
    }

    public static Printable concatenatePrintable (Printable... args) {
        return concatenatePrintable("", args);
    }

    public static Printable addPrintablesToTop(Printable base, int offset, Printable ... tops) {
        Printable builder = new PrintableScene(base.getPrintable());
        for (Printable top : tops) {
            for(int i = 0; i < offset; i++)
                builder = addStringToTop(builder, "");
            List<String> reversed = top.getPrintable();
            Collections.reverse(reversed);
            for (String row : reversed) {
                builder = addStringToTop(builder, row);
            }
        }
        return builder;
    }

    public static Printable addPrintablesToTop(Printable base, Printable ... tops) {
        return addPrintablesToTop(base, 0, tops);
    }

    public static Printable addStringToTop(Printable base, String toAdd) {
        return addStringAtRow(base, toAdd, 0, 0);
    }

    public static Printable addStringToTop(Printable base, String toAdd, int offset) {
        return addStringAtRow(base, toAdd, 0, offset);
    }

    public static Printable addBottomString (Printable base, String toAdd) {
        return addStringAtRow(base, toAdd, base.getPrintable().size(), 0);
    }

    public static Printable addBottomString (Printable base, String toAdd, int offset) {
        return addStringAtRow(base, toAdd, base.getPrintable().size(), offset);
    }

    public static Printable addStringAtRow (Printable base, String toAdd, int row, int separator) {
        List<String> newPrintable = new ArrayList<>(base.getPrintable());
        newPrintable.add(row, base.fillWithEmptySpace(toAdd));
        return new PrintableScene(newPrintable);
    }

    @Override
    public List<String> getPrintable() {
        return scene;
    }
}
