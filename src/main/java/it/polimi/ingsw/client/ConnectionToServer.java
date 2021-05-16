package it.polimi.ingsw.client;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionToServer {
    private final static String PONG_MESSAGE = "pong";
    private final static String QUIT_TYPE = "quit";
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
            System.out.println("Failed to start connection with server");
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
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type",QUIT_TYPE);
            sendMessage(jsonObject.toString());
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.out.println("failed to close socket");
        }
    }

    public void sendPong(){
        out.println(PONG_MESSAGE);
    }
}
