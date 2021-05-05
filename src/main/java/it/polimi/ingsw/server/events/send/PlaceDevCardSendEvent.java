package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.model.cards.DevelopmentCard;

public class PlaceDevCardSendEvent extends SendEvent{
    private final String newCardID;
    public PlaceDevCardSendEvent(String nickname, DevelopmentCard newCard) {
        super(nickname);
        this.newCardID = newCard.getID();
    }
}
