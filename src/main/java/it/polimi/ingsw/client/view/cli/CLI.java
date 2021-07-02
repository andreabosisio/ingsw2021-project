package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.ClientApp;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.PersonalBoard;
import it.polimi.ingsw.client.network.NetworkHandler;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This class implements the Command Line Interface.
 */
public class CLI implements View {

    private String nickname;
    private boolean isPlaying = false;
    private boolean onlineGame = true;
    private NetworkHandler networkHandler;
    private String ip = ClientApp.DEFAULT_IP;
    private int port = ClientApp.DEFAULT_PORT;
    private final CLICommandListener cliCommandListener;

    /**
     * This class is used to play in CLI mode
     */
    public CLI() {
        clearView();
        render(AnsiUtilities.LOGO.getAsciiArt());

        boolean correctSettings = false;
        cliCommandListener = new CLICommandListener();
        askSettings();

        if (onlineGame) {
            while (!correctSettings) {
                try {
                    this.networkHandler = new NetworkHandler(ip, port, this);
                    correctSettings = true;
                } catch (IOException e) {
                    renderError("Could not connect to the server");
                    askIPAndPort();
                }
            }
        } else {
            this.networkHandler = new NetworkHandler(this);
        }

        startNetwork();
    }

    /**
     * This method is used to ask the player if he wants to play online or locally
     * If online it also asks for an ip and port to use
     */
    private void askSettings() {
        if (cliCommandListener.askGameMode().equals("ONLINE")) {
            onlineGame = true;
            if (cliCommandListener.askForNetworkSettingsChanges().equals("CHANGE")) {
                askIPAndPort();
            } else {
                ip = ClientApp.DEFAULT_IP;
                port = ClientApp.DEFAULT_PORT;
            }
        } else {
            onlineGame = false;
            render("Starting a Local Game...");
        }
    }

    /**
     * This method is used to ask the player which ip and port he wants to use
     */
    private void askIPAndPort() {
        ip = cliCommandListener.askIP();
        port = cliCommandListener.askPort();
        render("Network settings changed.");
    }

    /**
     * This method is used to set the name of the player using this view
     *
     * @param nickname nickname the user has chosen
     */
    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * This method is used to get the nickname of the owner of this view
     *
     * @return the owner nickname
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * This method is used to set the player owner of this view as currently doing his turn
     *
     * @param isPlaying true if player is currently playing
     */
    @Override
    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * This method returns the player playing state
     *
     * @return true if the player is currently performing his turn
     */
    @Override
    public boolean isThisClientTurn() {
        return isPlaying;
    }

    /**
     * This method is used to start the network used to communicate with the server
     * It also set the networkHandler as listener to the cliCommandListener
     */
    @Override
    public void startNetwork() {
        cliCommandListener.registerObservers(networkHandler);
        networkHandler.startNetwork();
    }

    /**
     * This method is used to cleanup the player terminal before a new scene is printed
     */
    public static void clearView() {
        for (int i = 0; i < 50; i++)
            System.out.println();
    }

    /**
     * This method is used to show the wait animation in the terminal
     */
    @Override
    public void showWaitAnimation() {
        showThreePointsAnimation();
    }

    /**
     * This method is used to print a specific printable scene on the terminal
     *
     * @param printable the scene to print
     */
    public static void render(Printable printable) {
        if (printable != null) {
            List<String> toPrint = printable.getPrintable();
            if (toPrint != null)
                toPrint.forEach(System.out::println);
        }
    }

    /**
     * This method is used to print an error message from the server in red on the terminal
     *
     * @param printable the error to print in red
     */
    public static void renderError(String printable) {
        System.out.println(AnsiUtilities.RED + printable + AnsiUtilities.RESET);
    }

    /**
     * This method is used to print a message on the terminal
     *
     * @param printable String containing the message you wish to print
     */
    public static void render(String printable) {
        System.out.println(printable);
    }

    /**
     * This message is used to print an info message from the server on the terminal
     *
     * @param info String containing the message to print
     */
    @Override
    public void printInfoMessage(String info) {
        if (info == null)
            return;
        render(info);
    }

    /**
     * This method is used to print an error message on the terminal followed by a three points animation
     *
     * @param error message to print in red
     */
    @Override
    public void printErrorMessage(String error) {
        if (error == null)
            return;
        renderError(error);
        CLI.showThreePointsAnimation();
    }

