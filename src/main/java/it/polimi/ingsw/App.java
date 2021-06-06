package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientApp;

import it.polimi.ingsw.server.ServerApp;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        if (args[0].equals("-server"))
            ServerApp.main(args);
        else
            ClientApp.main(args);
    }
}

