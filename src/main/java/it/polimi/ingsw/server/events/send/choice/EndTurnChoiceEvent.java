package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.utils.ServerParser;

public class EndTurnChoiceEvent extends ChoiceEvent {

    public EndTurnChoiceEvent(String nickname) {
        super(nickname, ServerParser.endTurnChoiceType);
    }
}
