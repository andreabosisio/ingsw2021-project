package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.network.personal.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class is the Manager of the Communication with the Clients, in fact for every Clients that connects
 * it creates a new Thread and a new Socket to communicate with them in parallel.
 */
public class ServerSocketManager extends Thread {
    private ServerSocket serverSocket;//java ServerSocket class
    private ExecutorService executor;

    public ServerSocketManager(int socketPort) {
        try {
            this.serverSocket = new ServerSocket(socketPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("SocketServer waiting for client on port " + serverSocket.getLocalPort());
        start();//thread inherited method
    }


    /**
     * This method is used to start the Server manager
     * The manager will be listening on one port for new client connections
     * For every new client a dedicated Thread will be created and a clientHandler will be instantiated for him
     */
    @Override
    public void run() {
        executor = Executors.newCachedThreadPool();
        while (Server.getServer().getStatus()) {
            try {
                Socket socket = serverSocket.accept();//server accept request to connect from a client
                executor.submit(new ClientHandler(socket));//a thread for handling this specific client is setup
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    /**
     * close the multiThreadServer
     */
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}

