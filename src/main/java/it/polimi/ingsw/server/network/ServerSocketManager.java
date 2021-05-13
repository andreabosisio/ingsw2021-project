package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.network.personal.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketManager extends Thread {
    private ServerSocket serverSocket;//java ServerSocket class

    public ServerSocketManager(int socketPort) {
        try{
            this.serverSocket = new ServerSocket(socketPort);
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("SocketServer waiting for client on port "+ serverSocket.getLocalPort());
        start();//thread inherited method
    }


    @Override
    public void run() {
        ExecutorService executor = Executors.newCachedThreadPool();
        while(Server.getServer().getStatus()){
            try{
                Socket socket = serverSocket.accept();//server accept request to connect from a client
                executor.submit(new ClientHandler(socket));//a thread for handling this specific client is setup
            }catch (IOException e){
                e.printStackTrace();
                break;
            }
        }
        //todo check how to close a server
        executor.shutdown();
    }

    /**
     * close the multiThreadServer
     */
    public void close(){
        System.exit(0);
    }
}

