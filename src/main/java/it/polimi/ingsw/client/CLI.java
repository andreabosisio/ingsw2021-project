package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
                if(command.equals("quitc")){
                    break;
                }
                else if(command.equals("login")){
                    List<String> login = new ArrayList<>();
                    String nickname = inFromTerminal.nextLine();
                    String password = inFromTerminal.nextLine();
                    login.add(nickname);
                    login.add(password);
                    Gson gson = new Gson();
                    command = gson.toJson(login);
                }
                out.println(command);
                String answer = in.readLine();
                System.out.println(answer);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
