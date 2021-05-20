package it.polimi.ingsw.server.events.send.choice;

public class EndTurnChoiceEvent extends ChoiceEvent{

    public EndTurnChoiceEvent(String nickname) {
        super(nickname, "endTurnChoice");
    }
}
