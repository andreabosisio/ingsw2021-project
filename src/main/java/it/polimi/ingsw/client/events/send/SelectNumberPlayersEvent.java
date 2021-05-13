package it.polimi.ingsw.client.events.send;

public class SelectNumberPlayersEvent extends SendEvent {

    private final Integer size;

    public SelectNumberPlayersEvent(Integer size) {
        super("lobbyChoice");
        this.size = size;
    }
}
