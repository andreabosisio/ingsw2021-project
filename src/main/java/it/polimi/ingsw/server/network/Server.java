package it.polimi.ingsw.server.network;

import java.util.Scanner;

/**
 * singleton server class: when created starts a SocketServer(the real multithread server operating online)
 * this class is only in charge of responding to server terminal commands and being singleton guarantees
 * only one multiThread server will ever be running
 *
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

    public static synchronized Server getServer() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void start(){
        Scanner scanner = new Scanner(System.in);
        String input;
        while (true){
            System.out.println("SERVER READY\nTYPE LOBBY FOR ONLINE PLAYER-----QUIT TO CLOSE");
            input = scanner.nextLine().toLowerCase();
            switch (input){
                case "quit":
                    serverSocket.close();//close the multiThreadServer
                    //Lobby.getLobby().close();//close the lobby
                    System.out.println("SERVER CLOSED");
                    System.exit(0);//close the running main
                    break;
                case "lobby":
                    System.out.println(Lobby.getLobby().getOnlinePlayersNumber());
                    continue;
                default:
                    System.out.println("Invalid input");
            }
        }
    }

    /**
     * getter for server up status
     * @return server status
     */
    public boolean getStatus(){
        return serverUp;
    }
}
