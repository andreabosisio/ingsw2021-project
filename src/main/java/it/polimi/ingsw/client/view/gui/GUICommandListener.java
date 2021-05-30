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
        messageBox.setStyle("-fx-text-inner-color: black");
        //System.out.println("info: "+ info);
        messageBox.setText(info);
    }

    public void printErrorMessage(String error) {
        messageBox.setStyle("-fx-text-inner-color: red");
        //System.out.println("error: "+ error);
        messageBox.setText(error);
    }

    public CommandListenerObserver getCommandListenerObserver() {
        return commandListenerObserver;
    }

    protected void setNetworkNick(String nickname){
        commandListenerObserver.setNickname(nickname);
    }
}
