package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientApp;
import it.polimi.ingsw.server.ServerApp;

/**
 * Run the App to launch the Server and the Client Application
 */
public class App {

    public static final String SERVER_ARG = "-server";

    /**
     * This is the main method to start the app
     * -server to start in server mode
     * -cli to start in client mode with CLI
     * -gui to start in client mode with GUI
     *
     * @param args args to start app(server/cli/gui)
     */
    public static void main(String[] args) {
        if(args.length != 0) {
            if (args[0].equals(SERVER_ARG))
                ServerApp.main(args);
            else
                ClientApp.main(args);
        } else {
            System.err.println("Missing arguments. Please recompile adding " + SERVER_ARG + " or " + ClientApp.GUI_ARGUMENT +" or " +  ClientApp.CLI_ARGUMENT);
        }
    }
}

