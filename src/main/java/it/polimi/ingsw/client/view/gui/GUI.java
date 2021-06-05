package it.polimi.ingsw.client.view.gui;

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
    }};
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
        currentGuiCommandListener.printInfoMessage(info);
    }

    @Override
    public void printErrorMessage(String error) {
        currentGuiCommandListener.printErrorMessage(error);
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
        //todo matchmaking scene?
    }

    @Override
    public void setOnSetup(List<String> leaderCardsID, int numberOfResource) {
        Platform.runLater(() -> {
            SetupController nextGuiCommandListener = (SetupController) guiCommandListeners.get("setupController");
            nextGuiCommandListener.initializeData(leaderCardsID, numberOfResource);
            setRoot("setupScene", nextGuiCommandListener);
            currentGuiCommandListener = nextGuiCommandListener;
        });
    }

    @Override
    public void setOnYourTurn() {
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
        });
    }

    @Override
    public void setOnDevelopmentCardPlacement(String newCardID) {
        Platform.runLater(() -> {
            personalController.showCardPlacementPopup(newCardID);
        });
    }

    @Override
    public void setOnResourcesPlacement() {
        Platform.runLater(personalController::activateSwaps);
    }

    @Override
    public void setOnTransformation(int numberOfTransformation, List<String> possibleTransformations) {
        Platform.runLater(() -> {
            personalController.showTransformationPopup(numberOfTransformation, possibleTransformations);
        });
    }

    @Override
    public void setOnEndTurn() {
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
        personalController.marketUpdate();
    }

    @Override
    public void gridUpdate(String iD) {
        personalController.gridUpdate(iD);
    }

    @Override
    public void faithTracksUpdate() {
        personalController.faithTracksAndPopeTilesUpdate();
    }

    @Override
    public void productionBoardUpdate(String updatingNick) {
        if (nickname.equals(updatingNick)) {
            personalController.productionBoardUpdate();
        }
    }

    @Override
    public void activeLeadersUpdate(String updatingNick) {
        if (nickname.equals(updatingNick)) {
            personalController.activeLeadersUpdate();
        }
    }

    @Override
    public void warehouseUpdate(String updatingNick) {
        if (nickname.equals(updatingNick)) {
            personalController.warehouseUpdate();
        }
    }


    @Override
    public void start(Stage stage) throws Exception {
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
