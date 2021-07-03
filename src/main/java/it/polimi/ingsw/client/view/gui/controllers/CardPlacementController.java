package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.CardPlacementActionEvent;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.Locale;

/**
 * This class is used as the controller of the fxml scene:cardPlacement.fxml
 */
public class CardPlacementController extends GUICommandListener {
    private final String nickname;
    private String newCardID;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView newCard;
    @FXML
    private AnchorPane productionPane;

    /**
     * This is the controller used to place a card when bought
     *
     * @param nickname nickname of the player placing the card
     */
    public CardPlacementController(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Method used to save the ID of the card to place
     *
     * @param newCardID ID of the development card to place
     */
    public void setNewCardID(String newCardID) {
        this.newCardID = newCardID;
    }

    /**
     * Function used to initialize the fxml when loaded
     * It loads the cards owned by the player in the correct spots
     * It also assign the id for each card slot and the placeCardAction on a mousePressed
     */
    @FXML
    private void initialize() {
        GraphicUtilities.setImage(newCard, "/images/devCards/" + newCardID.toUpperCase(Locale.ROOT) + ".png");
        GraphicUtilities.populateProductionBoard(productionPane, nickname);
        int i = 1;
        for (Node slot : productionPane.getChildren()) {
            int slotButtonIndex = ((AnchorPane) slot).getChildren().size() - 1;
            Button slotButton = (Button) ((AnchorPane) slot).getChildren().get(slotButtonIndex);
            slotButton.setId(String.valueOf(i));
            slotButton.setOnMousePressed(mouseEvent -> placeCardAction(slotButton));
            i++;
        }
    }

    /**
     * This method is called when the player selected a development spot:
     * It construct the event to send to the server with all the needed data
     * It also closes the popup
     *
     * @param slot button representing the column chosen
     */
    private void placeCardAction(Node slot) {
        notifyObservers(new CardPlacementActionEvent(Integer.parseInt(slot.getId())));
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

}
