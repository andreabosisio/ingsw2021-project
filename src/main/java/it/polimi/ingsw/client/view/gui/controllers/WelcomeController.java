package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.ClientApp;
import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

/**
 * This class is used as the controller for the fxml scene:welcomeScene.fxml
 */
public class WelcomeController extends GUICommandListener {

    private final GUI gui;

    private static final String DEFAULT_IP = ClientApp.DEFAULT_IP;
    private static final int DEFAULT_PORT = ClientApp.DEFAULT_PORT;
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final String IP_REGEXP = "^(" + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + ")$";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);
    private final static String LOCAL_GAME = "Local Game";
    private final static String ONLINE_GAME = "Online Game";
    ObservableList<String> connectionModes = FXCollections.observableArrayList(ONLINE_GAME, LOCAL_GAME);

    @FXML
    private Button start;

    @FXML
    private Button settings;

    @FXML
    private TextField serverIP;

    @FXML
    private TextField serverPort;
    @FXML
    private ComboBox<String> connectionSelector;

    /**
     * Used to create a controller to handle the welcome phase
     *
     * @param gui gui associated with the player
     */
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
        serverIP.setText(DEFAULT_IP);
        serverPort.setVisible(false);
        serverPort.setText(String.valueOf(DEFAULT_PORT));
        start.setOnMousePressed((event -> startAction()));
        settings.setOnMousePressed((event -> showSettings()));
        connectionSelector.setVisible(false);
        connectionSelector.setItems(connectionModes);
        connectionSelector.setValue(connectionModes.get(0));
    }

    /**
     * This method is called when the player presses the start button
     * It tries to establish a connection with the selected serverIP and serverPort
     * In case of a failure it sets the serverIP and serverPort to the default values
     */
    private void startAction() {
        if (connectionSelector.getValue().equals(LOCAL_GAME)) {
            gui.setGUI();
            return;
        }
        String ip;
        int port;
        ip = serverIP.getText();
        try {
            port = Integer.parseInt(serverPort.getText());
        } catch (NumberFormatException e) {
            printErrorMessage("Port must be a number");
            serverPort.setText(String.valueOf(DEFAULT_PORT));
            return;
        }
        // Wrong IP or Port
        try {
            gui.setGUI(ip, port);
            //gui.setGui();
        } catch (Exception e) {
            printErrorMessage("Could not reach the Server");
            serverIP.setText(DEFAULT_IP);
            serverPort.setText(String.valueOf(DEFAULT_PORT));
        }
    }

    /**
     * This method is called when the player presses the settings button
     * It sets the serverIP and serverPort textField as visible
     */
    private void showSettings() {
        serverIP.setVisible(true);
        serverPort.setVisible(true);
        connectionSelector.setVisible(true);
    }
}
