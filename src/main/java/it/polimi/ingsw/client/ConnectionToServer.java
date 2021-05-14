package it.polimi.ingsw.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionToServer {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    public ConnectionToServer(Socket socket) {
        this.socket = socket;
        startConnection();
    }

    private void startConnection() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getMessage() {
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
            //send quit json to server
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPong(){
        out.println("pong");
    }

}
