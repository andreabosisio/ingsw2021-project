package it.polimi.ingsw.server.network;

import java.util.Scanner;

/**
 * singleton server class: when created starts a SocketServer(the real multiThread server operating online)
 * this class is only in charge of responding to server terminal commands and being singleton guarantees
 * only one multiThread server will ever be running
 */
public class Server {
    private static boolean serverUp;
    private static Server instance = null;
    private static ServerSocketManager serverSocket;//manual redefinition of ServerSocket (multiThread server)

    /**
     * Create the only existing instance of this Object.
     */
    private Server() {
    }

    /**
     * This method is used to get the only instance of the Server or create a new one if it does not exist
     *
     * @return instance of the Server
     */
    public static synchronized Server getServer() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    /**
     * This method is used to start the Server input listener.
     *
     * @param socketPort Port that server will be listening on
     */
    public void start(int socketPort) {

        serverUp = true;
        serverSocket = new ServerSocketManager(socketPort); //create the multiTreadServer

        Scanner scanner = new Scanner(System.in);
        String input;
        while (serverUp) {
            System.out.println("SERVER READY\nType LOBBY for lobby size or QUIT to close.");
            input = scanner.nextLine().toLowerCase();
            switch (input) {
                case "quit":
                    serverSocket.close();//close the multiThreadServer
                    System.out.println("SERVER CLOSED");
                    System.exit(0);//close the running main
                case "lobby":
                    System.out.println(Lobby.getLobby().getOnlinePlayersNumber());
                    continue;
                case "cheat":
                    Lobby.getLobby().cheat();
                    continue;
                default:
                    System.out.println("Invalid input");
            }
        }
    }

    /**
     * getter for server up status
     *
     * @return server status
     */
    public boolean getStatus() {
        return serverUp;
    }
}
