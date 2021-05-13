package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.NetworkHandler;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class CLI implements View {

    private NetworkHandler networkHandler;
    private CLICommandListener cliCommandListener;

    public CLI(String ip, int port) {
        try {
            this.networkHandler = new NetworkHandler(ip, port, this);
        } catch (IOException e) {
            System.exit(0);
        }
        cliCommandListener = new CLICommandListener();
        start();
    }

    @Override
    public void start() {
        cliCommandListener.registerObservers(networkHandler);

        networkHandler.run();
    }

    @Override
    public void printInfoMessage(String info) {
        System.out.println(info);
    }

    @Override
    public void printErrorMessage(String error) {
        System.err.println(error);
    }

    @Override
    public void setOnLogin() {
        showLoginScene();
        cliCommandListener.askCredentials();
    }

    @Override
    public void setOnChooseNumberOfPlayers(String payload) {
        cliCommandListener.askNumberOfPlayers(payload);
    }

    private void showLoginScene() {
        System.out.println("\n" +
                "██╗░░░░░░█████╗░░██████╗░██╗███╗░░██╗\n" +
                "██║░░░░░██╔══██╗██╔════╝░██║████╗░██║\n" +
                "██║░░░░░██║░░██║██║░░██╗░██║██╔██╗██║\n" +
                "██║░░░░░██║░░██║██║░░╚██╗██║██║╚████║\n" +
                "███████╗╚█████╔╝╚██████╔╝██║██║░╚███║\n" +
                "╚══════╝░╚════╝░░╚═════╝░╚═╝╚═╝░░╚══╝");
    }

    @Override
    public void setOnMatchMaking() {

        System.out.println("Matchmaking...");
        /*
        String a = "|/-\\";
        System.out.print("\033[2J");   // hide the cursor
        long start = System.currentTimeMillis();
        while (true) {
            for (int i = 0; i < 4; i++) {
                System.out.print("\033[2J");     // clear terminal
                System.out.print("\033[0;0H");   // place cursor at top left corner
                for (int j = 0; j < 80; j++) {   // 80 character terminal width, say
                    System.out.print(a.charAt(i));
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long now = System.currentTimeMillis();
            // stop after 20 seconds, say
            if (now - start >= 20000) break;
        }
        System.out.print("\033[?25h"); // restore the cursor

         */

    }

    @Override
    public void setOnSetup(List<String> leaderCardsID, int numberOfResource) {

    }


    @Override
    public void setOnGame() {

    }
}
