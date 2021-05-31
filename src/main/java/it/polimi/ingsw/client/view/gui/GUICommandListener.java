package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.events.send.SendEvent;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;
import it.polimi.ingsw.client.view.cli.AnsiEnum;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * This class listens to inputs from the GUI
 */
public abstract class GUICommandListener implements CommandListener {
    private CommandListenerObserver commandListenerObserver;
    @FXML
    private TextArea messageBox;
    private int textCounter = 0;

    @Override
    public void notifyObservers(SendEvent sendEvent) {
        commandListenerObserver.update(sendEvent);
    }

    @Override
    public void registerObservers(CommandListenerObserver commandListenerObserver) {
        this.commandListenerObserver = commandListenerObserver;
    }

    //todo better messageBox
    public void printInfoMessage(String info) {
        textCounter++;
        messageBox.setStyle("-fx-text-inner-color: black");
        //System.out.println("info: "+ info);
        if (textCounter > 5) {
            messageBox.setText(info + "\n");
            textCounter = 0;
        } else
            messageBox.appendText(info + "\n");
    }

    public void printErrorMessage(String error) {
        textCounter++;
        messageBox.setStyle("-fx-text-inner-color: red");
        //System.out.println("error: "+ error);
        if (textCounter > 5) {
            messageBox.setText(error + "\n");
            textCounter = 0;
        } else
            messageBox.appendText(error + "\n");
    }

    public CommandListenerObserver getCommandListenerObserver() {
        return commandListenerObserver;
    }

    protected void setNetworkNick(String nickname) {
        commandListenerObserver.setNickname(nickname);
    }
}
