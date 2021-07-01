package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

/**
 * Represent and Error message
 */
public class ErrorMessageEvent implements EventFromServer {
    private final String payload;

    /**
     * Create a new Error Message by specifying the error.
     *
     * @param error String containing the error
     */
    public ErrorMessageEvent(String error) {
        this.payload = error;
    }

    @Override
    public void updateView(View view) {
        view.printErrorMessage(payload);
    }
}
