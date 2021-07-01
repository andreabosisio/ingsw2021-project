package it.polimi.ingsw.client.view.cli;

import java.util.List;

/**
 * All the classes that should be printed on the CLI are sub-classes of this class.
 */
public abstract class Printable {

    private int width = -1;

    /**
     * Set the width of this Printable.
     *
     * @param prePrintable the Printable before being returned
     */
    public void setWidth(List<String> prePrintable) {
        this.width = prePrintable.stream().map(AnsiUtilities::getStringLengthWithoutANSI).max(Integer::compareTo).orElse(-1);
    }

    /**
     * @return the width of this Printable
     */
    public int getWidth() {
        if (this.width == -1)
            getPrintable();
        return this.width;
    }

    /**
     * @return a String containing this.width space characters
     */
    public String getEmptySpace() {
        StringBuilder emptySpace = new StringBuilder();
        for (int i = 0; i < getWidth(); i++)
            emptySpace.append(' ');
        return emptySpace.toString();
    }

    /**
     * All the Printable classes should override this method in order to return the right Printable.
     *
     * @return null
     */
    public List<String> getPrintable() {
        return null;
    }

    /**
     * Fills the String with space characters until it reaches this.width.
     *
     * @param toFill The String to fill with space characters
     * @return the String filled with space characters
     */
    public String fillWithEmptySpace(String toFill) {
        int toFillLength = AnsiUtilities.getStringLengthWithoutANSI(toFill);
        if (toFillLength < this.width) {
            StringBuilder filler = new StringBuilder(toFill);
            for (int i = 0; i < (this.width - toFillLength); i++)
                filler.append(' ');
            return filler.toString();
        }
        return toFill;
    }
}
