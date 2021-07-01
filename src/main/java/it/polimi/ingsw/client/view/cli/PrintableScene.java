package it.polimi.ingsw.client.view.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PrintableScene extends Printable {
    private final List<String> scene;

    /**
     * Create a Printable Scene from a general Scene.
     *
     * @param scene that generate the Printable Scene
     */
    public PrintableScene(String scene) {
        this.scene = new ArrayList<>() {{
            add(scene);
        }};
        setWidth(this.scene);
    }

    /**
     * Create a Printable Scene from a general Scene.
     *
     * @param scene that generate the Printable Scene
     */
    public PrintableScene(List<String> scene) {
        setWidth(scene);
        this.scene = scene.stream().map(this::fillWithEmptySpace).collect(Collectors.toList());
    }

    /**
     * Create a Printable Scene from a general Printable.
     *
     * @param scene that generate the Printable Scene
     */
    public PrintableScene(Printable scene) {
        this.scene = scene.getPrintable();
        setWidth(this.scene);
    }

    /**
     * Concatenates multiple Strings.
     *
     * @param args Multiple Strings to concatenate
     * @return a String containing all the concatenated Strings
     */
    public static String concatenateStrings(String... args) {
        StringBuilder concatenator = new StringBuilder();
        for (String toConcatenate : args)
            concatenator.append(toConcatenate);
        return concatenator.toString();
    }

    /**
     * Concatenates multiple Printables.
     *
     * @param printables Multiple Printables to concatenate
     * @return a Printable containing all the concatenated Printables
     */
    public static Printable concatenatePrintables(List<Printable> printables) {
        return concatenatePrintables(printables, "");
    }

    /**
     * Concatenates multiple Printables.
     *
     * @param printables Multiple Printables to concatenate
     * @param separator  String to added between each Printable to concatenate
     * @return a Printable containing all the concatenated Printables
     */
    public static Printable concatenatePrintables(List<Printable> printables, String separator) {
        return concatenatePrintables(separator, printables.toArray(new Printable[0]));
    }

    /**
     * Concatenates multiple Printables.
     *
     * @param args      Multiple Printables to concatenate
     * @param separator String to added between each Printable to concatenate
     * @return a Printable containing all the concatenated Printables
     */
    public static Printable concatenatePrintables(String separator, Printable... args) {
        List<String> rows = new ArrayList<>();
        String row = "";

        int maxHeight = 0;
        for (Printable printable : args) {
            int printableSize = printable.getPrintable().size();
            if (printableSize > maxHeight)
                maxHeight = printableSize;
        }

        int i = 0;
        do {
            for (Printable printable : args) {
                List<String> pRows = printable.getPrintable();
                try {
                    row = concatenateStrings(row, pRows.get(i), separator);
                } catch (IndexOutOfBoundsException e) {
                    row = concatenateStrings(row, printable.getEmptySpace(), separator);
                }
            }
            rows.add(row);
            row = "";
            i++;
        } while (i < maxHeight);

        return new PrintableScene(rows);
    }

    /**
     * Concatenates multiple Printables.
     *
     * @param args Multiple Printables to concatenate
     * @return a Printable containing all the concatenated Printables
     */
    public static Printable concatenatePrintables(Printable... args) {
        return concatenatePrintables("", args);
    }

    /**
     * Adds Multiple Printables to another Printable.
     *
     * @param base   the base Printable
     * @param offset the space between the Printables
     * @param tops   the Printables to add at the top
     * @return a Printable containing all the concatenated Printables
     */
    public static Printable addPrintablesToTop(Printable base, int offset, Printable... tops) {
        Printable builder = new PrintableScene(base.getPrintable());
        for (Printable top : tops) {
            for (int i = 0; i < offset; i++)
                builder = addStringToTop(builder, "");
            List<String> reversed = new ArrayList<>(top.getPrintable());
            Collections.reverse(reversed);
            for (String row : reversed) {
                builder = addStringToTop(builder, row);
            }
        }
        return builder;
    }

    /**
     * Adds Multiple Printables to another Printable.
     *
     * @param base the base Printable
     * @param tops the Printables to add at the top
     * @return a Printable containing all the concatenated Printables
     */
    public static Printable addPrintablesToTop(Printable base, Printable... tops) {
        return addPrintablesToTop(base, 0, tops);
    }

    /**
     * Adds a String to the top of a Printable.
     *
     * @param base  the base Printable
     * @param toAdd the String to add
     * @return a Printable containing the concatenated Printable
     */
    public static Printable addStringToTop(Printable base, String toAdd) {
        return addStringAtRow(base, toAdd, 0, 0);
    }

    /**
     * Adds a String to the top of a Printable.
     *
     * @param base   the base Printable
     * @param toAdd  the String to add
     * @param offset the space between the Printable and the String
     * @return a Printable containing the concatenated Printable
     */
    public static Printable addStringToTop(Printable base, String toAdd, int offset) {
        return addStringAtRow(base, toAdd, 0, offset);
    }

    /**
     * Adds a String to the bottom of a Printable.
     *
     * @param base  the base Printable
     * @param toAdd the String to add
     * @return a Printable containing the concatenated Printable
     */
    public static Printable addBottomString(Printable base, String toAdd) {
        return addStringAtRow(base, toAdd, base.getPrintable().size(), 0);
    }

    /**
     * Adds a String to the bottom of a Printable.
     *
     * @param base   the base Printable
     * @param toAdd  the String to add
     * @param offset the space between the Printable and the String
     * @return a Printable containing the concatenated Printable
     */
    public static Printable addBottomString(Printable base, String toAdd, int offset) {
        return addStringAtRow(base, toAdd, base.getPrintable().size(), offset);
    }

    /**
     * Adds a String at a specific row of a Printable.
     *
     * @param base      the base Printable
     * @param toAdd     the String to add
     * @param row       where to add the String
     * @param separator the space between the Printable and the String
     * @return a Printable containing the concatenated Printable
     */
    public static Printable addStringAtRow(Printable base, String toAdd, int row, int separator) {
        List<String> newPrintable = new ArrayList<>(base.getPrintable());
        newPrintable.add(row, base.fillWithEmptySpace(toAdd));
        return new PrintableScene(newPrintable);
    }

    /**
     * @return the Printable of this Scene
     */
    @Override
    public List<String> getPrintable() {
        return scene;
    }
}
