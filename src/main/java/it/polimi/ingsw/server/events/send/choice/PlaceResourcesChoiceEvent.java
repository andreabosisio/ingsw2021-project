package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.utils.ServerParser;

public class PlaceResourcesChoiceEvent extends ChoiceEvent {
    public PlaceResourcesChoiceEvent(String nickname) {
        super(nickname, ServerParser.placeResourcesType);
    }
}
