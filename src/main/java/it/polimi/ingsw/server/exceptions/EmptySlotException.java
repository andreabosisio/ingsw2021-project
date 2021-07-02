package it.polimi.ingsw.server.exceptions;

/**
 * Threw if a Resource Slot with no Resource in it is accessed.
 */
public class EmptySlotException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public EmptySlotException(String message) {
        super(message);
    }
}
