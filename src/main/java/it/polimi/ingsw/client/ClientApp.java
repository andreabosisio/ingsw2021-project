package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.gui.GUI;

public class ClientApp {
    private static final String CLI_ARGUMENT = "-cli";
    //private static final String DEFAULT_IP = "8.tcp.ngrok.io";
    //private static final int DEFAULT_PORT = 19721;
    private static final String DEFAULT_IP = "localhost";
    private static final int DEFAULT_PORT = 1337;


    public static String getIP(){
        return DEFAULT_IP;
    }
    public static int getPort(){
        return DEFAULT_PORT;
    }



    public static void main(String[] args) {

        View view;
        String ip = DEFAULT_IP;
        int port = DEFAULT_PORT;

        if(args[0].equals("-cli")) {
            view = new CLI(ip, port);
        }
        else {
            GUI gui = new GUI();
            gui.show();
            //new Thread(gui::show).start();
            //gui.setGUI(ip, port);
        }

        /*
        if(args.length == 2)
            view = new CLI(ip, port);

         */
    }
}
