package it.polimi.ingsw.server;

import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.server.network.Server;

/**
 * Server Application.
 */
public class ServerApp {

    private static final int DEFAULT_PORT = 1337; //port that server will be listening on

    /**
     * Starts the Server application
     *
     * @param args Server properties arguments
     */
    public static void main(String[] args) {
        //get only existing server(singleton) and start it
        Server server = Server.getServer();
        if(args.length > 1)
            server.start(Integer.parseInt(args[1]));
        else
            server.start(DEFAULT_PORT);
    }
}
