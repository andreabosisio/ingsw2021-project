package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

/**
 * This class is used as the controller for the fxml scene:welcomeScene.fxml
 */
public class WelcomeController extends GUICommandListener {

    private GUI gui;

    private static final String defaultIP = "127.0.0.1";
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final String IP_REGEXP = "^(" + zeroTo255 + "\\." + zeroTo255 + "\\."
            + zeroTo255 + "\\." + zeroTo255 + ")$";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);
    private static final int defaultPort = 1337;
    //private static final int defaultPort = 19721;
    //private static final String defaultIP = "8.tcp.ngrok.io";

    @FXML
    private Button start;

    @FXML
    private Button settings;

    @FXML
    private TextField serverIP;

    @FXML
    private TextField serverPort;

    public WelcomeController(GUI gui) {
        this.gui = gui;
    }

    /**
     * Function used to initialize the fxml when loaded
     * It sets the serverIP and serverPort textFields as not visible and as the default values
     * It also sets showSettings() as the mousePressed action for the button settings
     * It also sets startAction() as the mousePressed action for the button start
     */
    @FXML
    public void initialize() {
        serverIP.setVisible(false);
        serverIP.setText(defaultIP);
        serverPort.setVisible(false);
        serverPort.setText(String.valueOf(defaultPort));
        start.setOnMousePressed((event -> startAction()));
        settings.setOnMousePressed((event -> showSettings()));
    }

    /**
     * This method is called when the player presses the start button
     * It tries to establish a connection with the selected serverIP and serverPort
     * In case of a failure it sets the serverIP and serverPort to the default values
     */
    private void startAction() {
        String ip;
        int port;
        /*
        if (!IP_PATTERN.matcher(serverIP.getText()).matches()) {
            printErrorMessage("Not an IPv4");
            serverIP.setText(defaultIP);
            return;
        }

         */
        ip = serverIP.getText();
        try {
            port = Integer.parseInt(serverPort.getText());
        } catch (NumberFormatException e) {
            printErrorMessage("Port must be a number");
            serverPort.setText(String.valueOf(defaultPort));
            return;
        }
        // Wrong IP or Port
        try {
            gui.setGUI(ip, port);
        } catch (Exception e) {
            printErrorMessage("Could not reach the Server");
            serverIP.setText(defaultIP);
            serverPort.setText(String.valueOf(defaultPort));
        }
    }

    /**
     * This method is called when the player presses the settings button
     * It sets the serverIP and serverPort textField as visible
     */
    private void showSettings() {
        serverIP.setVisible(true);
        serverPort.setVisible(true);
    }
}
