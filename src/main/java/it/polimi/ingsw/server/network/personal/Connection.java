package it.polimi.ingsw.server.network.personal;

import java.io.*;
import java.net.Socket;

public class Connection {
    private BufferedReader in;
    private PrintWriter out;
    private final Socket socket;

    public Connection(Socket socket) {
        this.socket = socket;
        startConnection();
    }

    private void startConnection(){
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        out.println(message);
    }

    public String getMessage(){
        String message;
        try {
            message = in.readLine();
        } catch (IOException e) {
            message = null;
        }
        return message;
    }

    public void close() {
        try {
            System.out.println("one player left");
            out.println("quitting...");
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}