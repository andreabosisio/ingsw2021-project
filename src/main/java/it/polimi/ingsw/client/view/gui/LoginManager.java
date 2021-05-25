package it.polimi.ingsw.client.view.gui;

import javafx.fxml.FXML;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.Objects;


public class LoginManager {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private ImageView background;

    @FXML
    public void initialize(){
        File file = new File("src/main/resources/loginImage.jpg");
        background.setImage(new Image(file.toURI().toString()));
    }


    @FXML
    public void loginAction(MouseEvent event) {
        String usernameString = username.getText();
        String passwordString = password.getText();
        System.out.println(usernameString + " and " + passwordString);
    }
}
