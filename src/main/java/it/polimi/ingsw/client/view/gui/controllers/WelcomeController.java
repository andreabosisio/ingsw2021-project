package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.io.File;

public class WelcomeController extends GUICommandListener {

    private GUI gui;

    public WelcomeController(GUI gui) {
        this.gui = gui;
    }

    @FXML
    private Button start;

    @FXML
    public void initialize() {
        start.setOnMousePressed((event -> startAction()));
    }

    private void startAction() {
        gui.setGUI("localhost", 1337);
    }
}
