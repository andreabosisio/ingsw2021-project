package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.List;

public class ModelInterface {
    List<Player> players = new ArrayList<Player>(){{
       add(new Player("Andrea"));
       add(new Player("Matteo"));
       add(new Player("Marco"));
    }};
    private TurnLogic turnLogic = new TurnLogic(players);


    public TurnLogic getTurnLogic() {
        return turnLogic;
    }
}
