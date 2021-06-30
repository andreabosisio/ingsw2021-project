package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientApp;

import it.polimi.ingsw.server.ServerApp;

public class App {

    /**
     * This is the main method to start the app
     * -server to start in server mode
     * -cli to start in client mode with CLI
     * -gui to start in client mode with GUI
     *
     * @param args args to start app(server/cli/gui)
     */
    public static void main(String[] args) {
        if (args[0].equals("-server"))
            ServerApp.main(args);
        else
            ClientApp.main(args);
    }
}

