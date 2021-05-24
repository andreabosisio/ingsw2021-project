package it.polimi.ingsw.client;

import com.google.gson.*;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.gui.GUI;

public class ClientApp {
    private static final String CLI_ARGUMENT = "-cli";
    private static final String DEFAULT_IP = "0.tcp.ngrok.io";
    private static final int DEFAULT_PORT = 17144;
    //private static final String DEFAULT_IP = "localhost";
    //private static final int DEFAULT_PORT = 1337;


    public static void main(String[] args){

        View view;
        String ip = DEFAULT_IP;
        int port = DEFAULT_PORT;

        //todo add modify port and ip by args
        view = new CLI(ip, port);
        /*
        if(args.length == 2)
            view = new CLI(ip, port);

         */
    }
}