package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Represent a request of a placement of a Development Card
 */
public class PlaceDevCardChoiceEvent extends ChoiceEvent {
    private final String newCardID;

    public PlaceDevCardChoiceEvent(String nickname, DevelopmentCard newCard) {
        super(nickname, ServerParser.PLACE_DEV_CARD_TYPE);
        this.newCardID = newCard.getID();
    }
}
