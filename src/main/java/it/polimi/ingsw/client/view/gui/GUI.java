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
import java.util.*;

public class GUI extends Application implements View {

    private NetworkHandler networkHandler;
    private String nickname;
    private boolean isPlaying = false;
    private final Map<String, GUICommandListener> guiCommandListeners = new HashMap<>() {{
        put("loginController", new LoginController());
        put("chooseNumberController", new ChooseNumberController());
        put("setupController", new SetupController());
        put("endGameController", new EndGameController());
        put("matchmakingController", new MatchMakingController());
    }};
    private GUICommandListener welcome;
    private final PersonalController personalController = new PersonalController();
    private GUICommandListener currentGuiCommandListener;
    private static Scene scene;

    public void setGUI(String ip, int port) throws IOException {
        try {
            this.networkHandler = new NetworkHandler(ip, port, this);
            guiCommandListeners.values().forEach(guiCommandListener -> guiCommandListener.registerObservers(networkHandler));
            personalController.registerObservers(networkHandler);
            new Thread(this::startNetwork).start();
        } catch (Exception e) {
            throw new IOException();
        }
    }

    public void setGUI(){
        this.networkHandler = new NetworkHandler(this);
        guiCommandListeners.values().forEach(guiCommandListener -> guiCommandListener.registerObservers(networkHandler));
        personalController.registerObservers(networkHandler);
        new Thread(this::startNetwork).start();
    }

    @Override
    public void setNickname(String nickname) {
        personalController.setNickname(nickname);
        this.nickname = nickname;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

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

    @Override
    public void printInfoMessage(String info) {
        Platform.runLater(() -> currentGuiCommandListener.printInfoMessage(info));
    }

    @Override
    public void printErrorMessage(String error) {
        Platform.runLater(() -> currentGuiCommandListener.printErrorMessage(error));
    }

    @Override
    public void showWaitAnimation() {

    }

    @Override
    public void setOnLogin() {
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = guiCommandListeners.get("loginController");
            setRoot("loginScene", nextGuiCommandListener);
            currentGuiCommandListener = nextGuiCommandListener;
        });
    }

    @Override
    public void setOnChooseNumberOfPlayers(String payload) {
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = guiCommandListeners.get("chooseNumberController");
            setRoot("chooseNumberScene", nextGuiCommandListener);
            currentGuiCommandListener = nextGuiCommandListener;
        });
    }

    @Override
    public void setOnMatchMaking() {
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = guiCommandListeners.get("matchmakingController");
            setRoot("matchMakingScene", nextGuiCommandListener);
            currentGuiCommandListener = nextGuiCommandListener;
        });
    }

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
            setRoot("setupScene", nextGuiCommandListener);
            currentGuiCommandListener = nextGuiCommandListener;
        });
    }

    @Override
    public void setOnYourTurn() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = personalController;
            setRoot("boardScene", nextGuiCommandListener, 1800, 1000);
            currentGuiCommandListener = nextGuiCommandListener;
            personalController.activateBoard();
        });
    }

    @Override
    public void setOnWaitForYourTurn(String currentPlayer) {
        Platform.runLater(() -> {
            GUICommandListener nextGuiCommandListener = personalController;
            setRoot("boardScene", nextGuiCommandListener, 1800, 1000);
            currentGuiCommandListener = nextGuiCommandListener;
            personalController.disableBoard();
            personalController.printInfoMessage("It's " + currentPlayer + " turn");
        });
    }

    @Override
    public void setOnDevelopmentCardPlacement(String newCardID) {
        Platform.runLater(() -> {
            personalController.showCardPlacementPopup(newCardID,scene);
            personalController.activateBoard();
        });
    }

    @Override
    public void setOnResourcesPlacement() {
        Platform.runLater(()->{
            personalController.activateSwaps();
            personalController.activateBoard();
        });
    }

    @Override
    public void setOnTransformation(int numberOfTransformation, List<String> possibleTransformations) {
        Platform.runLater(() -> {
            personalController.showTransformationPopup(numberOfTransformation, possibleTransformations,scene);
            personalController.activateBoard();
        });
    }

    @Override
    public void setOnEndTurn() {
        Platform.runLater(()->{
            personalController.activateBoard();
            personalController.activateEndButton();
        });
    }



    @Override
    public void setOnEndGame(String winner, Map<String, Integer> playersPoints) {
        Platform.runLater(() -> {
            EndGameController nextGuiCommandListener = (EndGameController) guiCommandListeners.get("endGameController");
            setRoot("endGameScene", nextGuiCommandListener, 600, 800);
            currentGuiCommandListener = nextGuiCommandListener;
            nextGuiCommandListener.showEndGameEvent(winner, playersPoints);
        });
    }

    @Override
    public void marketUpdate() {
        Platform.runLater(personalController::marketUpdate);
    }

    @Override
    public void gridUpdate(String iD) {
        Platform.runLater(() -> personalController.gridUpdate(iD));
    }

    @Override
    public void faithTracksUpdate() {
        Platform.runLater(personalController::faithTracksAndPopeTilesUpdate);
    }

    @Override
    public void personalBoardUpdate(PersonalBoard updatingPersonalBoard) {
        activeLeadersUpdate(updatingPersonalBoard);
        productionBoardUpdate(updatingPersonalBoard);
        warehouseUpdate(updatingPersonalBoard);
    }

    private void productionBoardUpdate(PersonalBoard updatingPersonalBoard) {
        if (nickname.equals(updatingPersonalBoard.getNickname())) {
            Platform.runLater(personalController::productionBoardUpdate);
        }
    }

    private void activeLeadersUpdate(PersonalBoard updatingPersonalBoard) {
        if (nickname.equals(updatingPersonalBoard.getNickname())) {
            Platform.runLater(personalController::activeLeadersUpdate);
        }
    }

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
            try {
                Thread.sleep(5000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            Platform.exit();
        });
    }

    private Parent loadFXML(String fxmlFileName, GUICommandListener guiCommandListener) {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/" + fxmlFileName + ".fxml"));
        fxmlLoader.setController(guiCommandListener);
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setRoot(String fxml, GUICommandListener guiCommandListener) {
        if (!guiCommandListener.equals(currentGuiCommandListener)) {
            scene.setRoot(loadFXML(fxml, guiCommandListener));
        }
    }

    private void setRoot(String fxml, GUICommandListener guiCommandListener, double width, double height) {
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

    public void show() {
        launch();
    }
}
