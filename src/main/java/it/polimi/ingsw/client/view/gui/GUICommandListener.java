package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.events.send.SendEvent;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;

/**
 * This class listens to inputs from the GUI
 */
public class GUICommandListener implements CommandListener {
    CommandListenerObserver commandListenerObserver;

    @Override
    public void notifyObservers(SendEvent sendEvent) {
        commandListenerObserver.update(sendEvent);
    }

    @Override
    public void registerObservers(CommandListenerObserver commandListenerObserver) {
        this.commandListenerObserver = commandListenerObserver;
    }
}
