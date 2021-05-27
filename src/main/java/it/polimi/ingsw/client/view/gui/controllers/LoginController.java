package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.LoginEvent;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;


public class LoginController extends GUICommandListener {
    @FXML
    private TextField nickname;

    @FXML
    private PasswordField password;

    @FXML
    private ImageView background;

    @FXML
    private Button login;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    public void initialize() {
        login.setOnMousePressed((event -> loginAction()));
    }

    private void loginAction() {
        setNetworkNick(nickname.getText());
        notifyObservers(new LoginEvent(nickname.getText(), password.getText()));
    }

    //fixme
    public void activateProgressIndicator() {
        this.progressIndicator.setVisible(true);
    }

}
