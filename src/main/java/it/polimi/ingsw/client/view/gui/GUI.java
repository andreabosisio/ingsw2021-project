package it.polimi.ingsw.client.view.gui;

import com.google.gson.Gson;
import it.polimi.ingsw.client.model.DevelopmentCardsGrid;
import it.polimi.ingsw.client.network.NetworkHandler;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.gui.controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GUI extends Application implements View {

    private NetworkHandler networkHandler;
    private String nickname;
    private boolean isPlaying = false;
    private final Map<String, GUICommandListener> guiCommandListeners = new HashMap<String, GUICommandListener>() {{
        put("loginController", new LoginController());
        put("chooseNumberController", new ChooseNumberController());
        //put("marketController", new MarketController(false));
        put("setupController",new SetupController());
        put("personalController",new PersonalController());
        put("endGameController",new EndGameController());
    }};
    private GUICommandListener currentGuiCommandListener;
    private static Scene scene;

    public void setGUI(String ip, int port) throws IOException {
        try {
            this.networkHandler = new NetworkHandler(ip, port, this);
            guiCommandListeners.values().forEach(guiCommandListener -> guiCommandListener.registerObservers(networkHandler));
            new Thread(this::startNetwork).start();
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    public void setNickname(String nickname) {
        PersonalController p = (PersonalController) guiCommandListeners.get("personalController");
        p.setNickname(nickname);
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
    public void graphicUpdate() {

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
        GUICommandListener nextGuiCommandListener = guiCommandListeners.get("loginController");
        setRoot("loginScene", nextGuiCommandListener);
        currentGuiCommandListener = nextGuiCommandListener;
    }

    @Override
    public void setOnChooseNumberOfPlayers(String payload) {
        GUICommandListener nextGuiCommandListener = guiCommandListeners.get("chooseNumberController");
        setRoot("chooseNumberScene", nextGuiCommandListener);
        currentGuiCommandListener = nextGuiCommandListener;
    }

    @Override
    public void setOnMatchMaking() {
        /*
        GUICommandListener nextGuiCommandListener = guiCommandListeners.get("loginController");
        setRoot("loginScene", nextGuiCommandListener);
        ((LoginController) nextGuiCommandListener).activateProgressIndicator();
        currentGuiCommandListener = nextGuiCommandListener;

         */
        //todo move this code in place were marketAction is selected
        /*
        GUICommandListener nextGuiCommandListener = guiCommandListeners.get("marketController");
        setRoot("marketScene", nextGuiCommandListener);
        currentGuiCommandListener = nextGuiCommandListener;
         */
    }

    @Override
    public void setOnSetup(List<String> leaderCardsID, int numberOfResource) {
        SetupController nextGuiCommandListener = (SetupController) guiCommandListeners.get("setupController");
        nextGuiCommandListener.initializeData(leaderCardsID,numberOfResource);
        setRoot("setupScene", nextGuiCommandListener);
        currentGuiCommandListener = nextGuiCommandListener;
    }

    @Override
    public void setOnYourTurn() {
        GUICommandListener nextGuiCommandListener = guiCommandListeners.get("personalController");
        setRoot("boardScene", nextGuiCommandListener,1800,900);
        currentGuiCommandListener = nextGuiCommandListener;
    }

    @Override
    public void setOnWaitForYourTurn(String currentPlayer) {

    }

    @Override
    public void setOnDevelopmentCardPlacement(String newCardID) {
        ((PersonalController) currentGuiCommandListener).showCardPlacementPopup(newCardID);
    }

    @Override
    public void setOnResourcesPlacement() {
        PersonalController p = (PersonalController) guiCommandListeners.get("personalController");
        p.activateSwaps();
    }

    @Override
    public void setOnTransformation(int numberOfTransformation, List<String> possibleTransformations) {
        ((PersonalController) currentGuiCommandListener).showTransformationPopup(numberOfTransformation, possibleTransformations);
    }

    @Override
    public void setOnEndTurn() {
    }

    @Override
    public void setOnEndGame(String winner, Map<String, Integer> playersPoints) {
        EndGameController nextGuiCommandListener = (EndGameController) guiCommandListeners.get("endGameController");
        setRoot("endGameScene", nextGuiCommandListener, 800, 800);
        currentGuiCommandListener = nextGuiCommandListener;
        nextGuiCommandListener.showEndGameEvent(winner, playersPoints);
    }

    @Override
    public void marketUpdate() {
        PersonalController personalController = (PersonalController) guiCommandListeners.get("personalController");
        personalController.marketUpdate();
    }

    @Override
    public void gridUpdate(String iD) {
        PersonalController personalController = (PersonalController) guiCommandListeners.get("personalController");
        personalController.gridUpdate(iD);
    }

    @Override
    public void faithTracksUpdate() {
        PersonalController personalController = (PersonalController) guiCommandListeners.get("personalController");
        personalController.faithTracksUpdate();
    }

    @Override
    public void productionBoardUpdate(String updatingNick){
        if(nickname.equals(updatingNick)){
            PersonalController personalController = (PersonalController) guiCommandListeners.get("personalController");
            personalController.productionBoardUpdate();
        }
    }

    @Override
    public void activeLeadersUpdate(String updatingNick){
        if(nickname.equals(updatingNick)){
            PersonalController personalController = (PersonalController) guiCommandListeners.get("personalController");
            personalController.activeLeadersUpdate();
        }
    }

    @Override
    public void warehouseUpdate(String updatingNick){
        if(nickname.equals(updatingNick)){
            PersonalController personalController = (PersonalController) guiCommandListeners.get("personalController");
            personalController.warehouseUpdate();
        }
    }


    @Override
    public void start(Stage stage) throws Exception {
        currentGuiCommandListener = new WelcomeController(this);
        scene = new Scene(Objects.requireNonNull(loadFXML("welcomeScene", currentGuiCommandListener)), 800, 800);
        stage.setTitle("Maestri del Rinascimento");
        stage.setScene(scene);
        stage.show();
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
    private void setRoot(String fxml, GUICommandListener guiCommandListener,double width,double height) {
        if (!guiCommandListener.equals(currentGuiCommandListener)) {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            scene.setRoot(loadFXML(fxml, guiCommandListener));
            //setWindow height
            scene.getWindow().setHeight(height);
            //setWindow y position
            scene.getWindow().setY((screenBounds.getHeight()-height)/2);
            //setWindow width
            scene.getWindow().setWidth(width);
            //setWindow x position
            scene.getWindow().setX((screenBounds.getWidth()-width)/2);
        }

    }

    public void show() {
        launch();
    }
}
