package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.gui.GUI;

/**
 * Run the ClientApp to run the Client Application
 */
public class ClientApp {
    public static final String CLI_ARGUMENT = "-cli";
    public static final String GUI_ARGUMENT = "-gui";
    public static final String DEFAULT_IP = "localhost";
    public static final int DEFAULT_PORT = 1337;

    /**
     * This is the main method to start the Client Application
     * -cli to start the Client in CLI
     * -gui to start the Client in GUI
     *
     * @param args arguments to start the App
     */
    public static void main(String[] args) {

        if (args[0].equals(CLI_ARGUMENT)) {
            new CLI();
        } else if (args[0].equals(GUI_ARGUMENT)) {
            new GUI().show();
        }
    }
}
