package it.polimi.ingsw.server.network.personal;

import it.polimi.ingsw.server.network.PongObserver;

import java.io.*;
import java.net.Socket;

public class Connection {
    private BufferedReader in;
    private PrintWriter out;
    private final Socket socket;
    private PongObserver pongObserver;

    public Connection(Socket socket) {
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

    /*
      al momento fa notify e ritorna string uguale a pong che il clienthandler in game ignora
      tanto client handler in game è un loop continuo di lettura
    */
    public String getMessage() {
        String message;
        try {
            message = in.readLine();
            //start of PingPong code
            if (message.equals("pong")) {
                pongObserver.PongUpdate();
            }
        } catch (IOException e) {
            message = null;
        }
        return message;
    }

    public void close() {
        try {
            System.out.println("one player left");
            out.println("quitting...");
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearStack() {
        String str;
        while (true) {
            try {
                if (!in.ready()) break;
                str = in.readLine();
                //if ((str = in.readLine()) == null) break;
                System.out.println(str + " was ignored during synchronization");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPongObserver(PongObserver pongObserver) {
        this.pongObserver = pongObserver;
    }
}