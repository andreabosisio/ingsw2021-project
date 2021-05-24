package it.polimi.ingsw.client.view.cli;

import com.google.gson.*;
import it.polimi.ingsw.client.NetworkHandler;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CLI implements View {


    private String nickname;
    private NetworkHandler networkHandler;
    private final CLICommandListener cliCommandListener;

    public CLI(String ip, int port) {
        try {
            this.networkHandler = new NetworkHandler(ip, port, this);
            Board.getBoard();
        } catch (IOException e) {
            renderError("Could not connect to the server");
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
        render(AnsiEnum.LOGO.getAsciiArt());
        cliCommandListener.registerObservers(networkHandler);
        networkHandler.startNetwork();
    }

    public static void clearView() {
        for (int i = 0; i < 50; i++)
            System.out.println();
    }

    public static void render(Printable printable) {
        if (printable != null) {
            List<String> toPrint = printable.getPrintable();
            if (toPrint != null)
                toPrint.forEach(System.out::println);
        }
    }

    public static void renderError(String printable) {
        System.out.println(AnsiEnum.RED + printable + AnsiEnum.RESET);
    }

    public static void render(String printable) {
        System.out.println(printable);
    }

    @Override
    public void graphicUpdate() {

    }

    @Override
    public void printInfoMessage(String info) {
        if (info == null)
            return;
        render(info);
    }

    @Override
    public void printErrorMessage(String error) {
        if (error == null)
            return;
        render(AnsiEnum.RED + error + AnsiEnum.RESET);
        CLI.showThreePointsAnimation();
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
        render(AnsiEnum.LOGIN_SMALL.getAsciiArt());
    }

    @Override
    public void setOnMatchMaking() {
        clearView();
        render(AnsiEnum.LOGO.getAsciiArt());

        System.out.println();
        System.out.print("Matchmaking");
        showThreePointsAnimation();
    }

    @Override
    public void setOnSetup(List<String> leaderCardsID, int numberOfResource) {
        clearView();
        render(Board.getBoard().getPrintableMarketAndGrid());
        cliCommandListener.askSetupChoice(leaderCardsID, numberOfResource);
    }


    @Override
    public void setOnYourTurn() {
        clearView();
        CLI.render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
        CLI.render("Choose your action: (type " + CommandsEnum.MARKET + ", " + CommandsEnum.BUY + ", " + CommandsEnum.PRODUCTION + ", " + CommandsEnum.LEADER + " or " + CommandsEnum.SEE + ")");
        String answer = cliCommandListener.askFirstAction();
        CLI.clearView();
        switch (answer) {
            case "MARKET":
                CLI.render(Board.getBoard().getPrintableMarketAndGrid());
                if (!cliCommandListener.askMarketAction())
                    setOnYourTurn();
                break;
            case "BUY":
                CLI.render(Board.getBoard().getPrintableBuySceneOf(nickname));
                if (!cliCommandListener.askBuyAction())
                    setOnYourTurn();
                break;
            case "PRODUCTION":
                CLI.render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
                if (!cliCommandListener.askProductionAction())
                    setOnYourTurn();
                break;
            case "LEADER":
                CLI.render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
                if (!cliCommandListener.askLeaderAction())
                    setOnYourTurn();
                break;
            case "SEE":
                setOnSeeChoice();
        }
    }

    private void setOnSeeChoice() {
        String answer = cliCommandListener.askSeeChoice();
        switch (answer) {
            case "GRIDS": {
                CLI.render(Board.getBoard().getPrintableMarketAndGrid());
                setOnSeeChoice();
                break;
            }
            case "PLAYER": {
                setOnSeePlayerChoice();
                setOnSeeChoice();
                break;
            }
            default:
                clearView();
                setOnYourTurn();
        }
    }

    private void setOnSeePlayerChoice() {
        String playerToView = cliCommandListener.askSeePlayerChoice();
        render(Board.getBoard().getPrintablePersonalBoardOf(playerToView));
    }

    public static void showThreePointsAnimation() {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    @Override
    public void setOnWaitForYourTurn(String currentPlayer) {
        clearView();
        render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
        render("It's " + AnsiEnum.getPrettyNickname(currentPlayer) + " turn, wait for him to finish");
    }

    @Override
    public void setOnDevelopmentCardPlacement(String newCardID) {
        clearView();
        render(Board.getBoard().getPrintableCardPlacementSceneOf(nickname, newCardID));
        cliCommandListener.askCardPlacement();
    }

    @Override
    public void setOnResourcesPlacement() {
        clearView();
        render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
        cliCommandListener.askResourcePlacement();
    }

    @Override
    public void setOnTransformation(int numberOfTransformation, List<String> possibleTransformations) {
        clearView();
        cliCommandListener.askResourceTransformation(numberOfTransformation, possibleTransformations);
    }

    @Override
    public void setOnEndTurn() {
        clearView();
        render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
        if (cliCommandListener.askEndAction()) {
            //true if chosen action is a leaderAction
            if (!cliCommandListener.askLeaderAction()) {
                setOnEndTurn();
            }
        }
    }

    @Override
    public void setOnEndGame(String winner, Map<String, Integer> playersPoints) {
        System.out.println("The game is over!\nThe Winner is: " + winner + "\nThe points of all the players are:");
        playersPoints.forEach((player, victoryPoints) -> System.out.print(player + ":" + victoryPoints + "  "));
        //todo Implements play again / close app
    }
}
