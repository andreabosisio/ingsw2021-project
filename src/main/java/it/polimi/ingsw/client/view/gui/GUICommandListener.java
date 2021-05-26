package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.events.send.SendEvent;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;
import it.polimi.ingsw.client.view.cli.AnsiEnum;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * This class listens to inputs from the GUI
 */
public abstract class GUICommandListener implements CommandListener {
    private CommandListenerObserver commandListenerObserver;
    @FXML
    private TextField messageBox;

    @Override
    public void notifyObservers(SendEvent sendEvent) {
        commandListenerObserver.update(sendEvent);
    }

    @Override
    public void registerObservers(CommandListenerObserver commandListenerObserver) {
        this.commandListenerObserver = commandListenerObserver;
    }

    public void printInfoMessage(String info) {
        messageBox.setStyle("-fx-text-inner-color: black");
        messageBox.setText(info);
    }

    public void printErrorMessage(String error) {
        messageBox.setStyle("-fx-text-inner-color: red");
        messageBox.setText(error);
    }
}
