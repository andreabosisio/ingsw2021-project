package it.polimi.ingsw.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CLI {
    public void startCLI(){
        Socket clientSocket = null;
        try {
            clientSocket = new Socket("127.0.0.1",1337);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Scanner inFromTerminal = new Scanner(System.in);
            while (true){
                String command = inFromTerminal.nextLine();
                if(command.equals("quitc"))break;
                out.println(command);
                String answer = in.readLine();
                System.out.println(answer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
