package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.LoginEvent;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
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
    public void initialize() {
        //File file = new File("src/main/resources/images/loginImage.jpg");
        //background.setImage(new Image(file.toURI().toString()));
        login.setOnMousePressed((event -> loginAction()));
    }

    private void loginAction() {
        notifyObservers(new LoginEvent(nickname.getText(), password.getText()));
    }
}
