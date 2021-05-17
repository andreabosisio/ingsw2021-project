package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.NetworkHandler;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class CLI implements View {

    private NetworkHandler networkHandler;
    private final CLICommandListener cliCommandListener;

    protected static final int MARKET_X_SHIFT = 70;

    public CLI(String ip, int port) {
        try {
            this.networkHandler = new NetworkHandler(ip, port, this);
            Board.getBoard();
        } catch (IOException e) {
            System.out.println("Could not connect to the server");
            System.exit(0);
        }
        cliCommandListener = new CLICommandListener();
        start();
    }

    @Override
    public void start() {
        cliCommandListener.registerObservers(networkHandler);
        networkHandler.startNetwork();
    }

    public static void clearView() {
        for (int i = 0; i < 20; i++)
            System.out.println(" ");
    }

    public static String getHorizontalShift(int shift) {
        StringBuilder toReturn = new StringBuilder();
        for (int i = 0; i < shift; i++)
            toReturn.append(" ");
        return toReturn.toString();
    }

    @Override
    public void graphicUpdate() {
    }

    @Override
    public void printInfoMessage(String info) {
        if(info == null)
            return;
        System.out.println(info);
    }

    @Override
    public void printErrorMessage(String error) {
        if(error == null)
            return;
        System.out.println(AsciiArts.RED + error + AsciiArts.RESET);
    }

    @Override
    public void setOnLogin() {

        clearView();
        System.out.println(AsciiArts.LOGO.getAsciiArt());

        showLoginScene();
        cliCommandListener.askCredentials();

        clearView();
    }

    @Override
    public void setOnChooseNumberOfPlayers(String payload) {
        cliCommandListener.askNumberOfPlayers(payload);
    }

    private void showLoginScene() {
        System.out.println(AsciiArts.LOGIN_SMALL.getAsciiArt());
    }

    @Override
    public void setOnMatchMaking() {

        clearView();

        System.out.println(AsciiArts.LOGO.getAsciiArt());

        System.out.print("Matchmaking...");

        /*
        System.out.print("Matchmaking");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
    }

    @Override
    public void setOnSetup(List<String> leaderCardsID, int numberOfResource) {

        clearView();

        cliCommandListener.askSetupChoice(leaderCardsID, numberOfResource);

    }


    @Override
    public void setOnGame() {

    }
}
