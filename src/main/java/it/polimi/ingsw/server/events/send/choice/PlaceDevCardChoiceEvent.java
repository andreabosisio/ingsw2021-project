package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.utils.ServerParser;

public class PlaceDevCardChoiceEvent extends ChoiceEvent {
    private final String newCardID;

    public PlaceDevCardChoiceEvent(String nickname, DevelopmentCard newCard) {
        super(nickname, ServerParser.PLACE_DEV_CARD_TYPE);
        this.newCardID = newCard.getID();
    }
}
