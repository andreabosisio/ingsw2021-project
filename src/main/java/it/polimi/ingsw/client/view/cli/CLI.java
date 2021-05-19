package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.NetworkHandler;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.view.View;
import java.io.IOException;
import java.util.List;

public class CLI implements View {

    private String nickname;
    private NetworkHandler networkHandler;
    private final CLICommandListener cliCommandListener;

    public CLI(String ip, int port) {
        try {
            this.networkHandler = new NetworkHandler(ip, port, this);
            Board.getBoard();
        } catch (IOException e) {
            System.out.println("Could not connect to the server");
            System.exit(0);
        }
        cliCommandListener = new CLICommandListener();
        startView();
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void startView() {
        cliCommandListener.registerObservers(networkHandler);
        networkHandler.startNetwork();
    }

    public static void clearView() {
        for (int i = 0; i < 20; i++)
            System.out.println(" ");
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
    public void setOnYourTurn() {
        //fixme clearView and print personalBoard
        System.out.println("what do you wish to do?(market,buy,production,leader,see)");
        String answer = cliCommandListener.askFirstAction();
        switch (answer){
            //fixme clear view and print necessary items for everySwitch
            case "market":{
                cliCommandListener.askMarketAction();
                break;
            }
            //todo complete code for actions below
            case "buy":
                System.out.println("you choose buy");
                break;
            case "production":
                System.out.println("you choose production");
                break;
            case "leader":
                System.out.println("you choose leader");
                break;
            case "see":
                setOnSeeChoice();
        }
    }
    private void setOnSeeChoice(){
        System.out.println("What do you wish to see?");
        //String answer = cliCommandListener.askSeeChoice();
        //switch
        setOnYourTurn();
    }

    @Override
    public void setOnNotYourTurn(String currentPlayer) {
        System.out.println("It's " +currentPlayer+" turn, wait for him to finish");
    }

    @Override
    public void setOnPlaceDevCard(String newCardID) {
        //todo clearView and print card to place and personal production board
        //Board.getBoard().printPlaceCardScene(newCardId)
        cliCommandListener.askCardPlacement();
    }

    @Override
    public void setOnTransformation(int numberOfTransformation,List<String> possibleTransformations) {
        clearView();
        cliCommandListener.askResourceTransformation(numberOfTransformation,possibleTransformations);
    }

}
