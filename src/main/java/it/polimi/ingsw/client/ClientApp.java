package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.gui.GUI;

public class ClientApp {
    private static final String CLI_ARGUMENT = "-cli";
    private static final String DEFAULT_IP = "localhost";
    private static final int DEFAULT_PORT = 1337;


    public static void main(String[] args){

        View view;
        //todo add modify port and ip by args
        if (args.length == 0)
            view = new CLI(DEFAULT_IP, DEFAULT_PORT);
    }
}