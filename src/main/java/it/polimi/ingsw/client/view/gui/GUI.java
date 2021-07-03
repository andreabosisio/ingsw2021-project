package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.PersonalBoard;
import it.polimi.ingsw.client.network.NetworkHandler;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.gui.controllers.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Graphic User Interface implementation.
 */
public class GUI extends Application implements View {

    private static Scene scene;

    private String nickname;
    private boolean isPlaying = false;

    private NetworkHandler networkHandler;

    private final PersonalController personalController = new PersonalController();
    private GUICommandListener currentGuiCommandListener;
    private final Map<String, GUICommandListener> guiCommandListeners = new HashMap<>() {{
        put("loginController", new LoginController());
        put("chooseNumberController", new ChooseNumberController());
        put("setupController", new SetupController());
        put("endGameController", new EndGameController());
        put("matchmakingController", new MatchMakingController());
    }};

    private Map<String, Scene> scenes = new HashMap<>();

    /**
     * Set this GUI for a Remote Game and start the Network Connection.
     *
     * @param ip   IP Address of the Remote Server
     * @param port Port of the Remote server
     * @throws IOException if Socket creation failed
     */
    public void setGUI(String ip, int port) throws IOException {
        try {
            this.networkHandler = new NetworkHandler(ip, port, this);
            guiCommandListeners.values().forEach(guiCommandListener -> guiCommandListener.registerObservers(networkHandler));
            personalController.registerObservers(networkHandler);
            //loadScenes();
            new Thread(this::startNetwork).start();
        } catch (Exception e) {
            throw new IOException();
        }
    }

    /**
     * Set this GUI for a Local Game and start the Fake Network Connection.
     */
    public void setGUI() {
        this.networkHandler = new NetworkHandler(this);
        guiCommandListeners.values().forEach(guiCommandListener -> guiCommandListener.registerObservers(networkHandler));
        personalController.registerObservers(networkHandler);
        //loadScenes();
        new Thread(this::startNetwork).start();
    }
    
    /**
     * Set the nickname of the player owner of this view
     *
     * @param nickname nickname of the player
     */
    @Override
    public void setNickname(String nickname) {
        personalController.setNickname(nickname);
        this.nickname = nickname;
    }

    /**
     * Getter of nickname of the player using this view
     *
     * @return the player chosen nickname
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * Method used to start the network with the server
     */
    @Override
    public void startNetwork() {
        networkHandler.startNetwork();
    }

    @Override
    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    @Override
    public boolean isThisClientTurn() {
        return isPlaying;
    }

    /**
     * Print an infoMessage from the server
     *
     * @param info message to print
     */
    @Override
    public void printInfoMessage(String info) {
        Platform.runLater(() -> currentGuiCommandListener.printInfoMessage(info));
    }

    /**
     * Print an errorMessage from the server
     *
     * @param error message to print
     */
    @Override
    public void printErrorMessage(String error) {
        Platform.runLater(() -> currentGuiCommandListener.printErrorMessage(error));
    }

    /**
     * Method used to show a waiting animation
     */
    @Override
    public void showWaitAnimation() {

    }

