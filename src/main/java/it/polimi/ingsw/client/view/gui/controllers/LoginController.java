package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.LoginEvent;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;


/**
 * This class is used as the controller for the fxml scene:loginScene.fxml
 */
public class LoginController extends GUICommandListener {
    @FXML
    private TextField nickname;

    @FXML
    private PasswordField password;

    @FXML
    private ImageView background;

    @FXML
    private Button login;

    /**
     * Function used to initialize the fxml when loaded
     * It sets loginAction() on a onMousePressed for the login button
     */
    @FXML
    public void initialize() {
        login.setOnMousePressed((event -> loginAction()));
    }

    /**
     * This method is called when the player presses the login button
     * It construct an event to sent to the server containing the player nickname and password
     */
    private void loginAction() {
        setNetworkNick(nickname.getText());
        notifyObservers(new LoginEvent(nickname.getText(), password.getText()));
    }

}
