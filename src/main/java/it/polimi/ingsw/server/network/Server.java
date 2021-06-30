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
    private static final int socketPort = 1337;//port that server will be listening on
    private static ServerSocketManager serverSocket;//manual redefinition of ServerSocket (multiThread server)

    private Server() {
        serverUp = true;
        serverSocket = new ServerSocketManager(socketPort);//create the multiTreadServer
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
     * This method is used to start the Server input listener
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        String input;
        while (serverUp) {
            System.out.println("SERVER READY\nType LOBBY for lobby size, QUIT to close or CHEAT to cheat");
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
                    System.out.println("Added 6 resources to each player");
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