    /**
     * Set the view in login phase
     */
    @Override
    public void setOnLogin() {
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = guiCommandListeners.get("loginController");
            setScene("loginScene", nextGuiCommandListener);
            currentGuiCommandListener = nextGuiCommandListener;
        });
    }

    /**
     * Set the view in chose number of players phase
     *
     * @param payload message containing the max and min values permitted by the server
     */
    @Override
    public void setOnChooseNumberOfPlayers(String payload) {
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = guiCommandListeners.get("chooseNumberController");
            setScene("chooseNumberScene", nextGuiCommandListener);
            currentGuiCommandListener = nextGuiCommandListener;
        });
    }

    /**
     * Set the view on the matchmaking phase
     */
    @Override
    public void setOnMatchMaking() {
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = guiCommandListeners.get("matchmakingController");
            setScene("matchMakingScene", nextGuiCommandListener);
            currentGuiCommandListener = nextGuiCommandListener;
        });
    }

    /**
     * Set the view on the setup phase
     *
     * @param leaderCardsID    IDs of the leaders the player can chose from
     * @param numberOfResource number of resources the player needs to chose
     */
    @Override
    public void setOnSetup(List<String> leaderCardsID, int numberOfResource) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            SetupController nextGuiCommandListener = (SetupController) guiCommandListeners.get("setupController");
            nextGuiCommandListener.initializeData(leaderCardsID, numberOfResource);
            setScene("setupScene", nextGuiCommandListener);
            currentGuiCommandListener = nextGuiCommandListener;
        });
    }

    /**
     * Set the player on the startTurn phase
     */
    @Override
    public void setOnYourTurn() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = personalController;
            setScene("boardScene", nextGuiCommandListener, 1800, 1000);
            currentGuiCommandListener = nextGuiCommandListener;
            personalController.activateBoard();
        });
    }

    /**
     * Set the player on the wait for other player turn to end
     *
     * @param currentPlayer player doing his turn
     */
    @Override
    public void setOnWaitForYourTurn(String currentPlayer) {
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = personalController;
            setScene("boardScene", nextGuiCommandListener, 1800, 1000);
            currentGuiCommandListener = nextGuiCommandListener;
            personalController.disableBoard();
            personalController.printInfoMessage("It's " + currentPlayer + " turn");
        });
    }

    /**
     * Set the player on the placeDevelopmentCArd phase
     *
     * @param newCardID ID of the card to place
     */
    @Override
    public void setOnDevelopmentCardPlacement(String newCardID) {
        Platform.runLater(() -> {
            personalController.showCardPlacementPopup(newCardID, scene);
            personalController.activateBoard();
        });
    }

    /**
     * Set the player on the resource placement phase
     */
    @Override
    public void setOnResourcesPlacement() {
        Platform.runLater(() -> {
            personalController.activateSwaps();
            personalController.activateBoard();
        });
    }

    /**
     * Set the player on the resource transformation phase
     *
     * @param numberOfTransformation  number of resources to transform
     * @param possibleTransformations possible colors to transform them into
     */
    @Override
    public void setOnTransformation(int numberOfTransformation, List<String> possibleTransformations) {
        Platform.runLater(() -> {
            personalController.showTransformationPopup(numberOfTransformation, possibleTransformations, scene);
            personalController.activateBoard();
        });
    }

    /**
     * Set the player on the endTurn phase
     */
    @Override
    public void setOnEndTurn() {
        Platform.runLater(() -> {
            personalController.activateBoard();
            personalController.activateEndButton();
        });
    }

    /**
     * Show the player the endGame scene
     *
     * @param winner        winning player
     * @param playersPoints all players points
     */
    @Override
    public void setOnEndGame(String winner, Map<String, Integer> playersPoints) {
        Platform.runLater(() -> {
            EndGameController nextGuiCommandListener = (EndGameController) guiCommandListeners.get("endGameController");
            setScene("endGameScene", nextGuiCommandListener, 600, 800);
            currentGuiCommandListener = nextGuiCommandListener;
            nextGuiCommandListener.showEndGameEvent(winner, playersPoints);
        });
    }

    /**
     * Update the market state
     */
    @Override
    public void marketUpdate() {
        Platform.runLater(personalController::marketUpdate);
    }

    /**
     * Update the development card grid state
     *
     * @param iD ID of the new card
     */
    @Override
    public void gridUpdate(String iD) {
        Platform.runLater(() -> personalController.gridUpdate(iD));
    }

    /**
     * Update the faithTracks state
     */
    @Override
    public void faithTracksUpdate() {
        Platform.runLater(personalController::faithTracksAndPopeTilesUpdate);
    }

    /**
     * Updates the personalBoard of one player
     *
     * @param updatingPersonalBoard personalBoard to update
     */
    @Override
    public void personalBoardUpdate(PersonalBoard updatingPersonalBoard) {
        activeLeadersUpdate(updatingPersonalBoard);
        productionBoardUpdate(updatingPersonalBoard);
        warehouseUpdate(updatingPersonalBoard);
    }

    /**
     * Updates the production Board of the Player
     *
     * @param updatingPersonalBoard personalBoard to update
     */
    private void productionBoardUpdate(PersonalBoard updatingPersonalBoard) {
        if (nickname.equals(updatingPersonalBoard.getNickname())) {
            Platform.runLater(personalController::productionBoardUpdate);
        }
    }

    /**
     * Updates the Active Leaders Card of the Player
     *
     * @param updatingPersonalBoard personalBoard to update
     */
    private void activeLeadersUpdate(PersonalBoard updatingPersonalBoard) {
        if (nickname.equals(updatingPersonalBoard.getNickname())) {
            Platform.runLater(personalController::activeLeadersUpdate);
        }
    }

    /**
     * Updates the Warehouse / StrongBox of the Player
     *
     * @param updatingPersonalBoard personalBoard to update
     */
    private void warehouseUpdate(PersonalBoard updatingPersonalBoard) {
        if (nickname.equals(updatingPersonalBoard.getNickname())) {
            Platform.runLater(personalController::warehouseUpdate);
        }
    }


    @Override
    public void start(Stage stage) {
        currentGuiCommandListener = new WelcomeController(this);
        scene = new Scene(Objects.requireNonNull(loadFXML("welcomeScene", currentGuiCommandListener)), 600, 800);
        stage.setTitle("Maestri del Rinascimento");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> {
            if (networkHandler != null) {
                networkHandler.close();
            }
            Platform.exit();
        });
    }

    /**
     * This method load the file Fxml
     *
     * @param fxmlFileName is the name of the file Fxml
     * @param guiCommandListener is the Controller to set
     * @return the Parent
     */
    private Parent loadFXML(String fxmlFileName, GUICommandListener guiCommandListener) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(guiCommandListener);
        fxmlLoader.setLocation(getClass().getResource("/fxmls/" + fxmlFileName + ".fxml"));
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Failed: " + fxmlFileName);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method Load the Fxml and set the Root of the Scene
     *
     * @param fxml               the name of the fxml to load
     * @param guiCommandListener the Controller to set
     */
    private void setScene(String fxml, GUICommandListener guiCommandListener) {
        if (!guiCommandListener.equals(currentGuiCommandListener)) {
            scene.setRoot(loadFXML(fxml, guiCommandListener));
        }
    }

    /**
     * This method Load the Fxml and set the Root of the Scene with a specified dimension
     *
     * @param fxml               the name of the fxml to load
     * @param guiCommandListener the Controller to set
     * @param width              is the width of the scene
     * @param height             is the height of the scene
     */
    private void setScene(String fxml, GUICommandListener guiCommandListener, double width, double height) {
        if (!guiCommandListener.equals(currentGuiCommandListener)) {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            scene.setRoot(loadFXML(fxml, guiCommandListener));
            //setWindow height
            scene.getWindow().setHeight(height);
            //setWindow y position
            scene.getWindow().setY((screenBounds.getHeight() - height) / 2);
            //setWindow width
            scene.getWindow().setWidth(width);
            //setWindow x position
            scene.getWindow().setX((screenBounds.getWidth() - width) / 2);
        }
    }

    /**
     * Starts the GUI.
     */
    public void show() {
        launch();
    }
}
