package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.gui.GUI;

/**
 * Run the ClientApp to run the Client Application
 */
public class ClientApp {
    private static final String CLI_ARGUMENT = "-cli";
    private static final String GUI_ARGUMENT = "-gui";
    //private static final String DEFAULT_IP = "8.tcp.ngrok.io";
    //private static final int DEFAULT_PORT = 11605;
    private static final String DEFAULT_IP = "localhost";
    private static final int DEFAULT_PORT = 1337;

    /**
     * This is the main method to start the Client Application
     * -cli to start the Client in CLI
     * -gui to start the Client in GUI
     *
     * @param args arguments to start the App
     */
    public static void main(String[] args) {

        if (args[0].equals(CLI_ARGUMENT)) {
            CLI cli = new CLI();
        } else if (args[0].equals(GUI_ARGUMENT)) {
            GUI gui = new GUI();
            gui.show();
            //new Thread(gui::show).start();
            //gui.setGUI(ip, port);
        }

        /*
        if(args.length == 2)
            view = new CLI(ip, port);
         */
    }

    /**
     * Get method that return the value of the Default IP
     *
     * @return the Default IP
     */
    public static String getDefaultIP() {
        return DEFAULT_IP;
    }

    /**
     * Get method that return the value of the Default Port
     *
     * @return the Default Port
     */
    public static int getDefaultPort() {
        return DEFAULT_PORT;
    }
}
