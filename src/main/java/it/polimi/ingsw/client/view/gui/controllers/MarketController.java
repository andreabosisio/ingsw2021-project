package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.MarketActionEvent;
import it.polimi.ingsw.client.events.send.SendEvent;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

public class MarketController extends GUICommandListener {

    @FXML
    private Button arrow_0;

    @FXML
    public void marketAction(MouseEvent arrow) {
        System.out.println(arrow.getPickResult().getIntersectedNode().getId());
        notifyObservers(new MarketActionEvent(Integer.parseInt(arrow_0.getId().split("_")[1])));
    }

}
