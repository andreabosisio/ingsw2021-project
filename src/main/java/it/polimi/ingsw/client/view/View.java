package it.polimi.ingsw.client.view;

import java.util.List;

public interface View {

    void setNickname(String nickname);
    String getNickname();
    void startView();
    void graphicUpdate();
    void printInfoMessage(String info);
    void printErrorMessage(String error);
    void setOnLogin();
    void setOnChooseNumberOfPlayers(String payload);
    void setOnMatchMaking();
    void setOnSetup(List<String> leaderCardsID, int numberOfResource);
    void setOnYourTurn();
    void setOnNotYourTurn(String currentPlayer);
    void setOnPlaceDevCard(String newCardID);
    void setOnPlaceResources();
    void setOnTransformation(int numberOfTransformation,List<String> possibleTransformations);
    void setOnEndTurn();

}
