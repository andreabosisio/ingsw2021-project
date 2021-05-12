package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.events.send.choice.ChoiceEvent;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;

public class PlaceDevCardChoiceEvent extends ChoiceEvent {
    private final String newCardID;
    public PlaceDevCardChoiceEvent(String nickname, DevelopmentCard newCard) {
        super(nickname, "placeDevCard");
        this.newCardID = newCard.getID();
    }
}
