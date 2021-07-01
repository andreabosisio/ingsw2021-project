package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Represent the request of an End Turn Action.
 */
public class EndTurnChoiceEvent extends ChoiceEvent {

    public EndTurnChoiceEvent(String nickname) {
        super(nickname, ServerParser.END_TURN_CHOICE_TYPE);
    }
}
