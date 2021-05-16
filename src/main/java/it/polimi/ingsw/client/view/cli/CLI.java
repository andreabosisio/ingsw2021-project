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

    public void clearView() {
        for (int i = 0; i < 50; i++)
            System.out.println(" ");
    }

    @Override
    public void graphicUpdate() {

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