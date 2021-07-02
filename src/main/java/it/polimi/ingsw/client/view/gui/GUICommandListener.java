package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.events.send.EventToServer;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * This class listens to inputs from the GUI
 */
public abstract class GUICommandListener implements CommandListener {
    private CommandListenerObserver commandListenerObserver;
    @FXML
    private TextArea messageBox;
    private int textCounter = 0;
    private final int MAX_NUMBER_OF_MESSAGES = 10;

    @Override
    public void notifyObservers(EventToServer eventToServer) {
        commandListenerObserver.update(eventToServer);
    }

    @Override
    public void registerObservers(CommandListenerObserver commandListenerObserver) {
        this.commandListenerObserver = commandListenerObserver;
    }

    /**
     * Print an info message in the GUI
     *
     * @param info string containing the message
     */
    public void printInfoMessage(String info) {
        textCounter++;
        //messageBox.setStyle("-fx-text-inner-color: black");
        if (textCounter > MAX_NUMBER_OF_MESSAGES) {
            messageBox.setText(info + "\n");
            textCounter = 0;
        } else
            messageBox.appendText(info + "\n");
    }

    /**
     * Print an error message in the GUI
     *
     * @param error string containing the message
     */
    public void printErrorMessage(String error) {
        textCounter++;
        //messageBox.setStyle("-fx-text-inner-color: red");
        if (textCounter > MAX_NUMBER_OF_MESSAGES) {
            messageBox.setText(error + "\n");
            textCounter = 0;
        } else
            messageBox.appendText(error + "\n");
    }

    /**
     * Return the observer interested to the player actions saved as observer of this class
     *
     * @return the saved observer
     */
    public CommandListenerObserver getCommandListenerObserver() {
        return commandListenerObserver;
    }

    /**
     * Set nickname of the owner of this GUI as the nickname saved in the network
     *
     * @param nickname nickname of the player
     */
    protected void setNetworkNick(String nickname) {
        commandListenerObserver.setNickname(nickname);
    }
}
