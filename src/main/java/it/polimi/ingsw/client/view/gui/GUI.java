package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.NetworkHandler;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GUI implements View {

    private NetworkHandler networkHandler;
    private GUICommandListener guiCommandListener;

    public GUI(String ip, int port) {
        try {
            this.networkHandler = new NetworkHandler(ip, port, this);
            startView();
        } catch (IOException e) {
            System.exit(0);
        }
    }

    @Override
    public void setNickname(String nickname) {

    }

    @Override
    public String getNickname() {
        return null;
    }

    @Override
    public void startView() {
        networkHandler.startNetwork();
    }


    @Override
    public void graphicUpdate() {

    }

    @Override
    public void printInfoMessage(String info) {

    }

    @Override
    public void printErrorMessage(String error) {

    }

    @Override
    public void setOnLogin() {

    }

    @Override
    public void setOnChooseNumberOfPlayers(String payload) {

    }

    @Override
    public void setOnMatchMaking() {

    }

    @Override
    public void setOnSetup(List<String> leaderCardsID, int numberOfResource) {

    }

    @Override
    public void setOnYourTurn() {

    }

    @Override
    public void setOnWaitForYourTurn(String currentPlayer) {

    }

    @Override
    public void setOnDevelopmentCardPlacement(String newCardID) {

    }

    @Override
    public void setOnResourcesPlacement() {

    }

    @Override
    public void setOnTransformation(int numberOfTransformation,List<String> possibleTransformations) {

    }

    @Override
    public void setOnEndTurn() {

    }

    @Override
    public void setOnEndGame(String winner,Map<String, Integer> playersPoints) {

    }


}
