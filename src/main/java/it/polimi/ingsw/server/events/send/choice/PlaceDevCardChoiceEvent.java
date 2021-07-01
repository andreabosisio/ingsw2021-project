package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.utils.ServerParser;

public class PlaceDevCardChoiceEvent extends ChoiceEvent {
    private final String newCardID;

    public PlaceDevCardChoiceEvent(String nickname, DevelopmentCard newCard) {
        super(nickname, ServerParser.placeDevCardType);
        this.newCardID = newCard.getID();
    }
}
