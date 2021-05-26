package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class WelcomeController extends GUICommandListener {

    private GUI gui;
    private static final String defaultIP = "127.0.0.1";
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final String IP_REGEXP = "^(" + zeroTo255 + "\\." + zeroTo255 + "\\."
            + zeroTo255 + "\\." + zeroTo255 + ")$";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);
    private static final int defaultPort = 1337;
    @FXML
    private Button start;
    @FXML
    private Button settings;
    @FXML
    private TextField serverIP;
    @FXML
    private TextField serverPort;
    @FXML
    public void initialize() {
        serverIP.setVisible(false);
        serverIP.setText(defaultIP);
        serverPort.setVisible(false);
        serverPort.setText(String.valueOf(defaultPort));
        start.setOnMousePressed((event -> startAction()));
        settings.setOnMousePressed((event -> showSettings() ));
    }

    public WelcomeController(GUI gui) {
        this.gui = gui;
    }

    private void startAction() {
        String ip;
        int port;
        if(!IP_PATTERN.matcher(serverIP.getText()).matches()) {
            serverIP.setText(defaultIP);
            return;
        }
        ip = serverIP.getText();
        try{
            port = Integer.parseInt(serverPort.getText());
        }catch (NumberFormatException e){
            serverPort.setText(String.valueOf(defaultPort));
            return;
        }
        gui.setGUI(ip,port);
    }
    private void showSettings(){
        serverIP.setVisible(true);
        serverPort.setVisible(true);
    }
}
