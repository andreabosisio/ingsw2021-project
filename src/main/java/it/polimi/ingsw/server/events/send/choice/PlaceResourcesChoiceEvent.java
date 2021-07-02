package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Represent a request of a resource placement
 */
public class PlaceResourcesChoiceEvent extends ChoiceEvent {
    public PlaceResourcesChoiceEvent(String nickname) {
        super(nickname, ServerParser.PLACE_RESOURCES_TYPE);
    }
}
