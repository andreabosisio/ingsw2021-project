package it.polimi.ingsw.server.events.send.choice;

public class PlaceResourcesChoiceEvent extends ChoiceEvent {
    public PlaceResourcesChoiceEvent(String nickname) {
        super(nickname, "placeResources");
    }
}
