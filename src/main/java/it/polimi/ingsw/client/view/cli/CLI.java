package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.NetworkHandler;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.client.view.View;
import java.io.IOException;
import java.util.List;
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
        render(AnsiEnum.LOGO.getAsciiArt());
        cliCommandListener.registerObservers(networkHandler);
        networkHandler.startNetwork();
    }

    public static void clearView() {
        for (int i = 0; i < 30; i++)
            System.out.println(" ");
    }

    public static void render(Printable printable) {
        if (printable != null) {
            List<String> toPrint = printable.getPrintable();
            if (toPrint != null)
                toPrint.forEach(System.out::println);
        }
    }

    public static void render(String printable) {
        System.out.println(printable);
    }

    @Override
    public void graphicUpdate() {

    }

    @Override
    public void printInfoMessage(String info) {
        if(info == null)
            return;
        render(info);
    }

    @Override
    public void printErrorMessage(String error) {
        if(error == null)
            return;
        render(AnsiEnum.RED + error + AnsiEnum.RESET);
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
        System.out.print(AnsiEnum.WHITE_BRIGHT + "Your turn is starting" + AnsiEnum.RESET);
        CLI.showThreePointsAnimation();

        clearView();
        CLI.render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
        CLI.render("what do you wish to do?(market,buy,production,leader,see)");
        String answer = cliCommandListener.askFirstAction();
        CLI.clearView();
        switch (answer){
            //fixme clear view and print necessary items for everySwitch
            case "market":
                CLI.render(Board.getBoard().getPrintableMarketAndGrid());
                cliCommandListener.askMarketAction();
                break;
            //todo complete code for actions below
            case "buy":
                CLI.render(Board.getBoard().getPrintableBuySceneOf(nickname));
                cliCommandListener.askBuyAction();
                break;
            case "production":
                CLI.render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
                cliCommandListener.askProductionAction();
                break;
            case "leader":
                CLI.render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
                if(!cliCommandListener.askLeaderAction()){
                    setOnYourTurn();
                }
                break;
            case "see":
                setOnSeeChoice();
        }
    }
    private void setOnSeeChoice(){
        String answer = cliCommandListener.askSeeChoice();
        switch (answer){
            case "grids":{
                CLI.render(Board.getBoard().getPrintableMarketAndGrid());
                setOnSeeChoice();
                break;
            }
            case "player":{
                setOnSeePlayerChoice();
                setOnSeeChoice();
                break;
            }
            default:
                clearView();
                setOnYourTurn();
        }
    }
    private void setOnSeePlayerChoice(){
        String playerToView = cliCommandListener.askSeePlayerChoice();
        render(Board.getBoard().getPrintablePersonalBoardOf(playerToView));
    }

    protected static void showThreePointsAnimation() {
        for(int i = 0; i < 3; i++){
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
        System.out.println("It's " + currentPlayer +" turn, wait for him to finish");
    }

    @Override
    public void setOnPlaceDevCard(String newCardID) {
        clearView();
        //todo clearView and print card to place and personal production board
        //Board.getBoard().printPlaceCardScene(newCardId)
        cliCommandListener.askCardPlacement();
    }

    @Override
    public void setOnPlaceResources() {
        clearView();
        render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
        cliCommandListener.askResourcePlacement();
    }

    @Override
    public void setOnTransformation(int numberOfTransformation,List<String> possibleTransformations) {
        clearView();
        cliCommandListener.askResourceTransformation(numberOfTransformation, possibleTransformations);
    }

    @Override
    public void setOnEndTurn() {
        if(cliCommandListener.askEndAction()){
            //true if chosen action is a leaderAction
            if(!cliCommandListener.askLeaderAction()){
                setOnEndTurn();
            }
        }
    }
}
