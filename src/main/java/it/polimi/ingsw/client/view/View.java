package it.polimi.ingsw.client.view;

import java.util.List;

public interface View {

    void start();

    void clearView();

    void graphicUpdate();

    void printInfoMessage(String info);

    void printErrorMessage(String error);

    void setOnLogin();

    void setOnChooseNumberOfPlayers(String payload);

    void setOnMatchMaking();

    void setOnSetup(List<String> leaderCardsID, int numberOfResource);

    void setOnGame();

}