    /**
     * This method is used to set the view on the login phase
     */
    @Override
    public void setOnLogin() {
        showLoginScene();
        cliCommandListener.askCredentials();
    }

    /**
     * This method is used to set the view on the choose number of player phase
     *
     * @param payload the numbers available to chose
     */
    @Override
    public void setOnChooseNumberOfPlayers(String payload) {
        cliCommandListener.askNumberOfPlayers(payload);
    }

    /**
     * This method is show the login scene on the terminal
     */
    private void showLoginScene() {
        render(AnsiUtilities.LOGIN_SMALL.getAsciiArt());
    }

    /**
     * This method is used to set the view on the matchmaking phase
     */
    @Override
    public void setOnMatchMaking() {
        clearView();
        render(AnsiUtilities.LOGO.getAsciiArt());

        System.out.println();
        System.out.print("Matchmaking");
        showThreePointsAnimation();
    }

    /**
     * This method is used to set the view on the setup phase
     *
     * @param leaderCardsID    IDs the player can chose from as his leaderCards
     * @param numberOfResource number of resources the player can chose
     */
    @Override
    public void setOnSetup(List<String> leaderCardsID, int numberOfResource) {
        clearView();
        render(Board.getBoard().getPrintableMarketAndGrid());
        cliCommandListener.askSetupChoice(leaderCardsID, numberOfResource);
    }

    /**
     * This method is used to set the view on the start turn phase
     * It uses the command listener to ask which action the player wish to perform and call the appropriate method
     */
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

    /**
     * This method is used to set the view on the see phase
     * Its uses the cli command listener to ask the player what he wishes to see
     */
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

    /**
     * This method is used to set the view on the see player choice phase
     * It uses the cli command listener to ask the player which enemy he wishes to see and show his personal board to him
     */
    private void setOnSeePlayerChoice() {
        String playerToView = cliCommandListener.askSeePlayerChoice();
        render(Board.getBoard().getPrintablePersonalBoardOf(playerToView));
    }

    /**
     * This method is used to show the 3 points animation on the terminal
     */
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

    /**
     * This method is used to set the view on the wait for your turn phase
     */
    @Override
    public void setOnWaitForYourTurn(String currentPlayer) {
        clearView();
        render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
        render("It's " + AnsiUtilities.getPrettyNickname(currentPlayer) + " turn, wait for him to finish");
    }

    /**
     * This method is used to set the view on the development card placement phase
     *
     * @param newCardID card the player needs to place
     */
    @Override
    public void setOnDevelopmentCardPlacement(String newCardID) {
        clearView();
        render(Board.getBoard().getPrintableCardPlacementSceneOf(nickname, newCardID));
        cliCommandListener.askCardPlacement();
    }

    /**
     * This method is used to set the view on the place resources phase
     */
    @Override
    public void setOnResourcesPlacement() {
        clearView();
        render(Board.getBoard().getPrintablePersonalBoardOf(nickname));
        cliCommandListener.askResourcePlacement();
    }

    /**
     * This method is used to set the view on the transformation phase
     *
     * @param numberOfTransformation  number of resources the player needs to transform
     * @param possibleTransformations possible colors he can chose from
     */
    @Override
    public void setOnTransformation(int numberOfTransformation, List<String> possibleTransformations) {
        clearView();
        cliCommandListener.askResourceTransformation(numberOfTransformation, possibleTransformations);
    }

    /**
     * This method is used to set the view on the end turn phase
     */
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

    /**
     * This method is used to set the view on the end game view
     *
     * @param winner        winner of the game
     * @param playersPoints points of each player
     */
    @Override
    public void setOnEndGame(String winner, Map<String, Integer> playersPoints) {
        System.out.println("The game is over!\nThe Winner is: " + winner + "\nThe points of all the players are:");
        playersPoints.forEach((player, victoryPoints) -> System.out.print(player + ":" + victoryPoints + "  "));
    }

    @Override
    public void marketUpdate() {
    }

    @Override
    public void gridUpdate(String iD) {
    }

    @Override
    public void faithTracksUpdate() {
    }

    @Override
    public void personalBoardUpdate(PersonalBoard updatingPersonalBoard) {
        updatingPersonalBoard.updateCliScenes();
    }
}
