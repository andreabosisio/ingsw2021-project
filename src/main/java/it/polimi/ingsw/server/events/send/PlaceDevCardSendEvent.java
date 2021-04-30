package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.model.cards.DevelopmentCard;

public class PlaceDevCardSendEvent extends SendEvent{
    private final DevelopmentCard newCardID;
    public PlaceDevCardSendEvent(String nickname, DevelopmentCard newCardID) {
        super(nickname);
        this.newCardID = newCardID;
    }
}
